package com.ghostofpq.kulkan.entities.messages.user;

import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

public class MessagePlayerUpdate extends Message {
    private Player player;

    public MessagePlayerUpdate(Player player) {
        type = MessageType.PLAYER_UPDATE;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}

