package com.ghostofpq.kulkan.server.game;

import com.ghostofpq.kulkan.commons.Node;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.commons.Tree;
import com.ghostofpq.kulkan.entities.battlefield.BattleSceneState;
import com.ghostofpq.kulkan.entities.battlefield.Battlefield;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.messages.*;
import com.ghostofpq.kulkan.server.Server;
import com.ghostofpq.kulkan.server.authentification.AuthenticationManager;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Game {
    private final String CLIENT_QUEUE_NAME_BASE = "/client/";
    private final String GAME_SERVER_QUEUE_NAME_BASE = "/server/game/";
    private BattleSceneState state;
    private Battlefield battlefield;
    private List<Player> playerList;
    private Map<Player, Map<GameCharacter, Position>> characterPositionMap;
    private Map<Player, String> playerChannelMap;
    private Map<String, Player> keyTokenPlayerMap;
    private Map<String, Integer> keyTokenPlayerNumberMap;
    private List<GameCharacter> readyToPlay;
    private Channel channelGameIn;
    private Channel channelGameOut;
    private QueueingConsumer gameConsumer;
    private String gameID;
    private Player playerToPlay;
    private GameCharacter currentCharToPlay;


    public Game(Battlefield battlefield, List<Player> playerList, String gameID) {
        this.battlefield = battlefield;
        this.playerList = playerList;
        this.gameID = gameID;
        state = BattleSceneState.DEPLOY_POSITION;
        characterPositionMap = new HashMap<Player, Map<GameCharacter, Position>>();
        keyTokenPlayerMap = new HashMap<String, Player>();
        keyTokenPlayerNumberMap = new HashMap<String, Integer>();
        playerChannelMap = new HashMap<Player, String>();
        readyToPlay = new ArrayList<GameCharacter>();
        initConnections();
        sendDeployMessage();
    }

    private void initConnections() {
        try {
            channelGameIn = Server.getInstance().getConnection().createChannel();
            String queueNameIn = new StringBuilder().append(GAME_SERVER_QUEUE_NAME_BASE).append(gameID).toString();
            channelGameIn.queueDeclare(queueNameIn, false, false, false, null);
            gameConsumer = new QueueingConsumer(channelGameIn);
            channelGameIn.basicConsume(queueNameIn, true, gameConsumer);
            log.debug(" [-] OPENING QUEUE : {}", queueNameIn);
            QueueingConsumer.Delivery delivery = gameConsumer.nextDelivery(1);
            while (null != delivery) {
                delivery = gameConsumer.nextDelivery(1);
            }
            channelGameOut = Server.getInstance().getConnection().createChannel();
            for (Player player : playerList) {
                String playerKey = AuthenticationManager.getInstance().getTokenKeyFor(player.getPseudo());
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

    private void sendDeployMessage() {
        for (Player player : playerList) {
            List<GameCharacter> characterList = player.getTeam().getTeam();
            MessageDeploymentStart messageDeploymentStart = new MessageDeploymentStart(characterList, playerList.indexOf(player));
            String playerKey = AuthenticationManager.getInstance().getTokenKeyFor(player.getPseudo());
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
            log.debug(" SENDING {} TO {}", message.getType(), playerChannelMap.get(player));
            channelGameOut.basicPublish("", playerChannelMap.get(player), null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToChannel(String tokenKey, Message message) {
        try {
            String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(tokenKey).toString();
            log.debug(" SENDING {} TO {}", message.getType(), queueName);
            channelGameOut.basicPublish("", queueName, null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private GameCharacter getNextCharToPlay() {
        GameCharacter result;
        while (readyToPlay.isEmpty()) {
            for (Player player : characterPositionMap.keySet()) {
                for (GameCharacter gameCharacter : characterPositionMap.get(player).keySet()) {
                    if (gameCharacter.tickHourglass()) {
                        readyToPlay.add(gameCharacter);
                    }
                }
            }
        }
        result = readyToPlay.get(0);
        readyToPlay.remove(result);
        if (null == result) {
            log.error("[X] CHAR TO PLAY = NULL");
        } else {
            log.debug("[-] CHAR TO PLAY : {}", result.getName());
        }
        return result;
    }

    private Player getPlayerForCharacter(GameCharacter gameCharacter) {
        Player result = null;

        for (Player player : playerList) {
            for (GameCharacter gameCharacterOfPlayer : player.getTeam().getTeam()) {
                log.debug("[-] CHAR : {}", gameCharacterOfPlayer.getName());
                if (gameCharacterOfPlayer.equals(gameCharacter)) {
                    result = player;
                    log.debug("= {}", gameCharacter.getName());
                    break;
                } else {
                    log.debug("/= {}", gameCharacter.getName());
                }
            }
            if (null != result) {
                break;
            }
        }
        if (null == result) {
            log.error("[X] CHAR TO PLAY BELONGS TO NO ONE");
        }
        return result;
    }

    private Position getCharacterPosition(GameCharacter gameCharacter) {
        Position result = null;
        for (Map<GameCharacter, Position> gameCharacterPositionMap : characterPositionMap.values()) {
            if (gameCharacterPositionMap.keySet().contains(gameCharacter)) {
                result = gameCharacterPositionMap.get(gameCharacter);
                break;
            }
        }
        return result;
    }

    public void receiveMessage() {
        try {
            QueueingConsumer.Delivery delivery = gameConsumer.nextDelivery(1);
            if (null != delivery) {
                Message rawMessage = Message.loadFromBytes(delivery.getBody());
                ClientMessage message = (ClientMessage) rawMessage;
                if (null != message) {
                    switch (message.getType()) {
                        case FINISH_DEPLOYMENT:
                            MessageDeploymentFinishedForPlayer messageDeploymentFinishedForPlayer = (MessageDeploymentFinishedForPlayer) message;
                            log.debug(" [-] FINISH DEPLOYMENT MESSAGE FROM {}", messageDeploymentFinishedForPlayer.getKeyToken());
                            for (GameCharacter gameCharacter : messageDeploymentFinishedForPlayer.getCharacterPositionMap().keySet()) {
                                log.debug("-> {} : {}", gameCharacter.getName(), messageDeploymentFinishedForPlayer.getCharacterPositionMap().get(gameCharacter));
                            }
                            characterPositionMap.put(playerList.get(messageDeploymentFinishedForPlayer.getPlayerNumber()), messageDeploymentFinishedForPlayer.getCharacterPositionMap());
                            if (deployIsComplete()) {
                                log.debug(" [-] DEPLOYMENT IS COMPLETE");
                                completeDeployement();
                                newTurn();
                            }
                            break;
                        case CHARACTER_POSITION_TO_MOVE_REQUEST:
                            if (messageIsExpected(message)) {
                                MessagePositionToMoveRequest messagePositionToMoveRequest = (MessagePositionToMoveRequest) message;
                                GameCharacter characterToMove = messagePositionToMoveRequest.getCharacter();
                                if (characterToMove.equals(currentCharToPlay)) {
                                    Tree<Position> possiblePositionsToMoveTree = getPossiblePositionsToMoveTree(characterToMove);
                                    List<Position> possiblePositionsToMove = possiblePositionsToMoveTree.getAllElements();
                                    possiblePositionsToMove.remove(getCharacterPosition(characterToMove).plusYNew(-1));

                                    MessagePositionToMoveResponse messagePositionToMoveResponse = new MessagePositionToMoveResponse(possiblePositionsToMove);
                                    sendMessageToChannel(messagePositionToMoveRequest.getKeyToken(), messagePositionToMoveResponse);
                                } else {
                                    log.error(" [X] UNEXPECTED CHAR TO PLAY");
                                }
                            } else {
                                log.error(" [X] UNEXPECTED PLAYER TO PLAY {}", message.getKeyToken());
                            }
                            break;
                        case CHARACTER_ACTION_MOVE:
                            if (messageIsExpected(message)) {
                                MessageCharacterActionMove messageCharacterActionMove = (MessageCharacterActionMove) message;
                                GameCharacter characterToMove = messageCharacterActionMove.getCharacter();
                                if (characterToMove.equals(currentCharToPlay)) {
                                    Position positionToMove = messageCharacterActionMove.getPositionToMove();
                                    Tree<Position> possiblePositionsToMoveTree2 = getPossiblePositionsToMoveTree(characterToMove);
                                    List<Node<Position>> nodeList = possiblePositionsToMoveTree2.find(positionToMove);
                                    if (!nodeList.isEmpty()) {
                                        characterToMove.setHasMoved(true);
                                        // the position is on the floor, set the char position to position +Y1
                                        setCharacterPosition(characterToMove, positionToMove.plusYNew(1));
                                        List<Position> path = nodeList.get(0).getPathFromTop();
                                        MessageCharacterMoves messageCharacterMoves = new MessageCharacterMoves(characterToMove, path);
                                        sendToAll(messageCharacterMoves);
                                        MessageCharacterToPlay messageCharacterToPlay = new MessageCharacterToPlay(characterToMove, positionToMove);
                                        sendMessageToChannel(messageCharacterActionMove.getKeyToken(), messageCharacterToPlay);
                                    } else {
                                        log.error(" [X] NOT VALID POSITION TO MOVE : {}", positionToMove.toString());
                                    }
                                } else {
                                    log.error(" [X] UNEXPECTED CHAR TO PLAY");
                                }
                            } else {
                                log.error(" [X] UNEXPECTED PLAYER TO PLAY {}", message.getKeyToken());
                            }
                            break;
                        case CHARACTER_POSITION_TO_ATTACK_REQUEST:
                            if (messageIsExpected(message)) {
                            } else {
                                log.error(" [X] UNEXPECTED PLAYER TO PLAY {}", message.getKeyToken());
                            }
                            break;
                        case CHARACTER_ACTION_ATTACK:
                            if (messageIsExpected(message)) {
                            } else {
                                log.error(" [X] UNEXPECTED PLAYER TO PLAY {}", message.getKeyToken());
                            }
                            break;
                        case CHARACTER_ACTION_END_TURN:
                            if (messageIsExpected(message)) {
                                MessageCharacterEndTurn messageCharacterEndTurn = (MessageCharacterEndTurn) message;
                                GameCharacter character = messageCharacterEndTurn.getCharacter();
                                if (character.equals(currentCharToPlay)) {
                                    for (Player player : playerList) {
                                        if (characterPositionMap.get(player).keySet().contains(character)) {
                                            for (GameCharacter gameCharacter : characterPositionMap.get(player).keySet()) {
                                                if (gameCharacter.equals(character)) {
                                                    gameCharacter.setHeadingAngle(character.getHeadingAngle());
                                                }
                                            }
                                        }
                                    }
                                    newTurn();
                                } else {
                                    log.error(" [X] UNEXPECTED CHAR TO PLAY");
                                }
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
        String playerKey = AuthenticationManager.getInstance().getTokenKeyFor(playerToPlay.getPseudo());
        int expectedPlayerNumber = keyTokenPlayerNumberMap.get(playerKey);
        log.debug("message from player n°{} , expecting from player n°{}", receivedPlayerNumber, expectedPlayerNumber);
        if (receivedPlayerNumber == expectedPlayerNumber) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    private void setCharacterPosition(GameCharacter character, Position position) {
        for (Player player : playerList) {
            if (characterPositionMap.get(player).keySet().contains(character)) {
                characterPositionMap.get(player).put(character, position);
                break;
            }
        }
    }

    private Tree<Position> getPossiblePositionsToMoveTree(GameCharacter gameCharacter) {
        Position characterPosition = getCharacterPosition(gameCharacter).plusYNew(-1);
        Tree<Position> result = battlefield.getPositionTree(characterPosition, 3, 2, 1);
        for (Player player : playerList) {
            for (GameCharacter character : characterPositionMap.get(player).keySet()) {
                if (!character.equals(gameCharacter)) {
                    Position footPositionOfChar = characterPositionMap.get(player).get(character).plusYNew(-1);
                    if (result.contains(footPositionOfChar)) {
                        log.debug("remove : {}", footPositionOfChar);
                        result.remove(footPositionOfChar);
                    }
                }
            }
        }
        return result;
    }

    private void completeDeployement() {
        for (Player player : playerList) {
            log.debug("player {}", playerList.indexOf(player));
            Map<GameCharacter, Position> characterPositionMapForPlayer = new HashMap<GameCharacter, Position>();
            for (GameCharacter gameCharacter : characterPositionMap.get(player).keySet()) {
                log.debug("-> {} : {}", gameCharacter.getName(), characterPositionMap.get(player).get(gameCharacter));
                characterPositionMapForPlayer.put(gameCharacter, characterPositionMap.get(player).get(gameCharacter));
            }
            MessageDeploymentPositionsOfPlayer messageDeploymentPositionsOfPlayer =
                    new MessageDeploymentPositionsOfPlayer(characterPositionMapForPlayer, playerList.indexOf(player));
            for (Player playerToNotify : playerList) {
                if (player != playerToNotify) {
                    sendMessageToPlayer(playerToNotify, messageDeploymentPositionsOfPlayer);
                }
            }
        }
    }

    private void newTurn() {
        GameCharacter charToPlay = getNextCharToPlay();
        Player playerToPlay = getPlayerForCharacter(charToPlay);

        Map<GameCharacter, Position> actualCharacterPositionMap = new HashMap<GameCharacter, Position>();
        for (Player player : characterPositionMap.keySet()) {
            actualCharacterPositionMap.putAll(characterPositionMap.get(player));
        }
        MessageUpdateCharacters messageUpdateCharacters = new MessageUpdateCharacters(actualCharacterPositionMap);

        for (Player player : playerList) {
            sendMessageToPlayer(player, messageUpdateCharacters);
        }
        Position footPositionOfChar = actualCharacterPositionMap.get(charToPlay).plusYNew(-1);
        MessageCharacterToPlay messageCharacterToPlay = new MessageCharacterToPlay(charToPlay, footPositionOfChar);
        sendMessageToPlayer(playerToPlay, messageCharacterToPlay);

        this.playerToPlay = playerToPlay;
        this.currentCharToPlay = charToPlay;
    }

    private boolean deployIsComplete() {
        boolean result = true;

        for (Player player : playerList) {
            if (!characterPositionMap.keySet().contains(player)) {
                result = false;
                log.debug("[x] DEPLOYMENT NOT FINISHED FOR {}", player.getPseudo());
                break;
            }
        }

        return result;
    }
}
