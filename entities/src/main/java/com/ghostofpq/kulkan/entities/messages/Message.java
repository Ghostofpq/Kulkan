package com.ghostofpq.kulkan.entities.messages;

public abstract class Message {
    protected MessageType type;

    public MessageType getType() {
        return type;
    }
}
