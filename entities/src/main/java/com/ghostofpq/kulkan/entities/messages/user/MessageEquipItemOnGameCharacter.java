package com.ghostofpq.kulkan.entities.messages.user;

import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;
import org.bson.types.ObjectId;

public class MessageEquipItemOnGameCharacter extends ClientMessage {
    private ObjectId gameCharId;
    private String itemId;

    public MessageEquipItemOnGameCharacter(String keyToken, String username, ObjectId gameCharId, String itemId) {
        this.type = MessageType.EQUIP_ITEM;
        this.keyToken = keyToken;
        this.username = username;
        this.gameCharId = gameCharId;
        this.itemId = itemId;
    }

    public ObjectId getGameCharId() {
        return gameCharId;
    }

    public String getItemId() {
        return itemId;
    }
}
