package com.ghostofpq.kulkan.entities.messages.lobby;

import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

public class MessageSubscribeToLobby extends ClientMessage {
    public MessageSubscribeToLobby(String keyToken) {
        this.type = MessageType.LOBBY_SUBSCRIBE;
        this.keyToken = keyToken;
    }
}
