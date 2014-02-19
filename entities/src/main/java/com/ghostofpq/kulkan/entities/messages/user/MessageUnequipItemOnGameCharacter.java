package com.ghostofpq.kulkan.entities.messages.user;

import com.ghostofpq.kulkan.entities.inventory.item.ItemType;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;
import org.bson.types.ObjectId;

public class MessageUnequipItemOnGameCharacter extends ClientMessage {
    private ObjectId gameCharId;
    private ItemType itemType;

    public MessageUnequipItemOnGameCharacter(String keyToken, String username, ObjectId gameCharId, ItemType itemType) {
        this.type = MessageType.UNEQUIP_ITEM;
        this.keyToken = keyToken;
        this.username = username;
        this.gameCharId = gameCharId;
        this.itemType = itemType;
    }

    public ObjectId getGameCharId() {
        return gameCharId;
    }

    public ItemType getItemType() {
        return itemType;
    }
}
