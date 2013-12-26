package com.ghostofpq.kulkan.server.lobby;


import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.lobby.*;
import com.ghostofpq.kulkan.server.authentication.AuthenticationManager;
import com.ghostofpq.kulkan.server.database.controller.UserController;
import com.ghostofpq.kulkan.server.matchmaking.MatchmakingManager;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LobbyManager implements Runnable {
    private final String CLIENT_QUEUE_NAME_BASE = "/client/";
    private final String LOBBY_SERVER_QUEUE_NAME_BASE = "/server/lobby";
    private AuthenticationManager authenticationManager;
    private String hostIp;
    private Integer hostPort;
    private Connection connection;
    private MatchmakingManager matchmakingManager;
    private List<String> connectedClients;
    private List<String> pongClients;
    private Channel channelOut;
    private Channel channelLobbyIn;
    private QueueingConsumer lobbyConsumer;
    private long lastTimePing;
    // THREAD ROUTINE
    private boolean requestClose;
    @Autowired
    private UserController userController;

    private LobbyManager() {
        requestClose = false;
        connectedClients = new ArrayList<String>();
        pongClients = new ArrayList<String>();
        lastTimePing = System.currentTimeMillis();
    }

    public void initConnections() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(hostIp);
            factory.setPort(hostPort);
            connection = factory.newConnection();
            channelOut = connection.createChannel();
            channelLobbyIn = connection.createChannel();
            channelLobbyIn.queueDeclare(LOBBY_SERVER_QUEUE_NAME_BASE, false, false, false, null);
            lobbyConsumer = new QueueingConsumer(channelLobbyIn);
            channelLobbyIn.basicConsume(LOBBY_SERVER_QUEUE_NAME_BASE, true, lobbyConsumer);
            log.debug(" [-] OPENING QUEUE : {}", LOBBY_SERVER_QUEUE_NAME_BASE);
            QueueingConsumer.Delivery delivery = lobbyConsumer.nextDelivery(1);
            while (null != delivery) {
                delivery = lobbyConsumer.nextDelivery(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (!requestClose) {
            try {
                receiveMessage();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        closeChannel();
              /*
        if (deltaTimeInMillis() >= 15000) {
            pingClients();
            lastTimePing = System.currentTimeMillis();
        }                                   */
    }

    private long deltaTimeInMillis() {
        return System.currentTimeMillis() - lastTimePing;
    }

    public void receiveMessage() throws IOException, InterruptedException {
        QueueingConsumer.Delivery delivery = lobbyConsumer.nextDelivery();
        if (null != delivery) {
            log.debug(" [-] RECEIVED MESSAGE ON : {}", LOBBY_SERVER_QUEUE_NAME_BASE);
            Message message = Message.loadFromBytes(delivery.getBody());
            if (null != message) {
                switch (message.getType()) {
                    case LOBBY_CLIENT:
                        MessageLobbyClient messageLobbyClient = (MessageLobbyClient) message;
                        log.error(" [X] POST FROM {} : [{}]", messageLobbyClient.getKeyToken(), messageLobbyClient.getLobbyMessage());
                        postMessage(messageLobbyClient);
                        break;
                    case LOBBY_PONG:
                        MessageLobbyPong messageLobbyPong = (MessageLobbyPong) message;
                        pongFor(messageLobbyPong.getKeyToken());
                        break;
                    case LOBBY_SUBSCRIBE:
                        MessageSubscribeToLobby messageSubscribeToLobby = (MessageSubscribeToLobby) message;
                        log.error(" [X] LOBBY_SUBCRIBE : {}", messageSubscribeToLobby.getKeyToken());
                        addClient(messageSubscribeToLobby.getKeyToken());
                        break;
                    case LOBBY_UNSUBCRIBE:
                        MessageUnsubscribeToLobby messageUnsubscribeToLobby = (MessageUnsubscribeToLobby) message;
                        log.error(" [X] LOBBY_UNSUBCRIBE : {}", messageUnsubscribeToLobby.getKeyToken());
                        removeClient(messageUnsubscribeToLobby.getKeyToken());
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
            if (!connectedClients.contains(clientKey)) {
                log.debug(" [-] ADDING CLIENT KEY : {}", clientKey);
                String clientChannelName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(clientKey).toString();
                channelOut.queueDeclare(clientChannelName, false, false, false, null);
                log.debug(" [-] OPENING QUEUE : {}", clientChannelName);
                connectedClients.add(clientKey);
                if (!pongClients.contains(clientKey)) {
                    pongClients.add(clientKey);
                } else {
                    log.error(" CLIENT {} already in ping list", clientKey);
                }
            } else {
                log.error(" CLIENT {} already in connectedClients", clientKey);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeClient(String clientKey) {
        log.debug(" [-] REMOVING CLIENT KEY : {}", clientKey);
        connectedClients.remove(clientKey);
        matchmakingManager.removeClient(clientKey);
    }

    public void postMessage(MessageLobbyClient messageLobbyClient) throws IOException {
        String clientPseudo = userController.getNameForTokenKey(messageLobbyClient.getKeyToken());
        if (clientPseudo != "") {
            String message = new StringBuilder().append("[").append(clientPseudo).append("]  :  ").append(messageLobbyClient.getLobbyMessage()).toString();
            MessageLobbyServer messageLobbyServer = new MessageLobbyServer(message);
            log.debug(" [-] LOBBY MESSAGE TO SEND : '{}'", message);
            for (String target : connectedClients) {
                String clientChannelName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(target).toString();
                log.debug(" [-] SENDING TO {}", target);
                channelOut.basicPublish("", clientChannelName, null, messageLobbyServer.getBytes());
            }
        } else {
            removeClient(messageLobbyClient.getKeyToken());
        }
    }

    public void pingClients() throws IOException {
        List<String> absentClients = new ArrayList<String>();
        for (String target : connectedClients) {
            if (pongClients.contains(target)) {
                log.debug(" [-] SENDING PING TO {}", target);
                String clientChannelName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(target).toString();
                MessageLobbyPing messageLobbyPing = new MessageLobbyPing();
                channelOut.basicPublish("", clientChannelName, null, messageLobbyPing.getBytes());
            } else {
                absentClients.add(target);
            }
        }
        for (String absentClient : absentClients) {
            removeClient(absentClient);
        }
        pongClients = new ArrayList<String>();
    }

    public void pongFor(String clientKey) {
        pongClients.add(clientKey);
    }

    public void closeChannel() {
        try {
            channelOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setMatchmakingManager(MatchmakingManager matchmakingManager) {
        this.matchmakingManager = matchmakingManager;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public void setHostPort(Integer hostPort) {
        this.hostPort = hostPort;
    }

    public void setRequestClose(boolean requestClose) {
        this.requestClose = requestClose;
    }
}
