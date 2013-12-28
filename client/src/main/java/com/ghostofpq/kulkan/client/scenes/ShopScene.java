package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.Button;
import com.ghostofpq.kulkan.client.graphics.KeyValueRender;
import com.ghostofpq.kulkan.client.graphics.TextArea;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.inventory.ItemFactory;
import com.ghostofpq.kulkan.entities.inventory.item.Item;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.user.MessageBuyItem;
import com.ghostofpq.kulkan.entities.messages.user.MessagePlayerUpdate;
import com.rabbitmq.client.Channel;
import org.lwjgl.input.Mouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopScene implements Scene {
    private static final Logger LOG = LoggerFactory.getLogger(ShopScene.class);
    private static volatile ShopScene instance = null;
    private final String USER_SERVICE_QUEUE_NAME = "users";
    private Channel channelOut;
    private KeyValueRender money;
    private KeyValueRender selectedItemName;
    private KeyValueRender itemPrice;
    private KeyValueRender itemStock;
    private Button buyItem;
    private Button quitButton;
    private Item selectedItem;
    private TextArea itemDescription;
    private int widthSeparator = Client.getInstance().getWidth() / 20;
    private int heightSeparator = Client.getInstance().getHeight() / 20;
    private List<Button> buttons;

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
        buttons = new ArrayList<Button>();
        selectedItem = null;
        int widthStep = (Client.getInstance().getWidth() - widthSeparator) / 5;
        int heightStep = (Client.getInstance().getHeight() - 8 * heightSeparator) / 7;
        selectedItemName = new KeyValueRender(widthSeparator + 3 * widthStep, heightSeparator, widthStep * 2, heightStep, "Item", "0", 5);
        itemDescription = new TextArea(widthSeparator + 3 * widthStep, heightSeparator * 2 + 1 * heightStep, widthStep * 2, heightStep, "optimus_princeps_16");
        itemPrice = new KeyValueRender(widthSeparator + 3 * widthStep, heightSeparator * 3 + 2 * heightStep, widthStep * 2, heightStep, "Price", "0", 5);
        money = new KeyValueRender(widthSeparator + 3 * widthStep, heightSeparator * 4 + 3 * heightStep, widthStep * 2, heightStep, "Money", "0", 5);
        itemStock = new KeyValueRender(widthSeparator + 3 * widthStep, heightSeparator * 5 + 4 * heightStep, widthStep * 2, heightStep, "Stock", "0", 5);
        buyItem = new Button(widthSeparator + 3 * widthStep, heightSeparator * 6 + 5 * heightStep, widthStep * 2, heightStep, "Buy") {
            @Override
            public void onClick() {
                buySelectedItem();
            }
        };

        quitButton = new Button(widthSeparator + 3 * widthStep, heightSeparator * 7 + 6 * heightStep, widthStep * 2, heightStep, "Back") {
            @Override
            public void onClick() {
                Client.getInstance().setCurrentScene(LobbyScene.getInstance());
            }
        };

        int widthOfCanvas = 3 * widthStep;
        int heightOfCanvas = Client.getInstance().getHeight();

        int widthStepOfCanvas = (widthOfCanvas - 4 * widthSeparator) / 3;
        int heightStepOfCanvas = (heightOfCanvas - 5 * heightSeparator) / 4;

        // it will be images, but for now names should do great
        Map<String, String> itemNamesToId = new HashMap<String, String>();
        itemNamesToId.put("000","Cloth armor");
        itemNamesToId.put("001","Iron Helm");
        itemNamesToId.put("002","Yew wand");
        itemNamesToId.put("003","Stone club");
        itemNamesToId.put("004","Sling");
        itemNamesToId.put("005","Life Ring");
        itemNamesToId.put("006","Strength Ring");
        itemNamesToId.put("007","Will Necklace");
        itemNamesToId.put("008","Agility Necklace");
        itemNamesToId.put("009","Wooden Shield");
        itemNamesToId.put("010","Two Handed Sword");

        int posX = widthSeparator;
        int posY = heightSeparator;
        for (final String itemId : itemNamesToId.keySet()) {
            final String itemName = itemNamesToId.get(itemId);
            Button button = new Button(posX, posY, widthStepOfCanvas, heightStepOfCanvas, itemName) {
                @Override
                public void onClick() {
                    setSelectedItem(itemId);
                }
            };
            buttons.add(button);

            posX += widthStepOfCanvas + widthSeparator;
            if (posX + widthStepOfCanvas >= widthOfCanvas) {
                posX = widthSeparator;
                posY += heightStepOfCanvas + heightSeparator;
            }
        }
    }

    private void setSelectedItem(String itemId) {
        selectedItem = ItemFactory.createItem(itemId);
        selectedItemName.setValue(selectedItem.getName());
        itemDescription.clear();
        itemDescription.addLine(selectedItem.getDescription());
        itemPrice.setValue(String.valueOf(selectedItem.getPrice()));
        itemStock.setValue(String.valueOf(Client.getInstance().getPlayer().getInventory().getNumberOf(itemId)));
        money.setValue(String.valueOf(Client.getInstance().getPlayer().getMoney()));
    }

    private void buySelectedItem() {
        MessageBuyItem messageBuyItem = new MessageBuyItem(Client.getInstance().getTokenKey(), selectedItem.getItemID());
        try {
            LOG.debug("Sending ");
            channelOut.basicPublish("", USER_SERVICE_QUEUE_NAME, null, messageBuyItem.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        for (Button button : buttons) {
            button.draw();
        }

        if (null != selectedItem) {
            itemPrice.draw();
            selectedItemName.draw();
            itemDescription.draw();
            itemStock.draw();
            buyItem.draw();
            money.draw();
        }
        quitButton.draw();
    }

    @Override
    public void manageInput() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                if (quitButton.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    quitButton.onClick();
                } else if (buyItem.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    buyItem.onClick();
                } else {
                    for (Button button : buttons) {
                        if (button.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                            button.onClick();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void closeConnections() throws IOException {
        channelOut.close();
    }

    @Override
    public void receiveMessage() {
        Message message = Client.getInstance().receiveMessage();
        if (null != message) {
            switch (message.getType()) {
                case PLAYER_UPDATE:
                    LOG.debug("PLAYER_UPDATE");
                    MessagePlayerUpdate response = (MessagePlayerUpdate) message;
                    Client.getInstance().setPlayer(response.getPlayer());
                    itemStock.setValue(String.valueOf(Client.getInstance().getPlayer().getInventory().getNumberOf(selectedItem.getItemID())));
                    money.setValue(String.valueOf(Client.getInstance().getPlayer().getMoney()));
                    break;
            }
        }
    }
}
