package com.ghostofpq.kulkan.server;

import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.battlefield.Battlefield;
import com.ghostofpq.kulkan.entities.battlefield.BattlefieldElement;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.auth.MessageAuthenticationRequest;
import com.ghostofpq.kulkan.entities.messages.auth.MessageAuthenticationResponse;
import com.ghostofpq.kulkan.entities.messages.auth.MessageErrorCode;
import com.ghostofpq.kulkan.server.authentification.AuthenticationManager;
import com.ghostofpq.kulkan.server.game.GameManager;
import com.ghostofpq.kulkan.server.lobby.LobbyManager;
import com.ghostofpq.kulkan.server.matchmaking.MatchmakingManager;
import com.ghostofpq.kulkan.server.utils.SaveManager;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.UnknownHostException;

@Slf4j
public class Server {
    private static volatile Server instance = null;
    private final String HOST = "localhost";
    private String authenticationQueueName = "authentication";
    private boolean requestClose;
    private QueueingConsumer consumer;
    private Connection connection;
    private Channel channelAuthenticating;
    private MongoClient mongoClient;
    private DB db;

    private Server() {
        //createMAp();
        requestClose = false;
    }

    public static Server getInstance() {
        if (instance == null) {
            synchronized (Server.class) {
                if (instance == null) {
                    instance = new Server();
                }
            }
        }
        return instance;
    }

    public static void main(String[] argv) throws IOException, InterruptedException {
        Server s = Server.getInstance();
        s.init();
        s.run();
    }

    private void init() throws IOException, InterruptedException {
        initConnection();
        initDatabase();
    }

    private void initConnection() throws IOException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        connection = factory.newConnection();
        channelAuthenticating = connection.createChannel();
        channelAuthenticating.queueDeclare(authenticationQueueName, false, false, false, null);
        channelAuthenticating.basicQos(1);
        consumer = new QueueingConsumer(channelAuthenticating);
        channelAuthenticating.basicConsume(authenticationQueueName, false, consumer);
        QueueingConsumer.Delivery delivery = consumer.nextDelivery(1);
        while (null != delivery) {
            delivery = consumer.nextDelivery(1);
        }
        log.debug(" [*] Waiting for messages. To exit press CTRL+C");
    }

    private void initDatabase() throws UnknownHostException {
        mongoClient = new MongoClient("localhost", 27017);
        db = mongoClient.getDB("kulkan");
    }

    private void receiveMessage() throws InterruptedException, IOException {
        QueueingConsumer.Delivery delivery = consumer.nextDelivery(0);
        if (null != delivery) {
            log.debug(" [-] RECEIVED MESSAGE ON : {}", authenticationQueueName);
            BasicProperties props = delivery.getProperties();
            BasicProperties replyProps = new BasicProperties.Builder()
                    .correlationId(props.getCorrelationId())
                    .build();

            Message message = Message.loadFromBytes(delivery.getBody());
            log.debug(" [x] Received '{}'", message.getType());

            switch (message.getType()) {
                case AUTHENTICATION_REQUEST:
                    //TODO do stuff;
                    MessageAuthenticationRequest authenticationRequest = (MessageAuthenticationRequest) message;
                    boolean authenticationResult = AuthenticationManager.getInstance().authenticate(authenticationRequest.getPseudo(),
                            authenticationRequest.getPassword());
                    String tokenKey = AuthenticationManager.getInstance().getTokenKeyFor(authenticationRequest.getPseudo());

                    MessageErrorCode code;
                    if (authenticationResult) {
                        code = MessageErrorCode.OK;
                        LobbyManager.getInstance().addClient(tokenKey);
                    } else {
                        code = MessageErrorCode.BAD_LOGIN_INFORMATIONS;
                    }


                    MessageAuthenticationResponse authenticationResponse = new MessageAuthenticationResponse(
                            authenticationRequest.getPseudo(),
                            authenticationRequest.getPassword(),
                            tokenKey,
                            code);

                    channelAuthenticating.basicPublish("", props.getReplyTo(), replyProps, authenticationResponse.getBytes());
                    channelAuthenticating.basicAck(delivery.getEnvelope().getDeliveryTag(), false);


                    log.debug(" [x] Sent '{}'", authenticationResponse.getType());
            }
        }
    }

    public void run() throws IOException, InterruptedException {
        while (!requestClose) {
            receiveMessage();
            LobbyManager.getInstance().run();
            MatchmakingManager.getInstance().run();
            GameManager.getInstance().run();
        }
        channelAuthenticating.close();
        connection.close();
    }

    public void shutDown() {
        requestClose = true;
    }

    public Connection getConnection() {
        return connection;
    }

    public DB getDb() {
        return db;
    }

    public void createMAp() {

        int length = 10;
        int height = 5;
        int depth = 10;

        Battlefield battlefield = new Battlefield(length, height, depth, 2);

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < depth; j++) {
                battlefield.addBattlefieldElement(i, 0, j, BattlefieldElement.BattlefieldElementType.BLOC);
            }
        }

        for (int i = 0; i < length; i++) {
            Position position = new Position(i, 0, 0);
            battlefield.addDeployementZone(0, position);
            Position position2 = new Position(i, 0, depth - 1);
            battlefield.addDeployementZone(1, position2);
        }
        battlefield.setStartingPointsOfViewForPlayer(0, PointOfView.NORTH);
        battlefield.setStartingPointsOfViewForPlayer(1, PointOfView.SOUTH);

        battlefield.addBattlefieldElement(0, 1, 0, BattlefieldElement.BattlefieldElementType.BLOC);
        battlefield.addBattlefieldElement(0, 2, 1, BattlefieldElement.BattlefieldElementType.BLOC);
        battlefield.addBattlefieldElement(0, 3, 2, BattlefieldElement.BattlefieldElementType.BLOC);

        SaveManager saveManager = SaveManager.getInstance();
        saveManager.saveMap(battlefield, "mapTest1");

    }
}
