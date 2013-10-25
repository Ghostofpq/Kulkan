package com.ghostofpq.kulkan.entities.messages.lobby;

import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;

public class MessageLobbyPong extends ClientMessage implements Serializable {

    public MessageLobbyPong(String keyToken) {
        this.keyToken = keyToken;
        type = MessageType.LOBBY_PONG;
    }
}
