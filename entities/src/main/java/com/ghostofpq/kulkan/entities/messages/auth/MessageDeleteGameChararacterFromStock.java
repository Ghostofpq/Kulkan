package com.ghostofpq.kulkan.entities.messages.auth;


import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

public class MessageDeleteGameChararacterFromStock extends ClientMessage {
    private String username;
    private String gameCharName;

    public MessageDeleteGameChararacterFromStock(String keyToken, String username, String gameCharName) {
        type = MessageType.DELETE_GAME_CHARACTER_FROM_STOCK_REQUEST;
        this.keyToken = keyToken;
        this.username = username;
        this.gameCharName = gameCharName;
    }

    public String getUsername() {
        return username;
    }

    public String getGameCharName() {
        return gameCharName;
    }
}
