package com.ghostofpq.kulkan.entities.messages;


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
