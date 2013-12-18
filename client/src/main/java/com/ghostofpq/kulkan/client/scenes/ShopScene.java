package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ShopScene implements Scene {
    private static final Logger LOG = LoggerFactory.getLogger(ShopScene.class);
    private static volatile ShopScene instance = null;
    private final String USER_SERVICE_QUEUE_NAME = "users";
    private Map<String, Integer> itemIdPriceMap;
    private Channel channelOut;

    private ShopScene() {
    }

    public static ShopScene getInstance() {
        if (instance == null) {
            synchronized (ShopScene.class) {
                if (instance == null) {
                    instance = new ShopScene();
                }
            }
        }
        return instance;
    }

    @Override
    public void init() {
        itemIdPriceMap = new HashMap<String, Integer>();
        itemIdPriceMap.put("000", 10);
        itemIdPriceMap.put("001", 10);
        itemIdPriceMap.put("002", 10);
        itemIdPriceMap.put("003", 10);
        itemIdPriceMap.put("004", 10);
        itemIdPriceMap.put("005", 10);
        itemIdPriceMap.put("006", 10);
        itemIdPriceMap.put("007", 10);
        itemIdPriceMap.put("008", 10);
        itemIdPriceMap.put("009", 10);
        itemIdPriceMap.put("010", 10);
        itemIdPriceMap.put("011", 10);
    }

    @Override
    public void initConnections() throws IOException {
        channelOut = Client.getInstance().getConnection().createChannel();
        channelOut.queueDeclare(USER_SERVICE_QUEUE_NAME, false, false, false, null);
    }

    @Override
    public void update(long deltaTime) {
    }

    @Override
    public void render() {
    }

    @Override
    public void manageInput() {
    }

    @Override
    public void closeConnections() throws IOException {
        channelOut.close();
    }

    @Override
    public void receiveMessage() {
    }
}
