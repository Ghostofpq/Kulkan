package com.ghostofpq.kulkan.entities.messages;

import java.io.Serializable;

public class MessageGameEnd extends Message implements Serializable {
    public MessageGameEnd() {
        type = MessageType.GAME_END;
    }
}
