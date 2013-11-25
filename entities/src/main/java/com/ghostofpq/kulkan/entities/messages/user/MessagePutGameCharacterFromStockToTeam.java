package com.ghostofpq.kulkan.entities.messages.user;


import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

public class MessagePutGameCharacterFromStockToTeam extends ClientMessage {
    private String username;
    private String gameCharName;

    public MessagePutGameCharacterFromStockToTeam(String keyToken, String username, String gameCharName) {
        type = MessageType.PUT_GAME_CHARACTER_FROM_STOCK_TO_TEAM_REQUEST;
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
