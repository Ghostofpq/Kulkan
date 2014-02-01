package com.ghostofpq.kulkan.server.authentication;


import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.ping.MessageMajong;
import com.ghostofpq.kulkan.entities.messages.ping.MessagePing;
import com.ghostofpq.kulkan.entities.messages.ping.MessagePong;
import com.ghostofpq.kulkan.server.database.controller.UserController;
import com.ghostofpq.kulkan.server.lobby.LobbyManager;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class PingManager implements Runnable {
    private static final String CLIENT_QUEUE_NAME_BASE = "client/";
    private final String PING_MANAGER_QUEUE_NAME = "server/ping";

    private Map<String, Long> playersPingMap;
    // PARAMETERS - SPRING
    private String hostIp;
    private Integer hostPort;
    private Integer authKeySize;
    // MESSAGING
    private QueueingConsumer consumer;
    private Connection connection;
    private Channel channelPingManager;
    private Channel channelOut;
    // THREAD ROUTINE
    private boolean requestClose;
    private boolean interrupted;
    @Autowired
    private UserController userController;
    @Autowired
    private LobbyManager lobbyManager;

    private PingManager() {
        requestClose = false;
        interrupted = false;
        playersPingMap = new HashMap<String, Long>();
    }

    public void addPlayerInPingList(String playerTokenKey) {
        playersPingMap.put(playerTokenKey, 0l);
        String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(playerTokenKey).toString();
        try {
            channelOut.queueDeclare(queueName, false, false, false, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void whenRemovePlayerInPingList(String playerTokenKey) {
        userController.removeTokenKey(playerTokenKey);
        lobbyManager.removeClient(playerTokenKey);
    }

    public void initConnection() throws IOException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostIp);
        factory.setPort(hostPort);
        log.debug("{}:{}", hostIp, hostPort);
        connection = factory.newConnection();
        channelPingManager = connection.createChannel();
        channelPingManager.queueDeclare(PING_MANAGER_QUEUE_NAME, false, false, false, null);
        channelPingManager.basicQos(1);
        consumer = new QueueingConsumer(channelPingManager);
        channelPingManager.basicConsume(PING_MANAGER_QUEUE_NAME, true, consumer);

        channelOut = connection.createChannel();
    }

    // THREAD ROUTINE
    public void run() {
        while (!requestClose) {
            sendPings();
            interrupted = false;
            while (!interrupted) {
                try {
                    receiveMessage();
                } catch (InterruptedException e) {
                    log.warn("Message Wait interrupted");
                    interrupted = true;
                    logConnectionState();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            channelPingManager.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logConnectionState() {
        StringBuilder toLog = new StringBuilder().append(System.getProperty("line.separator"))
                .append("======================").append(System.getProperty("line.separator"))
                .append("|| CONNECTION STATE ||").append(System.getProperty("line.separator"))
                .append("======================").append(System.getProperty("line.separator"));
        Iterator<String> iterator = playersPingMap.keySet().iterator();
        while (iterator.hasNext()) {
            String playerTokenKey = iterator.next();
            toLog.append(">> ").append(playerTokenKey).append("||").append(playersPingMap.get(playerTokenKey)).append(System.getProperty("line.separator"));
        }
        log.debug(toLog.toString());
    }

    private void sendPings() {
        Iterator<String> iterator = playersPingMap.keySet().iterator();
        while (iterator.hasNext()) {
            String playerTokenKey = iterator.next();
            if (playersPingMap.get(playerTokenKey) == null) {
                // player did not ping back
                iterator.remove();
                whenRemovePlayerInPingList(playerTokenKey);
            } else {
                playersPingMap.put(playerTokenKey, null);
                sendPing(playerTokenKey);
            }
        }
    }

    private void sendPing(String playerTokenKey) {
        MessagePing messagePing = new MessagePing();
        sendMessageTo(playerTokenKey, messagePing);
    }

    private void sendMajong(String playerTokenKey, long timestampClient) {
        MessageMajong messageMajong = new MessageMajong(timestampClient);
        sendMessageTo(playerTokenKey, messageMajong);
    }

    private void sendMessageTo(String playerTokenKey, Message message) {
        try {
            String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(playerTokenKey).toString();
            log.debug(" [S] SENDING {} TO {}", message.getType(), queueName);
            channelOut.basicPublish("", queueName, null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessage() throws InterruptedException, IOException {
        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
        if (null != delivery) {
            Message message = Message.loadFromBytes(delivery.getBody());
            log.debug("Receive on [{}] : {}", PING_MANAGER_QUEUE_NAME, message.toString());
            switch (message.getType()) {
                case PONG:
                    managePong(message);
                    break;
                default:
                    break;
            }
        }
    }

    private void managePong(Message message) {
        MessagePong messagePong = (MessagePong) message;
        long delta = System.currentTimeMillis() - messagePong.getTimestampServer();
        playersPingMap.put(messagePong.getKeyToken(), delta);

        sendMajong(messagePong.getKeyToken(), messagePong.getTimestampClient());
    }

    public void setRequestClose(boolean requestClose) {
        this.requestClose = requestClose;
    }

    // SPRING
    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public void setHostPort(Integer hostPort) {
        this.hostPort = hostPort;
    }

    public void setAuthKeySize(Integer authKeySize) {
        this.authKeySize = authKeySize;
    }
}
