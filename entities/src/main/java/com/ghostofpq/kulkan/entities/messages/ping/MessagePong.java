package com.ghostofpq.kulkan.entities.messages.ping;

import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

public class MessagePong extends ClientMessage {

    private long timestampServer;
    private long timestampClient;

    public MessagePong(long timestampServer, String keyToken) {
        this.type = MessageType.PONG;
        this.keyToken = keyToken;
        this.timestampServer = timestampServer;
        timestampClient = System.currentTimeMillis();
    }

    public long getTimestampServer() {
        return timestampServer;
    }

    public void setTimestampServer(long timestampServer) {
        this.timestampServer = timestampServer;
    }

    public long getTimestampClient() {
        return timestampClient;
    }

    public void setTimestampClient(long timestampClient) {
        this.timestampClient = timestampClient;
    }
}
