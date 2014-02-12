package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.ClientContext;
import com.ghostofpq.kulkan.client.ClientMessenger;
import com.ghostofpq.kulkan.client.graphics.HUD.Button;
import com.ghostofpq.kulkan.client.graphics.HUD.TextArea;
import com.ghostofpq.kulkan.client.graphics.KeyValueRender;
import com.ghostofpq.kulkan.entities.inventory.item.Item;
import com.ghostofpq.kulkan.entities.inventory.item.ItemType;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.user.MessageEquipItemOnGameCharacter;
import com.ghostofpq.kulkan.entities.messages.user.MessagePlayerUpdate;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.input.Mouse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ManageGameCharacterEquipItemScene implements Scene {
    private ItemType filter;
    private KeyValueRender itemType;
    private Button quitButton;
    private Button equipButton;
    private Item selectedItem;
    private TextArea itemDescription;
    private KeyValueRender selectedItemName;
    private List<Button> itemButtonList;
    @Autowired
    private Client client;
    @Autowired
    private ClientContext clientContext;
    @Autowired
    private ClientMessenger clientMessenger;
    @Autowired
    private ManageGameCharacterEquipmentScene manageGameCharacterEquipmentScene;

    public ManageGameCharacterEquipItemScene() {
    }


    @Override
    public void init() {
        int widthSeparator = client.getWidth() / 20;
        int heightSeparator = client.getHeight() / 20;
        int widthStep = (client.getWidth() - 5 * widthSeparator) / 5;
        int heightStep = (client.getHeight() - 8 * heightSeparator) / 7;

        itemType = new KeyValueRender(widthSeparator, heightSeparator, widthStep * 2, heightStep, "Type", "0", 5);

        List<Item> itemList = client.getPlayer().getInventory().getItemsByType(filter);

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
                client.setCurrentScene(manageGameCharacterEquipmentScene);
            }
        };

    }

    public void equipItem(Item item) {
        MessageEquipItemOnGameCharacter messageEquipItemOnGameCharacter = new MessageEquipItemOnGameCharacter(client.getTokenKey(), clientContext.getSelectedCharacterId(), item.getItemID());
        clientMessenger.sendMessageToUserService(messageEquipItemOnGameCharacter);
    }

    public void selectItem(Item item) {
        selectedItem = item;
        selectedItemName.setValue(item.getName());
        itemDescription.clear();
        itemDescription.addLine(item.getDescription());
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
    public void receiveMessage() {
        Message message = clientMessenger.receiveMessage();
        if (null != message) {
            switch (message.getType()) {
                case PLAYER_UPDATE:
                    ManageGameCharacterEquipItemScene.log.debug("PLAYER_UPDATE");
                    MessagePlayerUpdate response = (MessagePlayerUpdate) message;
                    client.setPlayer(response.getPlayer());
                    client.setCurrentScene(manageGameCharacterEquipmentScene);
                    break;
            }
        }
    }

    public void setFilter(ItemType filter) {
        this.filter = filter;
    }
}
