package com.ghostofpq.kulkan.entities.messages;

import java.io.Serializable;

public class MessageCharacterActionMove extends Message implements Serializable {

    public MessageCharacterActionMove() {
        type = MessageType.CHARACTER_ACTION_MOVE;
    }
}
