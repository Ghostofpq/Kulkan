package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.ClientContext;
import com.ghostofpq.kulkan.client.ClientMessenger;
import com.ghostofpq.kulkan.client.graphics.HUD.Button;
import com.ghostofpq.kulkan.client.graphics.KeyValueRender;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.inventory.item.ItemType;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.user.MessagePlayerUpdate;
import com.ghostofpq.kulkan.entities.messages.user.MessageUnequipItemOnGameCharacter;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.input.Mouse;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ManageGameCharacterEquipmentScene implements Scene {
    private GameCharacter gameCharacter;
    private KeyValueRender nameRender;
    private KeyValueRender helm;
    private Button equipHelm;
    private Button unequipHelm;
    private KeyValueRender armor;
    private Button equipArmor;
    private Button unequipArmor;
    private KeyValueRender necklace;
    private Button equipNecklace;
    private Button unequipNecklace;
    private KeyValueRender ring;
    private Button equipRing;
    private Button unequipRing;
    private KeyValueRender weapon;
    private Button equipWeapon;
    private Button unequipWeapon;
    private KeyValueRender heldItem;
    private Button equipHeldItem;
    private Button unequipHeldItem;
    private Button quitButton;

    @Autowired
    private ClientContext clientContext;
    @Autowired
    private Client client;
    @Autowired
    private ManageGameCharacterScene manageGameCharacterScene;
    @Autowired
    private ClientMessenger clientMessenger;
    @Autowired
    private ManageGameCharacterEquipItemScene manageGameCharacterEquipItemScene;

    public ManageGameCharacterEquipmentScene() {

    }

    public void setGameCharacter(GameCharacter gameCharacter) {
        this.gameCharacter = gameCharacter;
    }

    @Override
    public void init() {
        int widthSeparator = client.getWidth() / 20;
        int heightSeparator = client.getHeight() / 20;

        int widthStep = (client.getWidth() - 4 * widthSeparator) / 5;
        int heightStep = (client.getHeight() - 9 * heightSeparator) / 8;

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
        //HELMET
        equipHelm = new Button(widthSeparator * 2 + widthStep * 3, heightSeparator * 2 + heightStep, widthStep, heightStep, "Equip") {
            @Override
            public void onClick() {
                changeToSceneEquipItem(ItemType.HELMET);
            }
        };
        unequipHelm = new Button(widthSeparator * 3 + widthStep * 4, heightSeparator * 2 + heightStep, widthStep, heightStep, "Unequip") {
            @Override
            public void onClick() {
                unequipItemType(ItemType.HELMET);
            }
        };
        //ARMOR
        equipArmor = new Button(widthSeparator * 2 + widthStep * 3, heightSeparator * 3 + heightStep * 2, widthStep, heightStep, "Equip") {
            @Override
            public void onClick() {
                changeToSceneEquipItem(ItemType.ARMOR);
            }
        };
        unequipArmor = new Button(widthSeparator * 3 + widthStep * 4, heightSeparator * 3 + heightStep * 2, widthStep, heightStep, "Unequip") {
            @Override
            public void onClick() {
                unequipItemType(ItemType.ARMOR);
            }
        };
        //NECKLACE
        equipNecklace = new Button(widthSeparator * 2 + widthStep * 3, heightSeparator * 4 + heightStep * 3, widthStep, heightStep, "Equip") {
            @Override
            public void onClick() {
                changeToSceneEquipItem(ItemType.NECKLACE);
            }
        };
        unequipNecklace = new Button(widthSeparator * 3 + widthStep * 4, heightSeparator * 4 + heightStep * 3, widthStep, heightStep, "Unequip") {
            @Override
            public void onClick() {
                unequipItemType(ItemType.NECKLACE);
            }
        };
        //RING
        equipRing = new Button(widthSeparator * 2 + widthStep * 3, heightSeparator * 5 + heightStep * 4, widthStep, heightStep, "Equip") {
            @Override
            public void onClick() {
                changeToSceneEquipItem(ItemType.RING);
            }
        };
        unequipRing = new Button(widthSeparator * 3 + widthStep * 4, heightSeparator * 5 + heightStep * 4, widthStep, heightStep, "Unequip") {
            @Override
            public void onClick() {
                unequipItemType(ItemType.RING);
            }
        };
        //WEAPON
        equipWeapon = new Button(widthSeparator * 2 + widthStep * 3, heightSeparator * 6 + heightStep * 5, widthStep, heightStep, "Equip") {
            @Override
            public void onClick() {
                changeToSceneEquipItem(ItemType.WEAPON);
            }
        };
        unequipWeapon = new Button(widthSeparator * 3 + widthStep * 4, heightSeparator * 6 + heightStep * 5, widthStep, heightStep, "Unequip") {
            @Override
            public void onClick() {
                unequipItemType(ItemType.WEAPON);
            }
        };
        //HELD ITEM
        equipHeldItem = new Button(widthSeparator * 2 + widthStep * 3, heightSeparator * 7 + heightStep * 6, widthStep, heightStep, "Equip") {
            @Override
            public void onClick() {
                changeToSceneEquipItem(ItemType.HELD_ITEM);
            }
        };
        unequipHeldItem = new Button(widthSeparator * 3 + widthStep * 4, heightSeparator * 7 + heightStep * 6, widthStep, heightStep, "Unequip") {
            @Override
            public void onClick() {
                unequipItemType(ItemType.HELD_ITEM);
            }
        };

        quitButton = new Button(widthSeparator * 3 + widthStep * 4, heightSeparator * 8 + heightStep * 7, widthStep, heightStep, "Back") {
            @Override
            public void onClick() {
                client.setCurrentScene(manageGameCharacterScene);
            }
        };

    }

    public void changeToSceneEquipItem(ItemType itemType) {
        manageGameCharacterEquipItemScene.setFilter(itemType);
        manageGameCharacterEquipItemScene.setGameCharId(gameCharacter.getId());
        client.setCurrentScene(manageGameCharacterEquipItemScene);
    }

    public void unequipItemType(ItemType itemType) {
        MessageUnequipItemOnGameCharacter messageUnequipItemOnGameCharacter = new MessageUnequipItemOnGameCharacter(client.getTokenKey(), gameCharacter.getId(), itemType);
        clientMessenger.sendMessageToUserService(messageUnequipItemOnGameCharacter);
    }

    @Override
    public void update(long deltaTime) {
    }

    @Override
    public void render() {
        nameRender.draw();
        helm.draw();
        armor.draw();
        necklace.draw();
        ring.draw();
        weapon.draw();
        heldItem.draw();
        quitButton.draw();

        if (null != gameCharacter.getEquipment().getHelm()) {
            unequipHelm.draw();
        } else {
            equipHelm.draw();
        }
        if (null != gameCharacter.getEquipment().getArmor()) {
            unequipArmor.draw();
        } else {
            equipArmor.draw();
        }
        if (null != gameCharacter.getEquipment().getNecklace()) {
            unequipNecklace.draw();
        } else {
            equipNecklace.draw();
        }
        if (null != gameCharacter.getEquipment().getRing()) {
            unequipRing.draw();
        } else {
            equipRing.draw();
        }
        if (null != gameCharacter.getEquipment().getWeapon()) {
            unequipWeapon.draw();
        } else {
            equipWeapon.draw();
        }
        if (null != gameCharacter.getEquipment().getHeldItem()) {
            unequipHeldItem.draw();
        } else {
            equipHeldItem.draw();
        }
        quitButton.draw();
    }

    @Override
    public void manageInput() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                if (quitButton.isClicked()) {
                    quitButton.onClick();
                } else if (unequipHelm.isClicked()) {
                    if (null != gameCharacter.getEquipment().getHelm()) {
                        unequipHelm.onClick();
                    }
                } else if (unequipArmor.isClicked()) {
                    if (null != gameCharacter.getEquipment().getArmor()) {
                        unequipArmor.onClick();
                    }
                } else if (unequipNecklace.isClicked()) {
                    if (null != gameCharacter.getEquipment().getNecklace()) {
                        unequipNecklace.onClick();
                    }
                } else if (unequipRing.isClicked()) {
                    if (null != gameCharacter.getEquipment().getRing()) {
                        unequipRing.onClick();
                    }
                } else if (unequipWeapon.isClicked()) {
                    if (null != gameCharacter.getEquipment().getWeapon()) {
                        unequipWeapon.onClick();
                    }
                } else if (unequipHeldItem.isClicked()) {
                    if (null != gameCharacter.getEquipment().getHeldItem()) {
                        unequipHeldItem.onClick();
                    }
                } else if (equipHelm.isClicked()) {
                    if (null == gameCharacter.getEquipment().getHelm()) {
                        equipHelm.onClick();
                    }
                } else if (equipArmor.isClicked()) {
                    if (null == gameCharacter.getEquipment().getArmor()) {
                        equipArmor.onClick();
                    }
                } else if (equipNecklace.isClicked()) {
                    if (null == gameCharacter.getEquipment().getNecklace()) {
                        equipNecklace.onClick();
                    }
                } else if (equipRing.isClicked()) {
                    if (null == gameCharacter.getEquipment().getRing()) {
                        equipRing.onClick();
                    }
                } else if (equipWeapon.isClicked()) {
                    if (null == gameCharacter.getEquipment().getWeapon()) {
                        equipWeapon.onClick();
                    }
                } else if (equipHeldItem.isClicked()) {
                    if (null == gameCharacter.getEquipment().getHeldItem()) {
                        equipHeldItem.onClick();
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
                    ManageGameCharacterEquipmentScene.log.debug("PLAYER_UPDATE");
                    MessagePlayerUpdate response = (MessagePlayerUpdate) message;
                    ManageGameCharacterEquipmentScene.log.debug("CREATE OK");
                    client.setPlayer(response.getPlayer());
                    GameCharacter updatedGameCharacter = response.getPlayer().getGameCharWithId(gameCharacter.getId());
                    clientContext.setSelectedGameCharacter(updatedGameCharacter);
                    init();
                    break;
            }
        }
    }
}