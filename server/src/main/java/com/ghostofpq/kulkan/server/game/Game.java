package com.ghostofpq.kulkan.server.game;

import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.battlefield.BattleSceneState;
import com.ghostofpq.kulkan.entities.battlefield.Battlefield;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageDeploymentStart;
import com.ghostofpq.kulkan.server.Server;
import com.ghostofpq.kulkan.server.authentification.AuthenticationManager;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    private final String CLIENT_QUEUE_NAME_BASE = "/client/";
    private final String GAME_SERVER_QUEUE_NAME_BASE = "/server/game/";
    private BattleSceneState state;
    private Battlefield battlefield;
    private List<Player> playerList;
    private Map<GameCharacter, Position> characterPositionMap;
    private Map<Player, String> playerChannelMap;
    private Channel channelGameIn;
    private Channel channelGameOut;
    private QueueingConsumer gameConsumer;
    private String gameID;

    public Game(Battlefield battlefield, List<Player> playerList, String gameID) {
        this.battlefield = battlefield;
        this.playerList = playerList;
        this.gameID = gameID;
        state = BattleSceneState.DEPLOY;
        characterPositionMap = new HashMap<GameCharacter, Position>();
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

            channelGameOut = Server.getInstance().getConnection().createChannel();
            for (Player player : playerList) {
                String playerKey = AuthenticationManager.getInstance().getTokenKeyFor(player.getPseudo());
                String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(playerKey).toString();
                channelGameOut.queueDeclare(queueName, false, false, false, null);
                playerChannelMap.put(player, queueName);
            }
        } catch (IOException e) {
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
            channelGameOut.basicPublish("", playerChannelMap.get(player), null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
