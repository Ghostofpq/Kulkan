package com.ghostofpq.kulkan.entities.messages.lobby;

import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

public class MessageLobbyPing extends Message {
    public MessageLobbyPing() {
        type = MessageType.LOBBY_PING;
    }
}
