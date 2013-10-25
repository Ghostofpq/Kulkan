package com.ghostofpq.kulkan.entities.messages.lobby;


import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;

public class MessageMatchAbort extends Message implements Serializable {
    public MessageMatchAbort() {
        type = MessageType.MATCHMAKING_MATCH_ABORT;
    }
}
