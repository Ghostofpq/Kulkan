package com.ghostofpq.kulkan.server.game;

import com.ghostofpq.kulkan.commons.Node;
import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.commons.Tree;
import com.ghostofpq.kulkan.entities.battlefield.Battlefield;
import com.ghostofpq.kulkan.entities.character.Alteration;
import com.ghostofpq.kulkan.entities.character.CombatCalculator;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.job.capacity.Move;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.game.*;
import com.ghostofpq.kulkan.entities.messages.game.capacity.MessageCapacityFireball;
import com.ghostofpq.kulkan.entities.messages.user.MessagePlayerUpdate;
import com.ghostofpq.kulkan.entities.utils.Range;
import com.ghostofpq.kulkan.entities.utils.RangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
public class Game implements Runnable {

    private static final String CLIENT_QUEUE_NAME_BASE = "client/";
    private static final String GAME_SERVER_QUEUE_NAME_BASE = "server/game/";
    private String hostIp;
    private Integer hostPort;
    private GameManager gameManager;
    private Battlefield battlefield;
    private List<Player> playerList;
    private Map<Player, String> playerChannelMap;
    private Map<String, Player> keyTokenPlayerMap;
    private Map<String, Integer> keyTokenPlayerNumberMap;
    private Channel channelGameIn;
    private Channel channelGameOut;
    private QueueingConsumer gameConsumer;
    private String gameID;
    private Player playerToPlay;
    private GameCharacter currentCharToPlay;
    private Connection connection;
    private boolean deploymentIsOver;


    public Game(Battlefield battlefield, String gameID, Map<String, Player> keyTokenPlayerMap, GameManager gameManager, String hostIp, int hostPort) {
        this.battlefield = battlefield;
        this.playerList = new ArrayList<Player>();

        this.gameManager = gameManager;

        for (Player player : playerList) {
            for (GameCharacter gameCharacter : player.getTeam()) {
                gameCharacter.initChar();
            }
        }
        this.gameID = gameID;
        this.keyTokenPlayerMap = keyTokenPlayerMap;
        this.keyTokenPlayerNumberMap = new HashMap<String, Integer>();
        this.playerChannelMap = new HashMap<Player, String>();
        this.hostIp = hostIp;
        this.hostPort = hostPort;
        this.deploymentIsOver = false;
    }

