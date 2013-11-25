package com.ghostofpq.kulkan.entities.messages.lobby;

import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

public class MessageUnsubscribeToLobby extends ClientMessage {
    public MessageUnsubscribeToLobby(String keyToken) {
        this.type = MessageType.LOBBY_UNSUBCRIBE;
        this.keyToken = keyToken;
    }
}
