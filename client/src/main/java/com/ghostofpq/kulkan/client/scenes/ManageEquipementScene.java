package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.Button;
import com.ghostofpq.kulkan.client.graphics.KeyValueRender;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.inventory.item.ItemType;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.user.MessagePlayerUpdate;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManageEquipementScene implements Scene {
    private static final Logger LOG = LoggerFactory.getLogger(ManageEquipementScene.class);
    private final String USER_SERVICE_QUEUE_NAME = "users";
    private GameCharacter gameCharacter;
    private Channel channelOut;
    private KeyValueRender nameRender;
    private KeyValueRender helm;
    private KeyValueRender armor;
    private KeyValueRender necklace;
    private KeyValueRender ring;
    private KeyValueRender weapon;
    private KeyValueRender heldItem;
    private List<Button> equipButtonList;
    private List<Button> unequipButtonList;
    private Button quitButton;

    public void setGameCharacter(GameCharacter gameCharacter) {
        this.gameCharacter = gameCharacter;
    }

    @Override
    public void init() {
        int widthSeparator = Client.getInstance().getWidth() / 20;
        int heightSeparator = Client.getInstance().getHeight() / 20;

        int widthStep = (Client.getInstance().getWidth() - 4 * widthSeparator) / 5;
        int heightStep = (Client.getInstance().getHeight() - 9 * heightSeparator) / 8;

        nameRender = new KeyValueRender(widthSeparator, heightSeparator, widthStep, heightStep, "Char Name", gameCharacter.getName(), 5);

        helm = new KeyValueRender(widthSeparator, heightSeparator * 2 + heightStep, widthStep * 3, heightStep, "Helm", "", 5);
        armor = new KeyValueRender(widthSeparator, heightSeparator * 3 + heightStep * 2, widthStep * 3, heightStep, "Armor", "", 5);
        necklace = new KeyValueRender(widthSeparator, heightSeparator * 4 + heightStep * 3, widthStep * 3, heightStep, "Necklace", "", 5);
        ring = new KeyValueRender(widthSeparator, heightSeparator * 5 + heightStep * 4, widthStep * 3, heightStep, "Ring", "", 5);
        weapon = new KeyValueRender(widthSeparator, heightSeparator * 6 + heightStep * 5, widthStep * 3, heightStep, "Weapon", "", 5);
        heldItem = new KeyValueRender(widthSeparator, heightSeparator * 7 + heightStep * 6, widthStep * 3, heightStep, "HeldItem", "", 5);

        if (null != gameCharacter.getEquipment().getHelm()) {
            helm.setValue(gameCharacter.getEquipment().getHelm().getName());
        }
        if (null != gameCharacter.getEquipment().getArmor()) {
            armor.setValue(gameCharacter.getEquipment().getArmor().getName());
        }
        if (null != gameCharacter.getEquipment().getNecklace()) {
            necklace.setValue(gameCharacter.getEquipment().getNecklace().getName());
        }
        if (null != gameCharacter.getEquipment().getRing()) {
            ring.setValue(gameCharacter.getEquipment().getRing().getName());
        }
        if (null != gameCharacter.getEquipment().getWeapon()) {
            weapon.setValue(gameCharacter.getEquipment().getWeapon().getName());
        }
        if (null != gameCharacter.getEquipment().getHeldItem()) {
            heldItem.setValue(gameCharacter.getEquipment().getHeldItem().getName());
        }
        List<ItemType> itemTypes = new ArrayList<ItemType>();
        itemTypes.add(0, ItemType.HELMET);
        itemTypes.add(1, ItemType.ARMOR);
        itemTypes.add(2, ItemType.NECKLACE);
        itemTypes.add(3, ItemType.RING);
        itemTypes.add(4, ItemType.WEAPON);
        itemTypes.add(5, ItemType.HELD_ITEM);
        for (int i = 0; i < 6; i++) {
            final ItemType buttonItemType = itemTypes.get(i);

            Button buttonEquip = new Button(widthSeparator * 2 + widthStep * 3, heightSeparator * (i + 2) + heightStep * (i + 1), widthStep, heightStep, "Equip") {
                @Override
                public void onClick() {
                    changeToSceneEquipItem(buttonItemType);
                }
            };
            equipButtonList.add(buttonEquip);
            Button buttonUnequip = new Button(widthSeparator * 3 + widthStep * 4, heightSeparator * (i + 2) + heightStep * (i + 1), widthStep, heightStep, "Unequip") {
                @Override
                public void onClick() {
                    unequipItemType(buttonItemType);
                }
            };
            unequipButtonList.add(buttonUnequip);
        }
        quitButton = new Button(widthSeparator * 3 + widthStep * 4, heightSeparator * 8 + heightStep * 7, widthStep, heightStep, "Back") {
            @Override
            public void onClick() {
                Client.getInstance().setCurrentScene(GameCharacterManageScene.getInstance());
            }
        };

    }

    public void changeToSceneEquipItem(ItemType itemType) {

    }

    public void unequipItemType(ItemType itemType) {

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
        Message message = Client.getInstance().receiveMessage();
        if (null != message) {
            switch (message.getType()) {
                case PLAYER_UPDATE:
                    LOG.debug("PLAYER_UPDATE");
                    MessagePlayerUpdate response = (MessagePlayerUpdate) message;
                    LOG.debug("CREATE OK");
                    Client.getInstance().setPlayer(response.getPlayer());
                    GameCharacter updatedGameCharacter = response.getPlayer().getGameCharWithId(gameCharacter.getId());
                    setGameCharacter(updatedGameCharacter);
                    break;
            }
        }
    }
}
