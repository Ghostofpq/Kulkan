package com.ghostofpq.kulkan.entities.messages.game;

import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;

public class MessageGameEnd extends Message implements Serializable {
    private boolean isWinner;

    public MessageGameEnd(String gameID, boolean isWinner) {
        type = MessageType.GAME_START;
        this.isWinner = isWinner;
    }

    public boolean isWinner() {
        return isWinner;
    }
}
