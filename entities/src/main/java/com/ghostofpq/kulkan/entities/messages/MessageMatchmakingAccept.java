package com.ghostofpq.kulkan.entities.messages;


import java.io.Serializable;

public class MessageMatchmakingAccept extends ClientMessage implements Serializable {
    private String matchKey;

    public MessageMatchmakingAccept(String keyToken, String matchKey) {
        type = MessageType.MATCHMAKING_ACCEPT;
        this.keyToken = keyToken;
        this.matchKey = matchKey;
    }

    public String getMatchKey() {
        return matchKey;
    }
}
