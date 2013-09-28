package com.ghostofpq.kulkan.server.lobby;


import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageLobbyClient;
import com.ghostofpq.kulkan.entities.messages.MessageLobbyServer;
import com.ghostofpq.kulkan.entities.messages.MessageType;
import com.ghostofpq.kulkan.server.Server;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LobbyManager {

    private static volatile LobbyManager instance = new LobbyManager();
    private final String CLIENT_QUEUE_NAME_BASE = "/client/";
    private final String LOBBY_SERVER_QUEUE_NAME_BASE = "/server/lobby";
    private List<String> connectedClients;
    private Channel channelOut;
    private Channel channelLobbyIn;
    private QueueingConsumer lobbyConsumer;

    private LobbyManager() {
        connectedClients = new ArrayList<String>();
        try {
            channelOut = Server.getInstance().getConnection().createChannel();
            channelLobbyIn = Server.getInstance().getConnection().createChannel();
            channelLobbyIn.queueDeclare(LOBBY_SERVER_QUEUE_NAME_BASE, false, false, false, null);
            lobbyConsumer = new QueueingConsumer(channelLobbyIn);
            channelLobbyIn.basicConsume(LOBBY_SERVER_QUEUE_NAME_BASE, true, lobbyConsumer);
            log.debug(" [-] OPENING QUEUE : {}", LOBBY_SERVER_QUEUE_NAME_BASE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LobbyManager getInstance() {
        if (instance == null) {
            synchronized (LobbyManager.class) {
                if (instance == null) {
                    instance = new LobbyManager();
                }
            }
        }
        return instance;
    }

    public void run() {
        MessageLobbyClient messageLobbyClient = receiveMessage();
        if (null != messageLobbyClient) {
            postMessage(messageLobbyClient);
        }
    }

    public MessageLobbyClient receiveMessage() {
        MessageLobbyClient result = null;
        try {
            QueueingConsumer.Delivery delivery = lobbyConsumer.nextDelivery(1);
            if (null != delivery) {
                log.debug(" [-] RECEIVED MESSAGE ON : {}", LOBBY_SERVER_QUEUE_NAME_BASE);
                Message message = Message.loadFromBytes(delivery.getBody());
                if (null != message) {
                    if (message.getType().equals(MessageType.LOBBY_CLIENT)) {
                        result = (MessageLobbyClient) message;
                    } else {
                        log.error(" [X] UNEXPECTED MESSAGE : {}", message.getType());
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void addClient(String clientKey) {
        try {
            log.debug(" [-] ADDING CLIENT KEY : {}", clientKey);
            String clientChannelName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(clientKey).toString();
            channelOut.queueDeclare(clientChannelName, false, false, false, null);
            log.debug(" [-] OPENING QUEUE : {}", clientChannelName);
            connectedClients.add(clientKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeClient(String clientKey) {
        log.debug(" [-] REMOVING CLIENT KEY : {}", clientKey);
        connectedClients.remove(clientKey);
    }

    public void postMessage(MessageLobbyClient messageLobbyClient) {
        String message = new StringBuilder().append("[").append(messageLobbyClient.getKeyToken()).append("]  :  ").append(messageLobbyClient.getLobbyMessage()).toString();
        MessageLobbyServer messageLobbyServer = new MessageLobbyServer(connectedClients, message);
        log.debug(" [-] LOBBY MESSAGE TO SEND : '{}'", message);
        for (String target : connectedClients) {
            String clientChannelName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(target).toString();
            try {
                log.debug(" [-] SENDING TO {}", target);
                channelOut.basicPublish("", clientChannelName, null, messageLobbyServer.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeChannel() {
        try {
            channelOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
