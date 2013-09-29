package com.ghostofpq.kulkan.entities.messages;


import java.io.Serializable;

public class MessageMatchmakingUnsubscribe extends ClientMessage implements Serializable {
    public MessageMatchmakingUnsubscribe(String keyToken) {
        type = MessageType.MATCHMAKING_UNSUBSCRIBE;
        this.keyToken = keyToken;
    }
}
