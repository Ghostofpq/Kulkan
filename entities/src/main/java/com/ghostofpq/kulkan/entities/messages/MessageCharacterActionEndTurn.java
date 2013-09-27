package com.ghostofpq.kulkan.entities.messages;

import java.io.Serializable;

public class MessageCharacterActionEndTurn extends Message implements Serializable {
    public MessageCharacterActionEndTurn() {
        type = MessageType.CHARACTER_ACTION_END_TURN;
    }
}
