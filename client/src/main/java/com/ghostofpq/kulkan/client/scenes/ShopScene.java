package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.Button;
import com.ghostofpq.kulkan.client.graphics.KeyValueRender;
import com.ghostofpq.kulkan.client.graphics.TextArea;
import com.ghostofpq.kulkan.entities.inventory.ItemFactory;
import com.ghostofpq.kulkan.entities.inventory.item.Item;
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
    private Map<String, Integer> itemIdPriceMap;
    private Channel channelOut;
    private KeyValueRender money;
    private KeyValueRender selectedItemName;
    private KeyValueRender itemPrice;
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

        selectedItem = null;
        int widthStep = (Client.getInstance().getWidth() - 5 * widthSeparator) / 5;
        int heightStep = (Client.getInstance().getHeight() - 6 * heightSeparator) / 8;
        selectedItemName = new KeyValueRender(widthSeparator * 4 + 3 * widthStep, heightSeparator, widthStep * 2, heightStep, "Item", "0", 5);
        itemDescription = new TextArea(widthSeparator * 4 + 3 * widthStep, heightSeparator * 2 + heightStep, 15, 10);
        itemPrice = new KeyValueRender(widthSeparator * 4 + 3 * widthStep, heightSeparator * 3 + 5 * heightStep, widthStep * 2, heightStep, "Price", "0", 5);

        buyItem = new Button(widthSeparator * 4 + 3 * widthStep, heightSeparator * 4 + 6 * heightStep, widthStep * 2, heightStep, "Unlock Capacity") {
            @Override
            public void onClick() {
                buySelectedItem();
            }
        };

        quitButton = new

                Button(widthSeparator * 4 + 3 * widthStep, heightSeparator * 5 + 7 * heightStep, widthStep * 2, heightStep, "Back") {
                    @Override
                    public void onClick() {
                        Client.getInstance().setCurrentScene(GameCharacterManageScene.getInstance());
                    }
                };

        int widthOfCanvas = 3 * widthStep;
        int heightOfCanvas = Client.getInstance().getHeight();

        int widthStepOfCanvas = (widthOfCanvas - 6 * widthSeparator) / 4;
        int heightStepOfCanvas = (heightOfCanvas - 4 * heightSeparator) / 3;

        // it will be images, but for now names should do great
        List<String> itemNames = new ArrayList<String>();
        itemNames.add("Cloth armor");
        itemNames.add("Iron Helm");
        itemNames.add("Yew wand");
        itemNames.add("Stone club");
        itemNames.add("Sling");
        itemNames.add("Life Ring");
        itemNames.add("Strength Ring");
        itemNames.add("Will Necklace");
        itemNames.add("Agility Necklace");
        itemNames.add("Wooden Shield");
        itemNames.add("Two Handed Sword");

        int posX = widthSeparator;
        int posY = heightSeparator;
        for (final String itemId : itemNames) {
            Button button = new Button(posX, posY, widthStepOfCanvas, heightStepOfCanvas, itemId) {
                @Override
                public void onClick() {
                    setSelectedItem(itemId);
                }
            };
            buttons.add(button);

            posX += widthOfCanvas + widthSeparator;
            if (posX >= widthOfCanvas) {
                posX = widthSeparator;
                posY += heightOfCanvas + heightSeparator;
            }
        }
    }

    private void setSelectedItem(String itemId) {
        selectedItem = ItemFactory.createItem(itemId);
        selectedItemName.setValue(selectedItem.getName());
        itemDescription.clear();
        itemDescription.addLine(selectedItem.getDescription());
        itemPrice.setValue(itemIdPriceMap.get(itemId).toString());
    }

    private void buySelectedItem() {

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
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                for (Button button : buttons) {
                    if (button.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                        button.onClick();
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
    }
}
