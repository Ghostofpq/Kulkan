package com.ghostofpq.kulkan.entities.messages.auth;


import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

public class MessagePutGameCharacterFromTeamToStock extends ClientMessage {
    private String username;
    private String gameCharName;

    public MessagePutGameCharacterFromTeamToStock(String keyToken, String username, String gameCharName) {
        type = MessageType.PUT_GAME_CHARACTER_FROM_TEAM_TO_STOCK_REQUEST;
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
