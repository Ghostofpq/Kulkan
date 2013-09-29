package com.ghostofpq.kulkan.entities.messages;


import java.io.Serializable;

public class MessageMatchFound extends Message implements Serializable {

    private String matchKey;

    public MessageMatchFound(String matchKey) {
        type = MessageType.MATCHMAKING_MATCH_FOUND;
        this.matchKey = matchKey;
    }

    public String getMatchKey() {
        return matchKey;
    }
}
