package com.ghostofpq.kulkan.entities.messages;

import java.io.Serializable;

public class MessageLobbyPong extends ClientMessage implements Serializable {

    public MessageLobbyPong(String keyToken) {
        this.keyToken = keyToken;
        type = MessageType.LOBBY_PONG;
    }
}
