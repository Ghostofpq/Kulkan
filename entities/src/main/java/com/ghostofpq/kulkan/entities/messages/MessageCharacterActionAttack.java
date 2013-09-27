package com.ghostofpq.kulkan.entities.messages;


import java.io.Serializable;

public class MessageCharacterActionAttack extends Message implements Serializable {

    public MessageCharacterActionAttack() {
        type = MessageType.CHARACTER_ACTION_ATTACK;
    }
}
