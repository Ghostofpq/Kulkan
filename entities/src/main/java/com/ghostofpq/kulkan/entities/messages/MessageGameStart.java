package com.ghostofpq.kulkan.entities.messages;

import com.ghostofpq.kulkan.entities.battlefield.Battlefield;

public class MessageGameStart extends Message {
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
