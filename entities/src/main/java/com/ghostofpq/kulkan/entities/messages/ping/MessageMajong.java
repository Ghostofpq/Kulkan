package com.ghostofpq.kulkan.entities.messages.ping;

import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;


public class MessageMajong extends Message {
    private long timestampClient;

    public MessageMajong(long timestampClient) {
        this.type = MessageType.MAJONG;
        this.timestampClient = timestampClient;
    }

    public long getTimestampClient() {
        return timestampClient;
    }
}
