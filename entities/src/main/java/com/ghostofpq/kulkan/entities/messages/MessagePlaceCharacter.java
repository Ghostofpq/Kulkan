package com.ghostofpq.kulkan.entities.messages;

import java.io.Serializable;

public class MessagePlaceCharacter extends Message implements Serializable {
    public MessagePlaceCharacter() {
        type = MessageType.PLACE_CHARACTER;
    }
}
