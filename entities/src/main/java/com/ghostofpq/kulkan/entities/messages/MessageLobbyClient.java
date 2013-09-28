package com.ghostofpq.kulkan.entities.messages;


import java.io.Serializable;

public class MessageLobbyClient extends ClientMessage implements Serializable {

    private String pseudo;
    private String lobbyMessage;

    public MessageLobbyClient(String pseudo, String keyToken, String lobbyMessage) {
        this.type = MessageType.LOBBY_CLIENT;
        this.pseudo = pseudo;
        this.keyToken = keyToken;
        this.lobbyMessage = lobbyMessage;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getLobbyMessage() {
        return lobbyMessage;
    }
}
