package com.ghostofpq.kulkan.server;

import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageAuthenticationRequest;
import com.ghostofpq.kulkan.entities.messages.MessageAuthenticationResponse;
import com.ghostofpq.kulkan.entities.messages.MessageErrorCode;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Server {
    private static volatile Server instance = null;
    private final String HOST = "localhost";
    private final String QUEUE_NAME = "hello";
    private String authenticationQueueName = "authentication";
    private boolean requestClose;
    private QueueingConsumer consumer;
    private Connection connection;
    private Channel channel;

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
        Server s = new Server();
        s.init();
        s.run();
    }

    private void init() throws IOException {
        initConnection();
    }

    private void initConnection() throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(authenticationQueueName, false, false, false, null);
        channel.basicQos(1);
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(authenticationQueueName, false, consumer);

        log.debug(" [*] Waiting for messages. To exit press CTRL+C");
    }

    private void receiveMessage() throws InterruptedException, IOException {
        QueueingConsumer.Delivery delivery = consumer.nextDelivery();

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

                MessageAuthenticationResponse authenticationResponse = new MessageAuthenticationResponse(
                        authenticationRequest.getPseudo(),
                        authenticationRequest.getPassword(),
                        "123456",
                        MessageErrorCode.OK);

                channel.basicPublish("", props.getReplyTo(), replyProps, authenticationResponse.getBytes());
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                log.debug(" corrId: {}", props.getCorrelationId());
                log.debug(" [x] Sent '{}'", authenticationResponse.getType());
        }
    }

    public void run() throws IOException, InterruptedException {
        while (!requestClose) {
            receiveMessage();
        }
        channel.close();
        connection.close();
    }

    public void shutDown() {
        requestClose = true;
    }
}
