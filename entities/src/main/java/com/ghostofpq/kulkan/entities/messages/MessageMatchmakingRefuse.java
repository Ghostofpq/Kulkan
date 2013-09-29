package com.ghostofpq.kulkan.entities.messages;


import java.io.Serializable;

public class MessageMatchmakingRefuse extends ClientMessage implements Serializable {
    private String matchKey;

    public MessageMatchmakingRefuse(String keyToken, String matchKey) {
        type = MessageType.MATCHMAKING_REFUSE;
        this.keyToken = keyToken;
        this.matchKey = matchKey;
    }

    public String getMatchKey() {
        return matchKey;
    }
}
