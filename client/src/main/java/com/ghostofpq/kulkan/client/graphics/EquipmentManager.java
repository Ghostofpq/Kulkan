package com.ghostofpq.kulkan.client.graphics;

import com.ghostofpq.kulkan.client.graphics.HUD.Button;
import com.ghostofpq.kulkan.client.graphics.HUD.HUDElement;
import com.ghostofpq.kulkan.client.graphics.HUD.TextZone;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.inventory.item.ItemType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EquipmentManager extends HUDElement {
    private TextZone helm;
    private Button equipHelm;
    private TextZone armor;
    private Button equipArmor;
    private TextZone necklace;
    private Button equipNecklace;
    private TextZone ring;
    private Button equipRing;
    private TextZone weapon;
    private Button equipWeapon;
    private TextZone heldItem;
    private Button equipHeldItem;

    private ItemType selectedItemType;

    private GameCharacter gameCharacter;

    public EquipmentManager(int posX, int posY, int width, int height, GameCharacter gameCharacter) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.gameCharacter = gameCharacter;
        init();
    }

    public void init() {
        int widthStep = width / 3;
        int heightStep = height / 6;

        helm = new TextZone(posX, posY, widthStep, heightStep, "Helm");
        armor = new TextZone(posX, posY + heightStep, widthStep, heightStep, "Armor");
        necklace = new TextZone(posX, posY + heightStep * 2, widthStep, heightStep, "Necklace");
        ring = new TextZone(posX, posY + heightStep * 3, widthStep, heightStep, "Ring");
        weapon = new TextZone(posX, posY + heightStep * 4, widthStep, heightStep, "Weapon");
        heldItem = new TextZone(posX, posY + heightStep * 5, widthStep, heightStep, "HeldItem");

        //HELMET
        equipHelm = new Button(posX + widthStep, posY, widthStep * 2, heightStep, "NONE") {
            @Override
            public void onClick() {
                setSelectedItemType(ItemType.HELMET);
            }
        };
        //ARMOR
        equipArmor = new Button(posX + widthStep, posY + heightStep, widthStep * 2, heightStep, "NONE") {
            @Override
            public void onClick() {
                setSelectedItemType(ItemType.ARMOR);
            }
        };
        //NECKLACE
        equipNecklace = new Button(posX + widthStep, posY + heightStep * 2, widthStep * 2, heightStep, "NONE") {
            @Override
            public void onClick() {
                setSelectedItemType(ItemType.NECKLACE);
            }
        };
        //RING
        equipRing = new Button(posX + widthStep, posY + heightStep * 3, widthStep * 2, heightStep, "NONE") {
            @Override
            public void onClick() {
                setSelectedItemType(ItemType.RING);
            }
        };
        //WEAPON
        equipWeapon = new Button(posX + widthStep, posY + heightStep * 4, widthStep * 2, heightStep, "NONE") {
            @Override
            public void onClick() {
                setSelectedItemType(ItemType.WEAPON);
            }
        };
        //HELD ITEM
        equipHeldItem = new Button(posX + widthStep, posY + heightStep * 5, widthStep * 2, heightStep, "NONE") {
            @Override
            public void onClick() {
                setSelectedItemType(ItemType.HELD_ITEM);
            }
        };

        if (null != gameCharacter.getEquipment().getHelm()) {
            equipHelm.setLabel(gameCharacter.getEquipment().getHelm().getName());
        }
        if (null != gameCharacter.getEquipment().getArmor()) {
            equipArmor.setLabel(gameCharacter.getEquipment().getArmor().getName());
        }
        if (null != gameCharacter.getEquipment().getNecklace()) {
            equipNecklace.setLabel(gameCharacter.getEquipment().getNecklace().getName());
        }
        if (null != gameCharacter.getEquipment().getRing()) {
            equipRing.setLabel(gameCharacter.getEquipment().getRing().getName());
        }
        if (null != gameCharacter.getEquipment().getWeapon()) {
            equipWeapon.setLabel(gameCharacter.getEquipment().getWeapon().getName());
        }
        if (null != gameCharacter.getEquipment().getHeldItem()) {
            equipHeldItem.setLabel(gameCharacter.getEquipment().getHeldItem().getName());
        }
        this.selectedItemType = null;
    }

    public ItemType getClickedItemType() {
        if (equipHelm.isHovered()) {
            selectedItemType = ItemType.HELMET;
        } else if (equipArmor.isHovered()) {
            selectedItemType = ItemType.ARMOR;
        } else if (equipNecklace.isHovered()) {
            selectedItemType = ItemType.NECKLACE;
        } else if (equipRing.isHovered()) {
            selectedItemType = ItemType.RING;
        } else if (equipWeapon.isHovered()) {
            selectedItemType = ItemType.WEAPON;
        } else if (equipHeldItem.isHovered()) {
            selectedItemType = ItemType.HELD_ITEM;
        } else {
            selectedItemType = null;
        }
        return selectedItemType;
    }


    @Override
    public void draw() {
        helm.draw();
        armor.draw();
        necklace.draw();
        ring.draw();
        weapon.draw();
        heldItem.draw();
        equipHelm.draw();
        equipArmor.draw();
        equipNecklace.draw();
        equipRing.draw();
        equipWeapon.draw();
        equipHeldItem.draw();
    }

    public void setSelectedItemType(ItemType selectedItemType) {
        this.selectedItemType = selectedItemType;
    }

    public void update(GameCharacter gameCharacter) {
        this.gameCharacter = gameCharacter;
        init();
    }
}
