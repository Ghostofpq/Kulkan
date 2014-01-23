package com.ghostofpq.kulkan.server.database;

import com.ghostofpq.kulkan.entities.inventory.item.Item;
import com.ghostofpq.kulkan.entities.inventory.item.ItemType;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.item.MessageItemsByTypeRequest;
import com.ghostofpq.kulkan.entities.messages.item.MessageItemsByTypeResponse;
import com.ghostofpq.kulkan.server.database.controller.ItemController;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

@Slf4j
public class ItemService implements Runnable {
    private static final String CLIENT_QUEUE_NAME_BASE = "/client/";
    private final String serviceQueueName = "items";
    // PARAMETERS - SPRING
    private String hostIp;
    private Integer hostPort;
    @Autowired
    private ItemController itemController;
    // MESSAGING
    private QueueingConsumer consumer;
    private Connection connection;
    private Channel channelServiceIn;
    private Channel channelServiceOut;
    // THREAD ROUTINE
    private boolean requestClose;

    private ItemService() {
        requestClose = false;
    }

    private void manageMessageItemsByTypeRequest(Message message) throws IOException {
        MessageItemsByTypeRequest messageItemsByTypeRequest = (MessageItemsByTypeRequest) message;

        String tokenKey = messageItemsByTypeRequest.getKeyToken();
        ItemType itemType = messageItemsByTypeRequest.getItemType();

        log.debug("Received a Items by type request");
        log.debug("TokenKey : '{}'", tokenKey);
        log.debug("Item Type : '{}'", itemType);

        if (null != tokenKey && null != itemType) {
            List<Item> response = itemController.getByItemType(messageItemsByTypeRequest.getItemType());
            MessageItemsByTypeResponse messageItemsByTypeResponse = new MessageItemsByTypeResponse(response);
            String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(tokenKey).toString();
            channelServiceOut.queueDeclare(queueName, false, false, false, null);
            channelServiceOut.basicPublish("", queueName, null, messageItemsByTypeResponse.getBytes());
        }
    }

    public void initConnection() throws IOException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostIp);
        factory.setPort(hostPort);
        log.debug("{}:{}", hostIp, hostPort);
        connection = factory.newConnection();
        channelServiceIn = connection.createChannel();
        channelServiceIn.queueDeclare(serviceQueueName, false, false, false, null);
        channelServiceIn.basicQos(1);
        consumer = new QueueingConsumer(channelServiceIn);
        channelServiceIn.basicConsume(serviceQueueName, true, consumer);
        channelServiceOut = connection.createChannel();
    }

    private void receiveMessage() throws InterruptedException, IOException {
        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
        if (null != delivery) {
            Message message = Message.loadFromBytes(delivery.getBody());
            log.debug(" [x] RECEIVED '{}' ON {}", message.getType(), serviceQueueName);
            switch (message.getType()) {
                case GET_ITEMS_BY_TYPE_REQUEST:
                    manageMessageItemsByTypeRequest(message);
                    break;
                default:
                    log.error(" [X] UNEXPECTED MESSAGE : {}", message.getType());
                    break;
            }
        }
    }

    // THREAD ROUTINE
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
        try {
            channelServiceIn.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // SPRING
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
