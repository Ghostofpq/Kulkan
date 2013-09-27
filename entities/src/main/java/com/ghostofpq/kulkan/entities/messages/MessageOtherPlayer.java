package com.ghostofpq.kulkan.entities.messages;

import com.ghostofpq.kulkan.entities.character.Player;

import java.io.Serializable;

public class MessageOtherPlayer extends Message implements Serializable {
    private Player otherPlayer;

    public MessageOtherPlayer() {
        type = MessageType.OTHER_PLAYER;
    }

    public Player getOtherPlayer() {
        return otherPlayer;
    }

    public void setOtherPlayer(Player otherPlayer) {
        this.otherPlayer = otherPlayer;
    }
}
