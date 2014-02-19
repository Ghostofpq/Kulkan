package com.ghostofpq.kulkan.entities.messages.user;


import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;
import org.bson.types.ObjectId;

public class MessageDeleteGameCharacterFromStock extends ClientMessage {
    private ObjectId gameCharId;

    public MessageDeleteGameCharacterFromStock(String keyToken, String username, ObjectId gameCharId) {
        type = MessageType.DELETE_GAME_CHARACTER_FROM_STOCK_REQUEST;
        this.keyToken = keyToken;
        this.username = username;
        this.gameCharId = gameCharId;
    }

    public ObjectId getGameCharId() {
        return gameCharId;
    }
}
