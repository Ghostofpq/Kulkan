package com.ghostofpq.kulkan.server.game;

import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.battlefield.BattleSceneState;
import com.ghostofpq.kulkan.entities.battlefield.Battlefield;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageDeploymentFinishedForPlayer;
import com.ghostofpq.kulkan.entities.messages.MessageDeploymentPositionsOfPlayer;
import com.ghostofpq.kulkan.entities.messages.MessageDeploymentStart;
import com.ghostofpq.kulkan.server.Server;
import com.ghostofpq.kulkan.server.authentification.AuthenticationManager;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
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
    private Channel channelGameIn;
    private Channel channelGameOut;
    private QueueingConsumer gameConsumer;
    private String gameID;

    public Game(Battlefield battlefield, List<Player> playerList, String gameID) {
        this.battlefield = battlefield;
        this.playerList = playerList;
        this.gameID = gameID;
        state = BattleSceneState.DEPLOY_POSITION;
        characterPositionMap = new HashMap<Player, Map<GameCharacter, Position>>();
        playerChannelMap = new HashMap<Player, String>();
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
            sendMessageToPlayer(player, messageDeploymentStart);
        }
    }

    private void sendMessageToPlayer(Player player, Message message) {
        try {
            log.debug(" SENDING {} TO {}", message.getType(), playerChannelMap);
            channelGameOut.basicPublish("", playerChannelMap.get(player), null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage() {
        try {
            QueueingConsumer.Delivery delivery = gameConsumer.nextDelivery(1);
            if (null != delivery) {
                Message message = Message.loadFromBytes(delivery.getBody());
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

    private boolean deployIsComplete() {
        boolean result = true;

        for (Player player : playerList) {
            for (GameCharacter gameCharacter : player.getTeam().getTeam()) {
                if (null != characterPositionMap.get(player)) {
                    if (characterPositionMap.get(player).containsKey(gameCharacter)) {
                        result = false;
                        break;
                    }
                } else {
                    result = false;
                    break;
                }
            }
            if (!result) {
                break;
            }
        }

        return result;
    }
}
