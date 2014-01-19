package com.ghostofpq.kulkan.server.game;

import com.ghostofpq.kulkan.commons.Node;
import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.commons.Tree;
import com.ghostofpq.kulkan.entities.battlefield.BattleSceneState;
import com.ghostofpq.kulkan.entities.battlefield.Battlefield;
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
import com.ghostofpq.kulkan.server.authentication.AuthenticationManager;
import com.ghostofpq.kulkan.server.database.controller.UserController;
import com.ghostofpq.kulkan.server.database.model.User;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Game {

    private static final String CLIENT_QUEUE_NAME_BASE = "/client/";
    private static final String GAME_SERVER_QUEUE_NAME_BASE = "/server/game/";
    private String hostIp;
    private Integer hostPort;
    private AuthenticationManager authenticationManager;
    private GameManager gameManager;
    private UserController userController;
    private BattleSceneState state;
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

    public Game(Battlefield battlefield, List<Player> playerList, String gameID, AuthenticationManager authenticationManager, UserController userController, GameManager gameManager, String hostIp, int hostPort) {
        this.battlefield = battlefield;
        this.playerList = playerList;

        this.authenticationManager = authenticationManager;
        this.gameManager = gameManager;
        this.userController = userController;

        for (Player player : playerList) {
            for (GameCharacter gameCharacter : player.getTeam()) {
                gameCharacter.initChar();
            }
        }
        this.gameID = gameID;
        state = BattleSceneState.DEPLOY_POSITION;
        keyTokenPlayerMap = new HashMap<String, Player>();
        keyTokenPlayerNumberMap = new HashMap<String, Integer>();
        playerChannelMap = new HashMap<Player, String>();
        this.hostIp = hostIp;
        this.hostPort = hostPort;
        initConnections();
        sendDeployMessage();
    }

    private void initConnections() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(hostIp);
            factory.setPort(hostPort);
            connection = factory.newConnection();
            channelGameIn = connection.createChannel();
            String queueNameIn = new StringBuilder().append(GAME_SERVER_QUEUE_NAME_BASE).append(gameID).toString();
            channelGameIn.queueDeclare(queueNameIn, false, false, false, null);
            gameConsumer = new QueueingConsumer(channelGameIn);
            channelGameIn.basicConsume(queueNameIn, true, gameConsumer);
            log.debug(" [-] OPENING QUEUE : {}", queueNameIn);
            QueueingConsumer.Delivery delivery = gameConsumer.nextDelivery(1);
            while (null != delivery) {
                delivery = gameConsumer.nextDelivery(1);
            }
            channelGameOut = connection.createChannel();
            for (Player player : playerList) {
                String playerKey = userController.getTokenKeyForUsername(player.getPseudo());
                keyTokenPlayerMap.put(playerKey, player);
                String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(playerKey).toString();
                log.debug(" [-] OPENING QUEUE : {}", queueName);
                channelGameOut.queueDeclare(queueName, false, false, false, null);
                playerChannelMap.put(player, queueName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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

    private void sendDeployMessage() {
        for (Player player : playerList) {
            List<GameCharacter> characterList = player.getTeam();
            MessageDeploymentStart messageDeploymentStart = new MessageDeploymentStart(characterList, playerList.indexOf(player));
            String playerKey = userController.getTokenKeyForUsername(player.getPseudo());
            keyTokenPlayerNumberMap.put(playerKey, playerList.indexOf(player));
            sendMessageToPlayer(player, messageDeploymentStart);
        }
    }

    private void sendToAll(Message message) {
        for (Player player : playerList) {
            sendMessageToPlayer(player, message);
        }
    }

    private void sendMessageToPlayer(Player player, Message message) {
        try {
            log.debug(" [S] SENDING {} TO {}", message.getType(), playerChannelMap.get(player));
            channelGameOut.basicPublish("", playerChannelMap.get(player), null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToChannel(String tokenKey, Message message) {
        try {
            String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(tokenKey).toString();
            log.debug(" [S] SENDING {} TO {}", message.getType(), queueName);
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
                log.error(" [X] NO CHAR READY, TICK HOURGLASS");
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
            player.getGameCharacter(gameCharacter).setPosition(gameCharacter.getPosition());
            player.getGameCharacter(gameCharacter).setHeadingAngle(gameCharacter.getHeadingAngle());
        }

        if (deployIsComplete()) {
            log.debug(" [S] DEPLOYMENT IS COMPLETE");
            completeDeployment();
            newTurn();
        }
    }

    private void manageMessageCharRequestToMove(ClientMessage message) {
        MessagePositionToMoveRequest messagePositionToMoveRequest = (MessagePositionToMoveRequest) message;
        GameCharacter characterToMove = messagePositionToMoveRequest.getCharacter();
        if (characterToMove.equals(currentCharToPlay)) {
            log.debug(" [C] {} REQUESTS POSSIBLE POSITIONS TO MOVE", characterToMove.getName());
            Tree<Position> possiblePositionsToMoveTree = getPossiblePositionsToMoveTree(characterToMove);
            List<Position> possiblePositionsToMove = possiblePositionsToMoveTree.getAllElements();
            possiblePositionsToMove.remove(getCharacterPosition(characterToMove).plusYNew(-1));

            MessagePositionToMoveResponse messagePositionToMoveResponse = new MessagePositionToMoveResponse(possiblePositionsToMove);
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
            Position positionToMove = messageCharacterActionMove.getPositionToMove();
            Position currentPosition = getCharacterPosition(characterToMove).plusYNew(-1);
            if (!currentPosition.equals(positionToMove)) {
                log.debug(" [C] {} MOVES TO {}", characterToMove.getName(), positionToMove.toString());
                Tree<Position> possiblePositionsToMoveTree2 = getPossiblePositionsToMoveTree(characterToMove);
                List<Node<Position>> nodeList = possiblePositionsToMoveTree2.find(positionToMove);
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
            newTurn();
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
            List<Position> possiblePositionsToUseCapacity = battlefield.getPossiblePositionsToAttack(characterPosition, rangeToUse);

            MessageCharacterPositionToUseCapacityResponse messageCharacterPositionToUseCapacityResponse = new MessageCharacterPositionToUseCapacityResponse(possiblePositionsToUseCapacity);
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
            List<Position> areaOfEffect = battlefield.getPossiblePositionsToAttack(messageCapacityAOERequest.getPosition(), rangeToUse);

            MessageCapacityAOEResponse messageCapacityAOEResponse = new MessageCapacityAOEResponse(areaOfEffect);
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
            List<Position> areaOfEffect = battlefield.getPossiblePositionsToAttack(positionToUse, rangeToUse);
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
                        int magicArmor = (targetedChar.getAggregatedSecondaryCharacteristics().getMagicResist() - character.getAggregatedSecondaryCharacteristics().getMagicPenetration());
                        double ratio = 100 / (100 - magicArmor);
                        double estimatedDamage = ratio * character.getMagicalDamage() * 10;
                        int damage = (int) Math.floor(estimatedDamage);
                        gameCharacterDamageMap.put(targetedChar, damage);
                        targetedChar.addHealthPoint(-damage);
                        totalDamage += damage;
                    }
                    MessageCapacityFireball messageCapacityFireball = new MessageCapacityFireball(character, gameCharacterDamageMap, positionToUse, areaOfEffect, move.getManaCost());
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
    }

    public void receiveMessage() {
        try {
            QueueingConsumer.Delivery delivery = gameConsumer.nextDelivery(1);
            if (null != delivery) {
                Message rawMessage = Message.loadFromBytes(delivery.getBody());
                ClientMessage message = (ClientMessage) rawMessage;
                if (null != message) {
                    log.debug("RECEIVED MESSAGE [{}]", message.getType());
                    switch (message.getType()) {
                        case FINISH_DEPLOYMENT:
                            manageMessageFinishDeployment(message);
                            break;
                        case CHARACTER_POSITION_TO_MOVE_REQUEST:
                            if (messageIsExpected(message)) {
                                manageMessageCharRequestToMove(message);
                            } else {
                                log.error(" [X] UNEXPECTED PLAYER TO PLAY {}", message.getKeyToken());
                            }
                            break;
                        case CHARACTER_POSITION_TO_ATTACK_REQUEST:
                            if (messageIsExpected(message)) {
                                manageMessageCharRequestToAttack(message);
                            } else {
                                log.error(" [X] UNEXPECTED PLAYER TO PLAY {}", message.getKeyToken());
                            }
                            break;
                        case CHARACTER_POSITION_TO_USE_CAPACITY_REQUEST:
                            if (messageIsExpected(message)) {
                                manageMessageCharRequestToUseCapacity(message);
                            } else {
                                log.error(" [X] UNEXPECTED PLAYER TO PLAY {}", message.getKeyToken());
                            }
                            break;
                        case CHARACTER_CAPACITY_AOE_REQUEST: {
                            if (messageIsExpected(message)) {
                                manageMessageCapacityAOERequest(message);
                            } else {
                                log.error(" [X] UNEXPECTED PLAYER TO PLAY {}", message.getKeyToken());
                            }
                            break;
                        }
                        case CHARACTER_ACTION_MOVE:
                            if (messageIsExpected(message)) {
                                manageMessageCharMoves(message);
                            } else {
                                log.error(" [X] UNEXPECTED PLAYER TO PLAY {}", message.getKeyToken());
                            }
                            break;
                        case CHARACTER_ACTION_ATTACK:
                            if (messageIsExpected(message)) {
                                manageMessageCharAttacks(message);
                            } else {
                                log.error(" [X] UNEXPECTED PLAYER TO PLAY {}", message.getKeyToken());
                            }
                            break;
                        case CHARACTER_ACTION_END_TURN:
                            if (messageIsExpected(message)) {
                                manageMessageCharEndTurn(message);
                            } else {
                                log.error(" [X] UNEXPECTED PLAYER TO PLAY {}", message.getKeyToken());
                            }
                            break;

                        case CHARACTER_ACTION_CAPACITY_USE:
                            if (messageIsExpected(message)) {
                                manageMessageCharCapacityUse(message);
                            } else {
                                log.error(" [X] UNEXPECTED PLAYER TO PLAY {}", message.getKeyToken());
                            }
                            break;
                        default:
                            log.error(" [X] UNEXPECTED MESSAGE : {}", message.getType());
                            break;
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean messageIsExpected(ClientMessage message) {
        boolean result;
        int receivedPlayerNumber = keyTokenPlayerNumberMap.get(message.getKeyToken());
        String playerKey = userController.getTokenKeyForUsername(playerToPlay.getPseudo());
        int expectedPlayerNumber = keyTokenPlayerNumberMap.get(playerKey);
        if (receivedPlayerNumber == expectedPlayerNumber) {
            result = true;
        } else {
            log.warn("message from player n°{} , expecting from player n°{}", receivedPlayerNumber, expectedPlayerNumber);
            result = false;
        }
        return result;
    }

    private GameCharacter getGameCharacterAtPosition(Position position) {
        GameCharacter result = null;
        for (Player player : playerList) {
            for (GameCharacter character : player.getTeam()) {
                Position footPositionOfChar = character.getPosition().plusYNew(-1);
                if (footPositionOfChar.equals(position)) {
                    result = character;
                    break;
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
        List<Position> result = battlefield.getPossiblePositionsToAttack(characterPosition, range);
        result.remove(characterPosition);
        return result;
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
        Player winnerPlayer = getWinnerPlayer();
        if (null != winnerPlayer) {
            for (Player player : playerList) {
                if (winnerPlayer == player) {
                    MessageGameEnd messageGameEnd = new MessageGameEnd(true);
                    sendMessageToPlayer(player, messageGameEnd);
                } else {
                    MessageGameEnd messageGameEnd = new MessageGameEnd(false);
                    sendMessageToPlayer(player, messageGameEnd);
                }
                User updatedUser = userController.updateGameCharacters(player);
                MessagePlayerUpdate messagePlayerUpdate = new MessagePlayerUpdate(updatedUser.toPlayer());
                sendMessageToPlayer(player, messagePlayerUpdate);
            }
            closeGame();
        } else {
            GameCharacter charToPlay = getNextCharToPlay();
            Player playerToPlay = getPlayerForCharacter(charToPlay);

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

    }

    private void closeGame() {
        log.debug("[S] GAME IS OVER");
        closeConnections();
        gameManager.closeGame(this.gameID);
        this.battlefield = null;
        this.playerList = null;
        this.gameID = null;
        state = null;
        keyTokenPlayerMap = null;
        keyTokenPlayerNumberMap = null;
        playerChannelMap = null;
    }

    private Player getWinnerPlayer() {
        int numberOfPlayerAlive = 0;
        Player result = null;
        Player playerAlive = null;
        for (Player player : playerList) {
            if (player.isAlive()) {
                numberOfPlayerAlive++;
                playerAlive = player;
            }
        }
        if (numberOfPlayerAlive == 1) {
            result = playerAlive;
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
}
