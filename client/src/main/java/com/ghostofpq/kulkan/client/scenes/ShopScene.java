package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.ClientContext;
import com.ghostofpq.kulkan.client.ClientMessenger;
import com.ghostofpq.kulkan.client.graphics.HUD.Button;
import com.ghostofpq.kulkan.client.graphics.HUD.TextArea;
import com.ghostofpq.kulkan.client.graphics.KeyValueRender;
import com.ghostofpq.kulkan.entities.inventory.ItemFactory;
import com.ghostofpq.kulkan.entities.inventory.item.Item;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.user.MessageBuyItem;
import com.ghostofpq.kulkan.entities.messages.user.MessagePlayerUpdate;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.input.Mouse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ShopScene implements Scene {
    private KeyValueRender money;
    private KeyValueRender selectedItemName;
    private KeyValueRender itemPrice;
    private KeyValueRender itemStock;
    private Button buyItem;
    private Button quitButton;
    private Item selectedItem;
    private TextArea itemDescription;
    private int widthSeparator;
    private int heightSeparator;
    private List<Button> buttons;
    @Autowired
    private Client client;
    @Autowired
    private ClientContext clientContext;
    @Autowired
    private ClientMessenger clientMessenger;

    public ShopScene() {
    }


    @Override
    public void init() {
        widthSeparator = client.getWidth() / 20;
        heightSeparator = client.getHeight() / 20;
        buttons = new ArrayList<Button>();
        selectedItem = null;
        int widthStep = (client.getWidth() - widthSeparator) / 5;
        int heightStep = (client.getHeight() - 8 * heightSeparator) / 7;
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
                client.setCurrentScene(client.getLobbyScene());
            }
        };

        int widthOfCanvas = 3 * widthStep;
        int heightOfCanvas = client.getHeight();

        int widthStepOfCanvas = (widthOfCanvas - 4 * widthSeparator) / 3;
        int heightStepOfCanvas = (heightOfCanvas - 5 * heightSeparator) / 4;

        // it will be images, but for now names should do great
        Map<String, String> itemNamesToId = new HashMap<String, String>();
        itemNamesToId.put("000", "Cloth armor");
        itemNamesToId.put("001", "Iron Helm");
        itemNamesToId.put("002", "Yew wand");
        itemNamesToId.put("003", "Stone club");
        itemNamesToId.put("004", "Sling");
        itemNamesToId.put("005", "Life Ring");
        itemNamesToId.put("006", "Strength Ring");
        itemNamesToId.put("007", "Will Necklace");
        itemNamesToId.put("008", "Agility Necklace");
        itemNamesToId.put("009", "Wooden Shield");
        itemNamesToId.put("010", "Two Handed Sword");

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
        itemStock.setValue(String.valueOf(clientContext.getPlayer().getInventory().getNumberOf(itemId)));
        money.setValue(String.valueOf(clientContext.getPlayer().getMoney()));
    }

    private void buySelectedItem() {
        MessageBuyItem messageBuyItem = new MessageBuyItem(client.getTokenKey(), selectedItem.getItemID());
        clientMessenger.sendMessageToUserService(messageBuyItem);
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
                if (quitButton.isClicked()) {
                    quitButton.onClick();
                } else if (buyItem.isClicked()) {
                    buyItem.onClick();
                } else {
                    for (Button button : buttons) {
                        if (button.isClicked()) {
                            button.onClick();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void receiveMessage() {
        Message message = clientMessenger.receiveMessage();
        if (null != message) {
            switch (message.getType()) {
                case PLAYER_UPDATE:
                    log.debug("PLAYER_UPDATE");
                    MessagePlayerUpdate response = (MessagePlayerUpdate) message;
                    clientContext.setPlayer(response.getPlayer());
                    itemStock.setValue(String.valueOf(clientContext.getPlayer().getInventory().getNumberOf(selectedItem.getItemID())));
                    money.setValue(String.valueOf(clientContext.getPlayer().getMoney()));
                    break;
            }
        }
    }
}
