package com.ghostofpq.kulkan.entities.messages;

import com.ghostofpq.kulkan.entities.battlefield.Battlefield;

import java.io.Serializable;

public class MessageGameStart extends Message implements Serializable {
    private Battlefield battlefield;

    public MessageGameStart() {
        type = MessageType.GAME_START;
    }

    public Battlefield getBattlefield() {
        return battlefield;
    }

    public void setBattlefield(Battlefield battlefield) {
        this.battlefield = battlefield;
    }
}