    public void initConnections() throws IOException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostIp);
        factory.setPort(hostPort);
        log.debug("{}:{}", hostIp, hostPort);
        String queueNameIn = new StringBuilder().append(GAME_SERVER_QUEUE_NAME_BASE).append(gameID).toString();

        connection = factory.newConnection();
        channelGameIn = connection.createChannel();
        channelGameIn.queueDeclare(queueNameIn, false, false, false, null);
        channelGameIn.basicQos(1);
        gameConsumer = new QueueingConsumer(channelGameIn);
        channelGameIn.basicConsume(queueNameIn, true, gameConsumer);

        while (gameConsumer.nextDelivery(0) != null) {
            // purge
        }

        channelGameOut = connection.createChannel();
        for (String keyToken : keyTokenPlayerMap.keySet()) {
            Player player = keyTokenPlayerMap.get(keyToken);
            playerList.add(player);

            String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(keyToken).toString();
            log.debug(" [-] OPENING QUEUE : {}", queueName);
            channelGameOut.queueDeclare(queueName, false, false, false, null);
            playerChannelMap.put(player, queueName);
            keyTokenPlayerNumberMap.put(keyToken, playerList.indexOf(player));
        }
    }

    private void closeConnections() {
        try {
            channelGameIn.close();
            channelGameOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendDeployMessageToAll() {
        for (Player player : playerList) {
            sendDeployMessageTo(player);
        }
    }

    private void sendDeployMessageTo(Player player) {
        for (GameCharacter gameCharacter : player.getTeam()) {
            gameCharacter.setPosition(null);
        }
        List<GameCharacter> characterList = player.getTeam();
        MessageDeploymentStart messageDeploymentStart = new MessageDeploymentStart(characterList, playerList.indexOf(player));
        sendMessageToPlayer(player, messageDeploymentStart);
    }

    private void sendToAll(Message message) {
        for (Player player : playerList) {
            sendMessageToPlayer(player, message);
        }
    }

    private void sendMessageToPlayer(Player player, Message message) {
        try {
            log.debug("SENDING to {} : {}", playerChannelMap.get(player), message.toString());
            channelGameOut.basicPublish("", playerChannelMap.get(player), null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToChannel(String tokenKey, Message message) {
        try {
            String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(tokenKey).toString();
            log.debug("SENDING to {} : {}", queueName, message.toString());
            channelGameOut.basicPublish("", queueName, null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private GameCharacter getNextCharToPlay() {
        GameCharacter result = null;
        while (null == result) {
            List<GameCharacter> readyGameCharactersList = new ArrayList<GameCharacter>();
            for (Player player : playerList) {
                for (GameCharacter gameCharacter : player.getTeam()) {
                    if (gameCharacter.isReadyToPlay()) {
                        readyGameCharactersList.add(gameCharacter);
                    }
                }
            }
            if (readyGameCharactersList.isEmpty()) {
                for (Player player : playerList) {
                    for (GameCharacter gameCharacter : player.getTeam()) {
                        gameCharacter.tickHourglass();
                    }
                }
            } else {
                log.debug(" [-] {} CHAR ARE TO PLAY : ", readyGameCharactersList.size());
                result = readyGameCharactersList.get(0);

                for (GameCharacter gameCharacter : readyGameCharactersList) {
                    log.debug("{} M:{} A:{}", gameCharacter.getName(), gameCharacter.hasMoved(), gameCharacter.hasActed());
                    if (result.getHourglass() > gameCharacter.getHourglass()) {
                        result = gameCharacter;
                    }
                }
                readyGameCharactersList.remove(result);
                result.setReadyToPlay(false);
            }

        }
        log.debug(" [-] CHAR TO PLAY : {}", result.getName());
        return result;
    }

    private Player getPlayerForCharacter(GameCharacter gameCharacter) {
        Player result = null;

        for (Player player : playerList) {
            if (player.getTeam().contains(gameCharacter)) {
                result = player;
                break;
            }
        }
        if (null == result) {
            log.error(" [X] CHAR TO PLAY BELONGS TO NO ONE");
        }
        return result;
    }

    private GameCharacter getEquivalentCharacter(GameCharacter gameCharacter) {
        GameCharacter result = null;
        for (Player player : playerList) {
            if (player.getTeam().contains(gameCharacter)) {
                for (GameCharacter character : player.getTeam()) {
                    if (character.equals(gameCharacter)) {
                        result = character;
                    }
                }
            }
        }
        return result;
    }

    private Position getCharacterPosition(GameCharacter gameCharacter) {
        Position result = null;
        for (Player player : playerList) {
            if (player.getTeam().contains(gameCharacter)) {
                result = player.getGameCharacter(gameCharacter).getPosition();
            }
        }
        return result;
    }

    private void manageMessageFinishDeployment(ClientMessage message) {
        MessageDeploymentFinishedForPlayer messageDeploymentFinishedForPlayer = (MessageDeploymentFinishedForPlayer) message;
        log.debug(" [P] FINISH DEPLOYMENT MESSAGE FROM {}", messageDeploymentFinishedForPlayer.getKeyToken());

        for (GameCharacter gameCharacter : messageDeploymentFinishedForPlayer.getCharactersList()) {
            log.debug("PLACING -> {} : {}", gameCharacter.getName(), gameCharacter.getPosition().toString());
            Player player = playerList.get(messageDeploymentFinishedForPlayer.getPlayerNumber());
            Position position = gameCharacter.getPosition();
            if (!battlefield.positionIsOccupied(position) && getGameCharacterAtPosition(position.plusYNew(-1)) == null) {
                player.getGameCharacter(gameCharacter).setPosition(position);
                player.getGameCharacter(gameCharacter).setHeadingAngle(gameCharacter.getHeadingAngle());
            } else {
                log.error("ERROR on deployment of {}:{} on {}", player.getPseudo(), gameCharacter.getName(), gameCharacter.getPosition());
                sendDeployMessageTo(player);
                break;
            }
        }
        if (deployIsComplete()) {
            log.debug(" [S] DEPLOYMENT IS COMPLETE");
            completeDeployment();
            deploymentIsOver = true;
        }

    }

    private void manageMessageCharRequestToMove(ClientMessage message) {
        MessagePositionToMoveRequest messagePositionToMoveRequest = (MessagePositionToMoveRequest) message;
        GameCharacter characterToMove = messagePositionToMoveRequest.getCharacter();
        if (characterToMove.equals(currentCharToPlay)) {
            log.debug(" [C] {} REQUESTS POSSIBLE POSITIONS TO MOVE", characterToMove.getName());
            Tree<Position> possiblePositionsToMoveTree = getPossiblePositionsToMoveTree(characterToMove);
            Set<Position> possiblePositionsToMove = possiblePositionsToMoveTree.getAllElements();
            possiblePositionsToMove.remove(getCharacterPosition(characterToMove).plusYNew(-1));

            List<Position> possiblePositionsToMoveList = new ArrayList<Position>(possiblePositionsToMove);
            MessagePositionToMoveResponse messagePositionToMoveResponse = new MessagePositionToMoveResponse(possiblePositionsToMoveList);
            sendMessageToChannel(messagePositionToMoveRequest.getKeyToken(), messagePositionToMoveResponse);
        } else {
            log.error(" [X] UNEXPECTED CHAR TO PLAY");
        }
    }

    private void manageMessageCharRequestToAttack(ClientMessage message) {
        MessagePositionToAttackRequest messagePositionToAttackRequest = (MessagePositionToAttackRequest) message;
        GameCharacter character = messagePositionToAttackRequest.getCharacter();
        if (character.equals(currentCharToPlay)) {
            log.debug(" [C] {} REQUESTS POSSIBLE POSITIONS TO ATTACK", character.getName());
            List<Position> possiblePositionsToAttack = getPossiblePositionsToAttack(character);
            MessagePositionToAttackResponse messagePositionToAttackResponse = new MessagePositionToAttackResponse(possiblePositionsToAttack);
            sendMessageToChannel(messagePositionToAttackRequest.getKeyToken(), messagePositionToAttackResponse);
        } else {
            log.error(" [X] UNEXPECTED CHAR TO PLAY");
        }
    }

    private void manageMessageCharMoves(ClientMessage message) {
        MessageCharacterActionMove messageCharacterActionMove = (MessageCharacterActionMove) message;
        GameCharacter characterToMove = getEquivalentCharacter(messageCharacterActionMove.getCharacter());
        if (characterToMove.equals(currentCharToPlay)) {
            if (!currentCharToPlay.hasMoved()) {
                Position positionToMove = messageCharacterActionMove.getPositionToMove();
                Position currentPosition = getCharacterPosition(characterToMove).plusYNew(-1);
                if (!currentPosition.equals(positionToMove)) {
                    log.debug(" [C] {} MOVES TO {}", characterToMove.getName(), positionToMove.toString());
                    Tree<Position> possiblePositionsToMoveTree = getPossiblePositionsToMoveTree(characterToMove);
                    List<Node<Position>> nodeList = possiblePositionsToMoveTree.find(positionToMove);
                    if (!nodeList.isEmpty()) {
                        characterToMove.setHasMoved(true);
                        // the position is on the floor, set the char position to position +Y1
                        Player player = playerList.get(messageCharacterActionMove.getPlayerNumber());
                        player.getGameCharacter(characterToMove).setPosition(positionToMove.plusYNew(1));
                        List<Position> path = nodeList.get(0).getPathFromTop();
                        MessageCharacterMoves messageCharacterMoves = new MessageCharacterMoves(characterToMove, path);
                        sendToAll(messageCharacterMoves);
                        MessageCharacterToPlay messageCharacterToPlay = new MessageCharacterToPlay(characterToMove, positionToMove);
                        sendMessageToChannel(messageCharacterActionMove.getKeyToken(), messageCharacterToPlay);
                    } else {
                        log.error(" [X] NOT VALID POSITION TO MOVE : {}", positionToMove.toString());
                        MessageCharacterToPlay messageCharacterToPlay = new MessageCharacterToPlay(characterToMove, currentPosition);
                        sendMessageToChannel(messageCharacterActionMove.getKeyToken(), messageCharacterToPlay);
                    }
                } else {
                    log.error(" [X] NOT MOVING : {}", positionToMove.toString());
                    MessageCharacterToPlay messageCharacterToPlay = new MessageCharacterToPlay(characterToMove, currentPosition);
                    sendMessageToChannel(messageCharacterActionMove.getKeyToken(), messageCharacterToPlay);
                }
            } else {
                log.error(" [X] CHARACTER HAS ALREADY MOVE THIS TURN");
                MessageCharacterToPlay messageCharacterToPlay = new MessageCharacterToPlay(currentCharToPlay, currentCharToPlay.getPosition());
                sendMessageToChannel(messageCharacterActionMove.getKeyToken(), messageCharacterToPlay);
            }
        } else {
            log.error(" [X] UNEXPECTED CHAR TO PLAY");
        }
    }

    private void manageMessageCharAttacks(ClientMessage message) {
        MessageCharacterActionAttack messageCharacterActionAttack = (MessageCharacterActionAttack) message;
        GameCharacter characterWhoAttacks = getEquivalentCharacter(messageCharacterActionAttack.getCharacter());
        Position characterWhoAttacksPosition = characterWhoAttacks.getPosition().plusYNew(-1);
        if (characterWhoAttacks.equals(currentCharToPlay)) {
            Position positionToAttack = messageCharacterActionAttack.getPositionToAttack();
            if (!positionToAttack.equals(characterWhoAttacksPosition)) {
                List<Position> possiblePositionsToAttack = getPossiblePositionsToAttack(characterWhoAttacks);
                if (possiblePositionsToAttack.contains(positionToAttack)) {
                    GameCharacter characterAttacked = getGameCharacterAtPosition(positionToAttack);
                    if (null != characterAttacked) {
                        if (characterAttacked.isAlive()) {
                            if (!characterWhoAttacks.hasActed()) {

                                log.debug(" [C] {} at {} ATTACKS {} at {}", characterWhoAttacks.getName(), characterWhoAttacksPosition.toString(), characterAttacked.getName(), positionToAttack.toString());
                                CombatCalculator combatCalculator = new CombatCalculator(characterWhoAttacks, characterWhoAttacksPosition, characterAttacked, positionToAttack);
                                double hitRoll = Math.random();
                                log.debug("rolled a {} to hit", hitRoll);
                                boolean hit = false;
                                boolean crit = false;
                                int damages = 0;

                                if (Math.floor(hitRoll * 100) <= combatCalculator.getChanceToHit()) {
                                    double critRoll = Math.random();
                                    log.debug("rolled a {} to crit", critRoll);

                                    if (Math.floor(critRoll * 100) <= combatCalculator.getChanceToCriticalHit()) {
                                        damages = combatCalculator.getEstimatedDamage() * 2;
                                        crit = true;
                                    } else {
                                        damages = combatCalculator.getEstimatedDamage();
                                    }

                                    log.debug("{} takes {} damages from {}", characterAttacked.getName(), damages, characterWhoAttacks.getName());
                                    characterAttacked.addHealthPoint(-damages);
                                    log.debug("{} : {}/{}", characterAttacked.getName(), characterAttacked.getCurrentHealthPoint(), characterAttacked.getMaxHealthPoint());
                                    hit = true;
                                } else {
                                    log.debug("missed");
                                }
                                MessageCharacterAttacks messageCharacterAttacks = new MessageCharacterAttacks(characterWhoAttacks, characterAttacked, hit, damages, crit);
                                sendToAll(messageCharacterAttacks);

                                characterWhoAttacks.setHasActed(true);
                                characterWhoAttacks.gainXp(damages);
                                characterWhoAttacks.gainJobPoints(5);
                                MessageCharacterGainsXP messageCharacterGainsXP = new MessageCharacterGainsXP(characterWhoAttacks, damages, 5);
                                sendToAll(messageCharacterGainsXP);

                                MessageCharacterToPlay messageCharacterToPlay = new MessageCharacterToPlay(characterWhoAttacks, characterWhoAttacksPosition);
                                sendMessageToChannel(messageCharacterActionAttack.getKeyToken(), messageCharacterToPlay);
                            } else {
                                log.error(" [X] CHARACTER HAS ALREADY ACTED THIS TURN");
                                MessageCharacterToPlay messageCharacterToPlay = new MessageCharacterToPlay(characterWhoAttacks, characterWhoAttacksPosition);
                                sendMessageToChannel(messageCharacterActionAttack.getKeyToken(), messageCharacterToPlay);
                            }
                        } else {
                            log.error(" [X] INVALID TARGET (ALREADY DEAD)");
                            MessageCharacterToPlay messageCharacterToPlay = new MessageCharacterToPlay(characterWhoAttacks, characterWhoAttacksPosition);
                            sendMessageToChannel(messageCharacterActionAttack.getKeyToken(), messageCharacterToPlay);
                        }
                    } else {
                        log.error(" [X] INVALID TARGET (EMPTY)");
                        MessageCharacterToPlay messageCharacterToPlay = new MessageCharacterToPlay(characterWhoAttacks, characterWhoAttacksPosition);
                        sendMessageToChannel(messageCharacterActionAttack.getKeyToken(), messageCharacterToPlay);
                    }
                } else {
                    log.error(" [X] INVALID TARGET (SELF)");
                    MessageCharacterToPlay messageCharacterToPlay = new MessageCharacterToPlay(characterWhoAttacks, characterWhoAttacksPosition);
                    sendMessageToChannel(messageCharacterActionAttack.getKeyToken(), messageCharacterToPlay);
                }
            } else {
                log.error(" [X] INVALID POSITION TO ATTACK : {}", positionToAttack.toString());
                MessageCharacterToPlay messageCharacterToPlay = new MessageCharacterToPlay(characterWhoAttacks, characterWhoAttacksPosition);
                sendMessageToChannel(messageCharacterActionAttack.getKeyToken(), messageCharacterToPlay);
            }
        } else {
            log.error(" [X] UNEXPECTED CHAR TO PLAY");
        }
    }

    private void manageMessageCharEndTurn(ClientMessage message) {
        MessageCharacterEndTurn messageCharacterEndTurn = (MessageCharacterEndTurn) message;
        GameCharacter character = getEquivalentCharacter(messageCharacterEndTurn.getCharacter());
        if (character.equals(currentCharToPlay)) {
            log.debug(" [C] END TURN FOR {}", character.getName());
            character.setHeadingAngle(messageCharacterEndTurn.getCharacter().getHeadingAngle());
            currentCharToPlay = null;
            playerToPlay = null;
        } else {
            log.error(" [X] UNEXPECTED CHAR TO PLAY");
        }
    }

    private void manageMessageCharRequestToUseCapacity(ClientMessage message) {
        MessageCharacterPositionToUseCapacityRequest characterPositionToUseCapacityRequest = (MessageCharacterPositionToUseCapacityRequest) message;
        GameCharacter character = getEquivalentCharacter(characterPositionToUseCapacityRequest.getCharacter());
        if (character.equals(currentCharToPlay)) {
            Move move = characterPositionToUseCapacityRequest.getSelectedMove();
            Range rangeToUse = null;
            switch (move.getMoveRangeType()) {
                case RANGE:
                    rangeToUse = move.getRange();
                    break;
                case RANGE_AOE:
                    rangeToUse = move.getRange();
                    break;
                case SELF:
                    rangeToUse = new Range(RangeType.CROSS, 0, 0);
                    break;
                case WEAPON:
                    rangeToUse = character.getRange();
                    break;

            }
            Position characterPosition = getCharacterPosition(character).plusYNew(-1);
            Set<Position> possiblePositionsToUseCapacity = battlefield.getPossiblePositionsToAttack(characterPosition, rangeToUse);

            List<Position> possiblePositionsToUseCapacityList = new ArrayList<Position>(possiblePositionsToUseCapacity);
            MessageCharacterPositionToUseCapacityResponse messageCharacterPositionToUseCapacityResponse = new MessageCharacterPositionToUseCapacityResponse(possiblePositionsToUseCapacityList);
            sendMessageToChannel(characterPositionToUseCapacityRequest.getKeyToken(), messageCharacterPositionToUseCapacityResponse);
        } else {
            log.error(" [X] UNEXPECTED CHAR TO PLAY");
        }
    }

    private void manageMessageCapacityAOERequest(ClientMessage message) {
        MessageCapacityAOERequest messageCapacityAOERequest = (MessageCapacityAOERequest) message;
        GameCharacter character = getEquivalentCharacter(messageCapacityAOERequest.getCharacter());
        if (character.equals(currentCharToPlay)) {
            Move move = messageCapacityAOERequest.getSelectedMove();
            Range rangeToUse = null;
            switch (move.getMoveRangeType()) {
                case RANGE:
                    rangeToUse = new Range(RangeType.CROSS, 0, 0);
                    break;
                case RANGE_AOE:
                    rangeToUse = move.getAreaOfEffect();
                    break;
                case SELF:
                    rangeToUse = new Range(RangeType.CROSS, 0, 0);
                    break;
                case WEAPON:
                    rangeToUse = new Range(RangeType.CROSS, 0, 0);
                    break;
                case WEAPON_AOE:
                    rangeToUse = move.getAreaOfEffect();
                    break;
            }
            Set<Position> areaOfEffect = battlefield.getPossiblePositionsToAttack(messageCapacityAOERequest.getPosition(), rangeToUse);

            List<Position> areaOfEffectList = new ArrayList<Position>(areaOfEffect);
            MessageCapacityAOEResponse messageCapacityAOEResponse = new MessageCapacityAOEResponse(areaOfEffectList);
            sendMessageToChannel(messageCapacityAOERequest.getKeyToken(), messageCapacityAOEResponse);
        } else {
            log.error(" [X] UNEXPECTED CHAR TO PLAY");
        }
    }

    private void manageMessageCharCapacityUse(ClientMessage message) {
        MessageCharacterActionCapacity messageCharacterActionCapacity = (MessageCharacterActionCapacity) message;
        GameCharacter character = getEquivalentCharacter(messageCharacterActionCapacity.getCharacter());
        if (character.equals(currentCharToPlay)) {

            Position positionToUse = messageCharacterActionCapacity.getPositionToUseCapacity();
            Move move = messageCharacterActionCapacity.getMove();
            if (move.getManaCost() <= currentCharToPlay.getCurrentManaPoint()) {
                currentCharToPlay.addManaPoint(-move.getManaCost());
                MessageCharacterGainsMP messageCharacterGainsMP = new MessageCharacterGainsMP(character, -move.getManaCost());
                sendToAll(messageCharacterGainsMP);

                Range rangeToUse = null;
                switch (move.getMoveRangeType()) {
                    case RANGE:
                        rangeToUse = new Range(RangeType.CROSS, 0, 0);
                        break;
                    case RANGE_AOE:
                        rangeToUse = move.getAreaOfEffect();
                        break;
                    case SELF:
                        rangeToUse = new Range(RangeType.CROSS, 0, 0);
                        break;
                    case WEAPON:
                        rangeToUse = new Range(RangeType.CROSS, 0, 0);
                        break;
                    case WEAPON_AOE:
                        rangeToUse = move.getAreaOfEffect();
                        break;
                }
                Set<Position> areaOfEffect = battlefield.getPossiblePositionsToAttack(positionToUse, rangeToUse);
                List<Position> areaOfEffectList = new ArrayList<Position>(areaOfEffect);
                int totalDamage = 0;
                switch (move.getMoveName()) {
                    case FIREBALL:
                        List<GameCharacter> targets = new ArrayList<GameCharacter>();
                        Map<GameCharacter, Integer> gameCharacterDamageMap = new HashMap<GameCharacter, Integer>();
                        for (Position position : areaOfEffect) {
                            GameCharacter target = getGameCharacterAtPosition(position);
                            if (null != target) {
                                targets.add(target);
                            }
                        }
                        for (GameCharacter targetedChar : targets) {
                            int magicArmor = (targetedChar.getAggregatedCharacteristics().getMagicResist() - character.getAggregatedCharacteristics().getMagicPenetration());
                            double ratio = 100.0 / (100.0 - (double) magicArmor);
                            double estimatedDamage = ratio * (double) character.getMagicalDamage();
                            int damage = (int) Math.floor(estimatedDamage);
                            gameCharacterDamageMap.put(targetedChar, damage);
                            targetedChar.addHealthPoint(-damage);
                            totalDamage += damage;
                        }
                        MessageCapacityFireball messageCapacityFireball = new MessageCapacityFireball(character, gameCharacterDamageMap, positionToUse, areaOfEffectList, move.getManaCost());
                        sendToAll(messageCapacityFireball);
                        break;
                    case EMPOWER:
                        // SET STATUS STRENGTH 10
                        break;
                    case DASH:
                        break;
                    case KNOCKUP:
                        break;
                }


                character.setHasActed(true);
                character.gainXp(totalDamage);
                character.gainJobPoints(5);
                MessageCharacterGainsXP messageCharacterGainsXP = new MessageCharacterGainsXP(character, totalDamage, 5);
                sendToAll(messageCharacterGainsXP);

                Position characterPosition = character.getPosition().plusYNew(-1);
                MessageCharacterToPlay messageCharacterToPlay = new MessageCharacterToPlay(character, characterPosition);
                sendMessageToChannel(messageCharacterActionCapacity.getKeyToken(), messageCharacterToPlay);
            } else {
                log.error(" [X] UNEXPECTED CHAR TO PLAY");
            }
        } else {
            log.error(" [X] Not enough MANA");
        }
    }

    public void receiveMessage() throws InterruptedException {
        log.debug("Waiting for Message");
        QueueingConsumer.Delivery delivery = gameConsumer.nextDelivery();
        log.debug(" ! ");
        if (null != delivery) {
            Message rawMessage = Message.loadFromBytes(delivery.getBody());
            ClientMessage message = (ClientMessage) rawMessage;
            if (null != message) {
                log.debug("RECEIVED MESSAGE [{}]", message.toString());
                switch (message.getType()) {
                    case FINISH_DEPLOYMENT:
                        manageMessageFinishDeployment(message);
                        break;
                    case CHARACTER_POSITION_TO_MOVE_REQUEST:
                        manageMessageCharRequestToMove(message);
                        break;
                    case CHARACTER_POSITION_TO_ATTACK_REQUEST:
                        manageMessageCharRequestToAttack(message);
                        break;
                    case CHARACTER_POSITION_TO_USE_CAPACITY_REQUEST:
                        manageMessageCharRequestToUseCapacity(message);
                        break;
                    case CHARACTER_CAPACITY_AOE_REQUEST:
                        manageMessageCapacityAOERequest(message);
                        break;
                    case CHARACTER_ACTION_MOVE:
                        manageMessageCharMoves(message);
                        break;
                    case CHARACTER_ACTION_ATTACK:
                        manageMessageCharAttacks(message);
                        break;
                    case CHARACTER_ACTION_END_TURN:
                        manageMessageCharEndTurn(message);
                        break;
                    case CHARACTER_ACTION_CAPACITY_USE:
                        manageMessageCharCapacityUse(message);
                        break;
                    default:
                        log.error(" [X] UNEXPECTED MESSAGE : {}", message.getType());
                        break;
                }
            }
        }
    }


    private GameCharacter getGameCharacterAtPosition(Position position) {
        GameCharacter result = null;
        for (Player player : playerList) {
            for (GameCharacter character : player.getTeam()) {
                if (character.getPosition() != null) {
                    Position footPositionOfChar = character.getPosition().plusYNew(-1);
                    if (footPositionOfChar.equals(position)) {
                        result = character;
                        break;
                    }
                }
            }
        }
        return result;
    }

    private Tree<Position> getPossiblePositionsToMoveTree(GameCharacter gameCharacter) {
        Position characterPosition = getCharacterPosition(gameCharacter).plusYNew(-1);
        Tree<Position> result = battlefield.getPositionTree(characterPosition, 3, 2, 1, false, PointOfView.NORTH);
        for (Player player : playerList) {
            for (GameCharacter character : player.getTeam()) {
                if (!character.equals(gameCharacter)) {
                    Position footPositionOfChar = character.getPosition().plusYNew(-1);
                    if (result.contains(footPositionOfChar)) {
                        result.remove(footPositionOfChar);
                    }
                }
            }
        }
        return result;
    }

    private List<Position> getPossiblePositionsToAttack(GameCharacter gameCharacter) {
        Position characterPosition = getCharacterPosition(gameCharacter).plusYNew(-1);
        Range range = gameCharacter.getRange();
        Set<Position> result = battlefield.getPossiblePositionsToAttack(characterPosition, range);
        List<Position> possiblePositionsToAttack = new ArrayList<Position>(result);
        possiblePositionsToAttack.remove(characterPosition);
        return possiblePositionsToAttack;
    }

    private void completeDeployment() {
        for (Player player : playerList) {
            log.debug("player {}", playerList.indexOf(player));
            for (GameCharacter gameCharacter : player.getTeam()) {
                log.debug("-> {} : {}", gameCharacter.getName(), gameCharacter.getPosition().toString());
            }
            MessageDeploymentPositionsOfPlayer messageDeploymentPositionsOfPlayer =
                    new MessageDeploymentPositionsOfPlayer(player.getTeam(), playerList.indexOf(player));
            for (Player playerToNotify : playerList) {
                if (player != playerToNotify) {
                    sendMessageToPlayer(playerToNotify, messageDeploymentPositionsOfPlayer);
                }
            }
        }
    }

    private void newTurn() {
        log.debug("[New Turn]");
        GameCharacter charToPlay = getNextCharToPlay();
        Player playerToPlay = getPlayerForCharacter(charToPlay);
        log.debug("charToPlay : {}", charToPlay);
        log.debug("playerToPlay : {}", playerToPlay);

        if (charToPlay.getCharacteristics().getHealthRegeneration() != 0) {
            log.debug("{} gains {} HP ", charToPlay.getName(), charToPlay.getCharacteristics().getHealthRegeneration());
            charToPlay.addHealthPoint(charToPlay.getCharacteristics().getHealthRegeneration());
            MessageCharacterGainsHP messageCharacterGainsHP = new MessageCharacterGainsHP(charToPlay, charToPlay.getCharacteristics().getHealthRegeneration());
            sendToAll(messageCharacterGainsHP);
        }

        if (charToPlay.getCharacteristics().getManaRegeneration() != 0) {
            log.debug("{} gains {} MP ", charToPlay.getName(), charToPlay.getCharacteristics().getManaRegeneration());
            charToPlay.addHealthPoint(charToPlay.getCharacteristics().getManaRegeneration());
            MessageCharacterGainsMP messageCharacterGainsMP = new MessageCharacterGainsMP(charToPlay, charToPlay.getCharacteristics().getManaRegeneration());
            sendToAll(messageCharacterGainsMP);
        }

        Iterator<Alteration> alterationsIterator = charToPlay.getAlterations().iterator();
        while (alterationsIterator.hasNext()) {
            Alteration alteration = alterationsIterator.next();
            if (alteration.isActive()) {
                if (alteration.getCharacteristics().getHealthRegeneration() != 0) {
                    log.debug("{} gains {} HP due to {}", charToPlay.getName(), alteration.getCharacteristics().getHealthRegeneration(), alteration.getName());
                    charToPlay.addHealthPoint(alteration.getCharacteristics().getHealthRegeneration());
                    MessageCharacterGainsHP messageCharacterGainsHP = new MessageCharacterGainsHP(charToPlay, alteration.getCharacteristics().getHealthRegeneration());
                    sendToAll(messageCharacterGainsHP);
                } else if (alteration.getCharacteristics().getManaRegeneration() != 0) {
                    log.debug("{} gains {} MP due to {}", charToPlay.getName(), alteration.getCharacteristics().getManaRegeneration(), alteration.getName());
                    charToPlay.addManaPoint(alteration.getCharacteristics().getManaRegeneration());
                    MessageCharacterGainsMP messageCharacterGainsMP = new MessageCharacterGainsMP(charToPlay, alteration.getCharacteristics().getManaRegeneration());
                    sendToAll(messageCharacterGainsMP);
                }
            } else {
                alterationsIterator.remove();
                MessageCharacterAlteration messageCharacterAlteration = new MessageCharacterAlteration(charToPlay, alteration, false);
                sendToAll(messageCharacterAlteration);
            }
        }

        charToPlay.updateAggregatedCharacteristics();

        List<GameCharacter> allGameCharacters = new ArrayList<GameCharacter>();
        for (Player player : playerList) {
            allGameCharacters.addAll(player.getTeam());
        }
        MessageUpdateCharacters messageUpdateCharacters = new MessageUpdateCharacters(allGameCharacters);

        for (Player player : playerList) {
            sendMessageToPlayer(player, messageUpdateCharacters);
        }
        Position footPositionOfChar = charToPlay.getPosition().plusYNew(-1);
        MessageCharacterToPlay messageCharacterToPlay = new MessageCharacterToPlay(charToPlay, footPositionOfChar);
        sendMessageToPlayer(playerToPlay, messageCharacterToPlay);

        this.playerToPlay = playerToPlay;
        this.currentCharToPlay = charToPlay;
    }

    private void closeGame() {
        log.debug("[S] GAME IS OVER");
        closeConnections();
        this.battlefield = null;
        this.gameID = null;
        playerList = null;
        keyTokenPlayerMap = null;
        keyTokenPlayerNumberMap = null;
        playerChannelMap = null;
        gameManager.closeGame(this.gameID);
    }

    public void setPlayerIsDisconnected(String tokenKey) {
        Player player = keyTokenPlayerMap.get(tokenKey);
        keyTokenPlayerMap.remove(tokenKey);
        playerList.remove(player);
        playerChannelMap.remove(player);
        keyTokenPlayerNumberMap.remove(tokenKey);
    }

    private Player getWinnerPlayer() {
        int numberOfPlayerAlive = 0;
        Player result = null;
        Player playerAlive = null;
        for (Player player : playerList) {
            if (player.isAlive()) {
                numberOfPlayerAlive++;
                playerAlive = player;
                if (numberOfPlayerAlive > 1) {
                    break;
                }
            }
        }
        if (numberOfPlayerAlive == 1) {
            result = playerAlive;
            log.debug("{} is the only one alive");
        }
        return result;
    }

    private boolean deployIsComplete() {
        boolean result = true;
        for (Player player : playerList) {
            for (GameCharacter gameCharacter : player.getTeam()) {
                if (null == gameCharacter.getPosition()) {
                    result = false;
                    log.debug("[x] DEPLOYMENT NOT FINISHED FOR {}", player.getPseudo());
                    break;
                }
            }
        }
        return result;
    }

    public void run() {
        log.debug("keyTokenPlayerMap : {}", keyTokenPlayerMap);
        log.debug("keyTokenPlayerNumberMap : {}", keyTokenPlayerNumberMap);
        log.debug("playerChannelMap : {}", playerChannelMap);
        log.debug("playerList : {}", playerList);
        sendDeployMessageToAll();
        Player winnerPlayer = getWinnerPlayer();
        while (null == winnerPlayer) {
            if (deploymentIsOver && null == currentCharToPlay) {
                newTurn();
            }
            try {
                receiveMessage();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            winnerPlayer = getWinnerPlayer();
        }
        for (Player player : playerList) {
            if (winnerPlayer == player) {
                MessageGameEnd messageGameEnd = new MessageGameEnd(true);
                sendMessageToPlayer(player, messageGameEnd);
            } else {
                MessageGameEnd messageGameEnd = new MessageGameEnd(false);
                sendMessageToPlayer(player, messageGameEnd);
            }
        }
        List<Player> updatedPlayerList = gameManager.updatePlayers(playerList);
        for (Player player : updatedPlayerList) {
            MessagePlayerUpdate messagePlayerUpdate = new MessagePlayerUpdate(player);
            sendMessageToPlayer(player, messagePlayerUpdate);
        }
        closeGame();
    }

}
