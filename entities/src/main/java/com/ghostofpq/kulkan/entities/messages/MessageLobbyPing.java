package com.ghostofpq.kulkan.entities.messages;

public class MessageLobbyPing extends Message {
    public MessageLobbyPing() {
        type = MessageType.LOBBY_PING;
    }
}
