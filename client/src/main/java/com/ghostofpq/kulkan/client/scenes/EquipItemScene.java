package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.HUD.Button;
import com.ghostofpq.kulkan.client.graphics.HUD.TextArea;
import com.ghostofpq.kulkan.client.graphics.KeyValueRender;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.inventory.item.Item;
import com.ghostofpq.kulkan.entities.inventory.item.ItemType;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.user.MessageEquipItemOnGameCharacter;
import com.ghostofpq.kulkan.entities.messages.user.MessagePlayerUpdate;
import com.rabbitmq.client.Channel;
import org.bson.types.ObjectId;
import org.lwjgl.input.Mouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EquipItemScene implements Scene {
    private static final Logger LOG = LoggerFactory.getLogger(EquipItemScene.class);
    private static volatile EquipItemScene instance = null;
    private final String USER_SERVICE_QUEUE_NAME = "users";
    private Channel channelOut;
    private ItemType filter;
    private KeyValueRender itemType;
    private ObjectId gameCharId;
    private Button quitButton;
    private Button equipButton;
    private Item selectedItem;
    private TextArea itemDescription;
    private KeyValueRender selectedItemName;
    private int widthSeparator = Client.getInstance().getWidth() / 20;
    private int heightSeparator = Client.getInstance().getHeight() / 20;
    private List<Button> itemButtonList;

    private EquipItemScene() {
    }

    public static EquipItemScene getInstance() {
        if (instance == null) {
            synchronized (EquipItemScene.class) {
                if (instance == null) {
                    instance = new EquipItemScene();
                }
            }
        }
        return instance;
    }

    @Override
    public void init() {
        int widthStep = (Client.getInstance().getWidth() - 5 * widthSeparator) / 5;
        int heightStep = (Client.getInstance().getHeight() - 8 * heightSeparator) / 7;

        itemType = new KeyValueRender(widthSeparator, heightSeparator, widthStep * 2, heightStep, "Type", "0", 5);

        List<Item> itemList = Client.getInstance().getPlayer().getInventory().getItemsByType(filter);

        itemButtonList = new ArrayList<Button>();

        for (int i = 0; i < itemList.size(); i++) {
            final Item item = itemList.get(i);
            int line = i / 3;
            int col = i % 3;
            Button itemButton = new Button((col + 1) * widthSeparator + (col) * widthStep, (1 + line) * heightSeparator + (1 + line) * heightStep, widthStep, heightStep, item.getName()) {
                @Override
                public void onClick() {
                    selectItem(item);
                }
            };
            itemButtonList.add(itemButton);
        }

        selectedItemName = new KeyValueRender(widthSeparator * 4 + 3 * widthStep, heightSeparator, widthStep * 2, heightStep, "Item", "0", 5);
        itemDescription = new TextArea(widthSeparator * 4 + 3 * widthStep, heightSeparator * 2 + heightStep, widthStep * 2, heightStep, "optimus_princeps_16");

        equipButton = new Button(widthSeparator * 4 + 3 * widthStep, heightSeparator * 6 + 5 * heightStep, widthStep * 2, heightStep, "Equip") {
            @Override
            public void onClick() {
                equipItem(selectedItem);
            }
        };

        quitButton = new Button(widthSeparator * 4 + 3 * widthStep, heightSeparator * 7 + 6 * heightStep, widthStep * 2, heightStep, "Back") {
            @Override
            public void onClick() {
                Client.getInstance().setCurrentScene(Client.getInstance().getLobbyScene());
            }
        };

    }

    public void equipItem(Item item) {
        MessageEquipItemOnGameCharacter messageEquipItemOnGameCharacter = new MessageEquipItemOnGameCharacter(Client.getInstance().getTokenKey(), gameCharId, item.getItemID());
        try {
            channelOut.basicPublish("", USER_SERVICE_QUEUE_NAME, null, messageEquipItemOnGameCharacter.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectItem(Item item) {
        selectedItem = item;
        selectedItemName.setValue(item.getName());
        itemDescription.clear();
        itemDescription.addLine(item.getDescription());
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
        itemType.draw();
        if (selectedItem != null) {
            selectedItemName.draw();
            itemDescription.draw();
            equipButton.draw();
        }
        for (Button button : itemButtonList) {
            button.draw();
        }
        quitButton.draw();
    }

    @Override
    public void manageInput() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                if (quitButton.isClicked()) {
                    quitButton.onClick();
                } else if (equipButton.isClicked()) {
                    if (selectedItem != null) {
                        equipButton.onClick();
                    }
                } else {
                    for (Button button : itemButtonList) {
                        if (button.isClicked()) {
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
        LOG.debug("channelOut closed");
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
                    GameCharacter gameCharacter = response.getPlayer().getGameCharWithId(gameCharId);
                    ManageEquipementScene.getInstance().setGameCharacter(gameCharacter);
                    GameCharacterManageScene.getInstance().setGameCharacter(gameCharacter);
                    Client.getInstance().setCurrentScene(ManageEquipementScene.getInstance());
                    break;
            }
        }
    }

    public void setFilter(ItemType filter) {
        this.filter = filter;
    }

    public void setGameCharId(ObjectId gameCharId) {
        this.gameCharId = gameCharId;
    }
}
