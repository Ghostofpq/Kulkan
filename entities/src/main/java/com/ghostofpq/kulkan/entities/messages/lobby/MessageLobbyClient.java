package com.ghostofpq.kulkan.entities.messages.lobby;


import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;

public class MessageLobbyClient extends ClientMessage implements Serializable {

    private String lobbyMessage;

    public MessageLobbyClient(String keyToken, String lobbyMessage) {
        this.type = MessageType.LOBBY_CLIENT;
        this.keyToken = keyToken;
        this.lobbyMessage = lobbyMessage;
    }

    public String getLobbyMessage() {
        return lobbyMessage;
    }
}
