package com.ghostofpq.kulkan.entities.messages.user;


import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;
import org.bson.types.ObjectId;

public class MessageDeleteGameCharacterFromTeam extends ClientMessage {
    private String username;
    private ObjectId gameCharId;

    public MessageDeleteGameCharacterFromTeam(String keyToken, String username, ObjectId gameCharId) {
        type = MessageType.DELETE_GAME_CHARACTER_FROM_TEAM_REQUEST;
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
