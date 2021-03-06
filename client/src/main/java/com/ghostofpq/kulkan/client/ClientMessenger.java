package com.ghostofpq.kulkan.client;

import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.ping.MessageMajong;
import com.ghostofpq.kulkan.entities.messages.ping.MessagePing;
import com.ghostofpq.kulkan.entities.messages.ping.MessagePong;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Slf4j
public class ClientMessenger {
    // QUEUE NAMES
    private final String AUTHENTICATION_SERVICE_QUEUE_NAME = "server/authentication";
    private final String USER_SERVICE_QUEUE_NAME = "server/users";
    private final String LOBBY_SERVER_QUEUE_NAME_BASE = "server/lobby";
    private final String MATCHMAKING_SERVER_QUEUE_NAME_BASE = "server/matchmaking";
    private final String PING_MANAGER_QUEUE_NAME = "server/ping";
    private final String CLIENT_QUEUE_NAME_BASE = "client/";
    private final String GAME_SERVER_QUEUE_NAME_BASE = "server/game/";
    private String gameServerQueueName;
    private String clientQueueName;
    // CHANNELS
    private Connection connection;
    private Channel channelAuthentication;
    private Channel channelUsers;
    private Channel channelLobby;
    private Channel channelMatchmaking;
    private Channel channelClientIn;
    private Channel channelGame;
    private Channel channelPing;
    private QueueingConsumer channelClientConsumer;
    private QueueingConsumer channelAuthenticatingConsumer;
    private String authenticationReplyQueueName;
    private long ping;
    @Autowired
    private Client client;
    @Autowired
    private ClientContext clientContext;

    public ClientMessenger() {
    }

    public void initConnection() throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        log.debug("Opening connection on [{}:{}]", clientContext.getServerIP(), clientContext.getServerPort());
        factory.setHost(clientContext.getServerIP());
        factory.setPort(clientContext.getServerPort());
        connection = factory.newConnection();

