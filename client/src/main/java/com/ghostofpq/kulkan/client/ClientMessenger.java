package com.ghostofpq.kulkan.client;

import com.ghostofpq.kulkan.entities.messages.Message;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Slf4j
public class ClientMessenger {
    // QUEUE NAMES
    private final String USER_SERVICE_QUEUE_NAME = "server/users";
    private final String LOBBY_SERVER_QUEUE_NAME_BASE = "server/lobby";
    private final String MATCHMAKING_SERVER_QUEUE_NAME_BASE = "server/matchmaking";
    private final String CLIENT_QUEUE_NAME_BASE = "client/";
    private final String GAME_SERVER_QUEUE_NAME_BASE = "server/game/";
    private String gameServerQueueName;
    private String clientQueueName;
    @Autowired
    private ClientContext clientContext;
    // CHANNELS
    private Connection connection;
    private Channel channelUsers;
    private Channel channelLobby;
    private Channel channelMatchmaking;
    private Channel channelClientIn;
    private Channel channelGame;
    private QueueingConsumer channelClientConsumer;
    // SERVER
    private String hostIp;
    private int hostPort;

    public ClientMessenger() {
    }

    public void initConnection() throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        log.debug("Opening connection on [{}:{}}", hostIp, hostPort);
        factory.setHost(hostIp);
        factory.setPort(hostPort);
        connection = factory.newConnection();
    }

    public void openChannelsAfterAuthentication() throws IOException {
        log.debug("Opening channels after authentication");

        clientQueueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(clientContext.getTokenKey()).toString();
        channelClientIn = connection.createChannel();
        log.debug("Opening client channel in : [{}]", clientQueueName);
        channelClientIn.queueDeclare(clientQueueName, false, false, false, null);
        channelClientConsumer = new QueueingConsumer(channelClientIn);
        channelClientIn.basicConsume(clientQueueName, true, channelClientConsumer);

        channelUsers = connection.createChannel();
        log.debug("Opening user service channel out : [{}]", USER_SERVICE_QUEUE_NAME);
        channelUsers.queueDeclare(USER_SERVICE_QUEUE_NAME, false, false, false, null);

        channelLobby = connection.createChannel();
        log.debug("Opening lobby service channel out : [{}]", LOBBY_SERVER_QUEUE_NAME_BASE);
        channelLobby.queueDeclare(LOBBY_SERVER_QUEUE_NAME_BASE, false, false, false, null);

        channelMatchmaking = connection.createChannel();
        log.debug("Opening matchmaking service channel out : [{}]", MATCHMAKING_SERVER_QUEUE_NAME_BASE);
        channelMatchmaking.queueDeclare(MATCHMAKING_SERVER_QUEUE_NAME_BASE, false, false, false, null);
    }

    public void openChannelGame(String gameNumber) throws IOException {
        gameServerQueueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(gameNumber).toString();
        channelGame = connection.createChannel();
        log.debug("Opening game service channel out : [{}]", gameServerQueueName);
        channelGame.queueDeclare(gameServerQueueName, false, false, false, null);
    }

    public Message receiveMessage() {
        Message result = null;
        if (null != channelClientConsumer) {
            try {
                QueueingConsumer.Delivery delivery = channelClientConsumer.nextDelivery(0);
                if (null != delivery) {
                    result = Message.loadFromBytes(delivery.getBody());
                    log.debug("Receive on [{}] : {}", clientQueueName, result.toString());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public void sendMessageToUserService(Message message) throws IOException {
        log.debug("Publish on [{}] : {}", USER_SERVICE_QUEUE_NAME, message.toString());
        channelUsers.basicPublish("", USER_SERVICE_QUEUE_NAME, null, message.getBytes());
    }

    public void sendMessageToLobbyService(Message message) throws IOException {
        log.debug("Publish on [{}] : {}", LOBBY_SERVER_QUEUE_NAME_BASE, message.toString());
        channelLobby.basicPublish("", LOBBY_SERVER_QUEUE_NAME_BASE, null, message.getBytes());
    }

    public void sendMessageToMatchmakingService(Message message) throws IOException {
        log.debug("Publish on [{}] : {}", MATCHMAKING_SERVER_QUEUE_NAME_BASE, message.toString());
        channelMatchmaking.basicPublish("", MATCHMAKING_SERVER_QUEUE_NAME_BASE, null, message.getBytes());
    }

    public void sendMessageToGameService(Message message) throws IOException {
        log.debug("Publish on [{}] : {}", gameServerQueueName, message.toString());
        channelGame.basicPublish("", gameServerQueueName, null, message.getBytes());
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }
}
