package com.ghostofpq.kulkan.server.authentication;


import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.server.database.controller.UserController;
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
    private final String pingManagerQueueName = "server/ping";

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
    @Autowired
    private UserController userController;

    private PingManager() {
        requestClose = false;
        playersPingMap = new HashMap<String, Long>();
    }

    public void addPlayerInPingList(String playerTokenKey) {
        playersPingMap.put(playerTokenKey, null);
        String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(playerTokenKey).toString();
        try {
            channelOut.queueDeclare(queueName, false, false, false, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void whenRemovePlayerInPingList(String playerTokenKey) {
        userController.removeTokenKey(playerTokenKey);
    }

    public void initConnection() throws IOException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostIp);
        factory.setPort(hostPort);
        log.debug("{}:{}", hostIp, hostPort);
        connection = factory.newConnection();
        channelPingManager = connection.createChannel();
        channelPingManager.queueDeclare(pingManagerQueueName, false, false, false, null);
        channelPingManager.basicQos(1);
        consumer = new QueueingConsumer(channelPingManager);
        channelPingManager.basicConsume(pingManagerQueueName, false, consumer);
        channelOut = connection.createChannel();
    }

    // THREAD ROUTINE
    public void run() {
        while (!requestClose) {
            sendPings();
            try {
                receiveMessage();
            } catch (InterruptedException e) {
                log.warn("Message Wait interrupted");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            channelPingManager.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            }
        }
    }

    private void sendPing(String playerTokenKey) {

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
            switch (message.getType()) {
                case AUTHENTICATION_REQUEST:
                    break;
                default:
                    break;
            }
        }
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
