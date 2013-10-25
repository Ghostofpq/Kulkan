package com.ghostofpq.kulkan.entities.messages.lobby;


import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;

public class MessageLobbyServer extends Message implements Serializable {
    private String message;

    public MessageLobbyServer(String message) {
        this.type = MessageType.LOBBY_SERVER;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
