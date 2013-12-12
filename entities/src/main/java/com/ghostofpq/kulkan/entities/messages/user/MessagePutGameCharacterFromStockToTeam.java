package com.ghostofpq.kulkan.entities.messages.user;


import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;
import org.bson.types.ObjectId;

public class MessagePutGameCharacterFromStockToTeam extends ClientMessage {
    private String username;
    private ObjectId gameCharId;

    public MessagePutGameCharacterFromStockToTeam(String keyToken, String username, ObjectId gameCharId) {
        type = MessageType.PUT_GAME_CHARACTER_FROM_STOCK_TO_TEAM_REQUEST;
        this.keyToken = keyToken;
        this.username = username;
        this.gameCharId = gameCharId;
    }

    public String getUsername() {
        return username;
    }

    public ObjectId getGameCharId() {
        return gameCharId;
    }
}
