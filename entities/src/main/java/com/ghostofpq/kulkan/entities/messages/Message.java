package com.ghostofpq.kulkan.entities.messages;

import org.apache.commons.lang.SerializationUtils;

import java.io.Serializable;

public abstract class Message implements Serializable {

    protected MessageType type;

    public static Message loadFromBytes(byte[] bytes) {
        return (Message) SerializationUtils.deserialize(bytes);
    }

    public MessageType getType() {
        return type;
    }

    public byte[] getBytes() {
        return SerializationUtils.serialize(this);
    }

    public String toString() {
        return new StringBuffer().append("Message Type :").append(type).toString();
    }

    ;
}
