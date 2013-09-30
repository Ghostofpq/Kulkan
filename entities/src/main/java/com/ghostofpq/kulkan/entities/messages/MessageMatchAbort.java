package com.ghostofpq.kulkan.entities.messages;


import java.io.Serializable;

public class MessageMatchAbort extends Message implements Serializable {
    public MessageMatchAbort() {
        type = MessageType.MATCHMAKING_MATCH_ABORT;
    }
}
