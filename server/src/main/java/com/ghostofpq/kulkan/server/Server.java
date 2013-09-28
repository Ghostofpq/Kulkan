package com.ghostofpq.kulkan.server;

import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageAuthenticationRequest;
import com.ghostofpq.kulkan.entities.messages.MessageAuthenticationResponse;
import com.ghostofpq.kulkan.entities.messages.MessageErrorCode;
import com.ghostofpq.kulkan.server.authentification.AuthenticationManager;
import com.ghostofpq.kulkan.server.lobby.LobbyManager;
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

    private void init() throws IOException {
        initConnection();
        initDatabase();
    }

    private void initConnection() throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        connection = factory.newConnection();
        channelAuthenticating = connection.createChannel();
        channelAuthenticating.queueDeclare(authenticationQueueName, false, false, false, null);
        channelAuthenticating.basicQos(1);
        consumer = new QueueingConsumer(channelAuthenticating);
        channelAuthenticating.basicConsume(authenticationQueueName, false, consumer);

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
}