        log.debug("Opening channel authenticating");
        openAuthenticationChannel();
    }

    private void openAuthenticationChannel() throws IOException {
        channelAuthentication = client.getConnection().createChannel();
        authenticationReplyQueueName = channelAuthentication.queueDeclare().getQueue();
        channelAuthenticatingConsumer = new QueueingConsumer(channelAuthentication);
        channelAuthentication.basicConsume(authenticationReplyQueueName, true, channelAuthenticatingConsumer);
    }

    private void closeAuthenticationChannel() throws IOException {
        if (null != channelAuthentication) {
            channelAuthentication.close();
            channelAuthentication = null;
            channelAuthenticatingConsumer = null;
        } else {
            log.warn("[{}] has already been closed", AUTHENTICATION_SERVICE_QUEUE_NAME);
        }
    }

    public Message requestOnAuthenticationChannel(Message message) throws IOException, InterruptedException {
        Message response = null;
        if (null != connection) {
            String corrId = java.util.UUID.randomUUID().toString();
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(authenticationReplyQueueName)
                    .build();
            log.debug("Publish on [{}] : {}", AUTHENTICATION_SERVICE_QUEUE_NAME, message.toString());
            channelAuthentication.basicPublish("", AUTHENTICATION_SERVICE_QUEUE_NAME, props, message.getBytes());
            QueueingConsumer.Delivery delivery = channelAuthenticatingConsumer.nextDelivery(1000);
            if (null != delivery) {
                if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                    response = Message.loadFromBytes(delivery.getBody());
                    log.debug("Receive response from [{}] : {}", AUTHENTICATION_SERVICE_QUEUE_NAME, response.toString());
                }
            }
        }
        return response;
    }

    public void openChannelsAfterAuthentication(String tokenKey) throws IOException {
        log.debug("Opening channels after authentication");

        log.debug("Closing authentication channel");
        closeAuthenticationChannel();

        if (null == channelClientIn) {
            clientQueueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(tokenKey).toString();
            channelClientIn = connection.createChannel();
            log.debug("Opening client channel in : [{}]", clientQueueName);
            channelClientIn.queueDeclare(clientQueueName, false, false, false, null);
            channelClientConsumer = new QueueingConsumer(channelClientIn);
            channelClientIn.basicConsume(clientQueueName, true, channelClientConsumer);
        } else {
            log.warn("[{}] has already been opened", clientQueueName);
        }

        if (null == channelUsers) {
            channelUsers = connection.createChannel();
            log.debug("Opening user service channel out : [{}]", USER_SERVICE_QUEUE_NAME);
            channelUsers.queueDeclare(USER_SERVICE_QUEUE_NAME, false, false, false, null);
        } else {
            log.warn("[{}] has already been opened", USER_SERVICE_QUEUE_NAME);
        }

        if (null == channelLobby) {
            channelLobby = connection.createChannel();
            log.debug("Opening lobby service channel out : [{}]", LOBBY_SERVER_QUEUE_NAME_BASE);
            channelLobby.queueDeclare(LOBBY_SERVER_QUEUE_NAME_BASE, false, false, false, null);
        } else {
            log.warn("[{}] has already been opened", LOBBY_SERVER_QUEUE_NAME_BASE);
        }

        if (null == channelMatchmaking) {
            channelMatchmaking = connection.createChannel();
            log.debug("Opening matchmaking service channel out : [{}]", MATCHMAKING_SERVER_QUEUE_NAME_BASE);
            channelMatchmaking.queueDeclare(MATCHMAKING_SERVER_QUEUE_NAME_BASE, false, false, false, null);
        } else {
            log.warn("[{}] has already been opened", MATCHMAKING_SERVER_QUEUE_NAME_BASE);
        }

        if (null == channelPing) {
            channelPing = connection.createChannel();
            log.debug("Opening ping service channel out : [{}]", PING_MANAGER_QUEUE_NAME);
            channelPing.queueDeclare(PING_MANAGER_QUEUE_NAME, false, false, false, null);
        } else {
            log.warn("[{}] has already been opened", PING_MANAGER_QUEUE_NAME);
        }
    }

    public void openChannelGame(String gameNumber) {
        try {
            gameServerQueueName = new StringBuilder().append(GAME_SERVER_QUEUE_NAME_BASE).append(gameNumber).toString();
            channelGame = connection.createChannel();
            log.debug("Opening game service channel out : [{}]", gameServerQueueName);
            channelGame.queueDeclare(gameServerQueueName, false, false, false, null);
        } catch (IOException e) {
            e.printStackTrace();
            client.quit();
        }
    }

    public void closeConnection() throws IOException {
        if (null != channelClientIn) {
            channelClientIn.close();
        }
        if (null != channelUsers) {
            channelUsers.close();
        }
        if (null != channelLobby) {
            channelLobby.close();
        }
        if (null != channelMatchmaking) {
            channelMatchmaking.close();
        }
        if (null != channelGame) {
            channelGame.close();
        }
        if (null != channelPing) {
            channelPing.close();
        }
        if (null != connection) {
            connection.close();
        }
    }

    public Message receiveMessage() {
        Message result = null;
        if (null != channelClientConsumer) {
            try {
                QueueingConsumer.Delivery delivery = channelClientConsumer.nextDelivery(0);
                if (null != delivery) {
                    result = Message.loadFromBytes(delivery.getBody());
                    log.debug("Receive on [{}] : {}", clientQueueName, result.toString());
                    switch (result.getType()) {
                        case PING:
                            manageMessagePing(result);
                            result = null;
                            break;
                        case MAJONG:
                            manageMessageMajong(result);
                            result = null;
                            break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private void manageMessagePing(Message message) {
        MessagePing messagePing = (MessagePing) message;
        MessagePong messagePong = new MessagePong(messagePing.getTimestampServer(), clientContext.getTokenKey());
        sendMessageToPingService(messagePong);
    }

    private void manageMessageMajong(Message message) {
        MessageMajong messageMajong = (MessageMajong) message;
        this.ping = System.currentTimeMillis() - messageMajong.getTimestampClient();
        log.debug("ping: {}", ping);
    }


    public void sendMessageToUserService(Message message) {
        try {
            log.debug("Publish on [{}] : {}", USER_SERVICE_QUEUE_NAME, message.toString());
            channelUsers.basicPublish("", USER_SERVICE_QUEUE_NAME, null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            client.quit();
        }
    }

    public void sendMessageToLobbyService(Message message) {
        try {
            log.debug("Publish on [{}] : {}", LOBBY_SERVER_QUEUE_NAME_BASE, message.toString());
            channelLobby.basicPublish("", LOBBY_SERVER_QUEUE_NAME_BASE, null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            client.quit();
        }
    }

    public void sendMessageToMatchmakingService(Message message) {
        try {
            log.debug("Publish on [{}] : {}", MATCHMAKING_SERVER_QUEUE_NAME_BASE, message.toString());
            channelMatchmaking.basicPublish("", MATCHMAKING_SERVER_QUEUE_NAME_BASE, null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            client.quit();
        }
    }

    public void sendMessageToGameService(Message message) {
        try {
            log.debug("Publish on [{}] : {}", gameServerQueueName, message.toString());
            channelGame.basicPublish("", gameServerQueueName, null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            client.quit();
        }
    }

    public void sendMessageToPingService(Message message) {
        try {
            log.debug("Publish on [{}] : {}", PING_MANAGER_QUEUE_NAME, message.toString());
            channelPing.basicPublish("", PING_MANAGER_QUEUE_NAME, null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            client.quit();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
