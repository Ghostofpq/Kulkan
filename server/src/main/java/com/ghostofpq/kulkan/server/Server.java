package com.ghostofpq.kulkan.server;

import com.ghostofpq.kulkan.entities.messages.Message;
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
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        log.debug(" [*] Waiting for messages. To exit press CTRL+C");
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }

    private void receiveMessage() throws InterruptedException {
        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
        Message message = Message.loadFromBytes(delivery.getBody());
        log.debug(" [x] Received '{}'", message.getType());
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
