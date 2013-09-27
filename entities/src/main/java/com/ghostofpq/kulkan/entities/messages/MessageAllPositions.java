package com.ghostofpq.kulkan.entities.messages;

import java.io.Serializable;

public class MessageAllPositions extends Message implements Serializable {

    public MessageAllPositions() {
        type = MessageType.ALL_POSITIONS;
    }
}
