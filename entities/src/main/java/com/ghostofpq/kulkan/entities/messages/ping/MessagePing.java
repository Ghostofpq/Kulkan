package com.ghostofpq.kulkan.entities.messages.ping;

import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

public class MessagePing extends Message {
    private long timestampServer;

    public MessagePing() {
        this.type = MessageType.PING;
        timestampServer = System.currentTimeMillis();
    }

    public long getTimestampServer() {
        return timestampServer;
    }

    public void setTimestampServer(long timestampServer) {
        this.timestampServer = timestampServer;
    }
}
