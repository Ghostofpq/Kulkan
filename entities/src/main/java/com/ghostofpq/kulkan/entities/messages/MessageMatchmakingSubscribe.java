package com.ghostofpq.kulkan.entities.messages;


import java.io.Serializable;

public class MessageMatchmakingSubscribe extends ClientMessage implements Serializable {
    public MessageMatchmakingSubscribe(String keyToken) {
        type = MessageType.MATCHMAKING_SUBSCRIBE;
        this.keyToken = keyToken;
    }
}
