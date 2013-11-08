package com.ghostofpq.kulkan.server.matchmaking;

import com.ghostofpq.kulkan.entities.battlefield.Battlefield;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.game.MessageGameStart;
import com.ghostofpq.kulkan.entities.messages.lobby.*;
import com.ghostofpq.kulkan.server.authentication.AuthenticationManager;
import com.ghostofpq.kulkan.server.database.controller.UserController;
import com.ghostofpq.kulkan.server.database.model.User;
import com.ghostofpq.kulkan.server.game.GameManager;
import com.ghostofpq.kulkan.server.lobby.LobbyManager;
import com.ghostofpq.kulkan.server.utils.SaveManager;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MatchmakingManager {
    private final String CLIENT_QUEUE_NAME_BASE = "/client/";
    private final String MATCHMAKING_SERVER_QUEUE_NAME_BASE = "/server/matchmaking";
    private String hostIp;
    private Integer hostPort;
    private Connection connection;
    private AuthenticationManager authenticationManager;
    private LobbyManager lobbyManager;
    private GameManager gameManager;
    private List<String> subscribedClients;
    private Map<String, Match> matchMap;
    private Channel channelOut;
    private Channel channelMatchmakingIn;
    private QueueingConsumer matchmakingConsumer;
    private int matchmapIncrementor;
    @Autowired
    private UserController userController;

    private MatchmakingManager() {
        matchmapIncrementor = 0;
        subscribedClients = new ArrayList<String>();
        matchMap = new HashMap<String, Match>();
    }

    public void initConnections() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(hostIp);
            factory.setPort(hostPort);
            connection = factory.newConnection();
            channelOut = connection.createChannel();
            channelMatchmakingIn = connection.createChannel();
            channelMatchmakingIn.queueDeclare(MATCHMAKING_SERVER_QUEUE_NAME_BASE, false, false, false, null);
            matchmakingConsumer = new QueueingConsumer(channelMatchmakingIn);
            channelMatchmakingIn.basicConsume(MATCHMAKING_SERVER_QUEUE_NAME_BASE, true, matchmakingConsumer);
            log.debug(" [-] OPENING QUEUE : {}", MATCHMAKING_SERVER_QUEUE_NAME_BASE);
            QueueingConsumer.Delivery delivery = matchmakingConsumer.nextDelivery(1);
            while (null != delivery) {
                delivery = matchmakingConsumer.nextDelivery(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() throws IOException, InterruptedException {
        receiveMessage();
        match();
        checkMatchPropositions();
    }

    public void receiveMessage() throws IOException, InterruptedException {
        QueueingConsumer.Delivery delivery = matchmakingConsumer.nextDelivery(1);
        if (null != delivery) {
            Message message = Message.loadFromBytes(delivery.getBody());
            if (null != message) {
                switch (message.getType()) {
                    case MATCHMAKING_SUBSCRIBE:
                        MessageMatchmakingSubscribe messageMatchmakingSubscribe = (MessageMatchmakingSubscribe) message;
                        addClient(messageMatchmakingSubscribe.getKeyToken());
                        break;
                    case MATCHMAKING_UNSUBSCRIBE:
                        MessageMatchmakingUnsubscribe messageMatchmakingUnsubscribe = (MessageMatchmakingUnsubscribe) message;
                        removeClient(messageMatchmakingUnsubscribe.getKeyToken());
                        break;
                    case MATCHMAKING_ACCEPT:
                        MessageMatchmakingAccept messageMatchmakingAccept = (MessageMatchmakingAccept) message;
                        log.debug(" [-] CLIENT {} ACCEPTS MATCH {}", messageMatchmakingAccept.getKeyToken(), messageMatchmakingAccept.getMatchKey());
                        matchMap.get(messageMatchmakingAccept.getMatchKey()).clientAccept(messageMatchmakingAccept.getKeyToken());
                        break;
                    case MATCHMAKING_REFUSE:
                        MessageMatchmakingRefuse messageMatchmakingRefuse = (MessageMatchmakingRefuse) message;
                        log.debug(" [-] CLIENT {} REFUSES MATCH {}", messageMatchmakingRefuse.getKeyToken(), messageMatchmakingRefuse.getMatchKey());
                        matchMap.get(messageMatchmakingRefuse.getMatchKey()).clientRefuse(messageMatchmakingRefuse.getKeyToken());
                        break;
                    default:
                        log.error(" [X] UNEXPECTED MESSAGE : {}", message.getType());
                        break;
                }
            }
        }
    }

    public void addClient(String clientKey) {
        try {
            log.debug(" [-] ADDING CLIENT KEY {}", clientKey);
            String clientChannelName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(clientKey).toString();
            channelOut.queueDeclare(clientChannelName, false, false, false, null);
            log.debug(" [-] OPENING QUEUE : {}", clientChannelName);
            if (!subscribedClients.contains(clientKey)) {
                subscribedClients.add(clientKey);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeClient(String clientKey) {
        log.debug(" [-] REMOVING CLIENT KEY {}", clientKey);
        subscribedClients.remove(clientKey);
        for (String matchKey : matchMap.keySet()) {
            Match match = matchMap.get(matchKey);
            if (match.getAllClients().contains(clientKey)) {
                match.clientRefuse(clientKey);
            }
        }
    }

    public void checkMatchPropositions() {
        List<String> matchesToRemove = new ArrayList<String>();
        for (String matchKey : matchMap.keySet()) {
            Match match = matchMap.get(matchKey);
            Match.ClientState globalClientState = match.getGlobalClientState();
            switch (globalClientState) {
                case ACCEPT:
                    log.debug(" [-] GAME {} STARTS ", matchKey);
                    Battlefield battlefield = SaveManager.getInstance().loadMap("mapTest1");
                    List<Player> playerList = new ArrayList<Player>();
                    for (String client : match.getAllClients()) {
                        User user = userController.getUserForTokenKey(client);
                        Player player = user.toPlayer();
                        playerList.add(player);
                    }
                    MessageGameStart messageGameStart = new MessageGameStart(matchKey, battlefield, playerList);
                    for (String client : match.getAllClients()) {
                        String clientChannelName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(client).toString();
                        try {
                            log.debug(" [-] SENDING GAME START TO {}", client);
                            channelOut.basicPublish("", clientChannelName, null, messageGameStart.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        lobbyManager.removeClient(client);
                    }
                    gameManager.addGame(matchKey, battlefield, playerList);
                    matchesToRemove.add(matchKey);
                    break;
                case REFUSE:
                    log.debug(" [-] GAME {} WAS REFUSED ", matchKey);
                    List<String> clientsToReinject = match.getClientsToReinject();
                    for (String client : clientsToReinject) {
                        if (!subscribedClients.contains(client)) {
                            subscribedClients.add(client);
                        }
                        sendAbortMessage(client);
                    }
                    matchesToRemove.add(matchKey);
                    break;
                case PENDING:
                    break;
            }
        }
        for (String match : matchesToRemove) {
            matchMap.remove(match);
        }
    }

    public void sendAbortMessage(String clientKey) {
        MessageMatchAbort messageMatchAbort = new MessageMatchAbort();
        String clientChannelName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(clientKey).toString();
        log.debug(" [-] MATCH ABORT FOR {}", clientKey);
        try {
            channelOut.basicPublish("", clientChannelName, null, messageMatchAbort.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void match() {
        if (subscribedClients.size() >= 2) {
            List<String> matchedPlayers = new ArrayList<String>();
            matchedPlayers.add(subscribedClients.get(0));
            matchedPlayers.add(subscribedClients.get(1));

            Match match = new Match(matchedPlayers);
            String matchKey = insertInMatchMap(match);

            for (String matchedPlayer : matchedPlayers) {
                MessageMatchFound messageMatchFound = new MessageMatchFound(matchKey);
                String clientChannelName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(matchedPlayer).toString();
                log.debug(" [-] MATCH FOUND FOR {}", matchedPlayer);
                try {
                    channelOut.basicPublish("", clientChannelName, null, messageMatchFound.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                subscribedClients.remove(matchedPlayer);
            }

        }
    }

    public String insertInMatchMap(Match match) {
        String matchKey = String.valueOf(matchmapIncrementor);
        matchMap.put(matchKey, match);
        matchmapIncrementor++;
        return matchKey;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setLobbyManager(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public void setHostPort(Integer hostPort) {
        this.hostPort = hostPort;
    }
}
