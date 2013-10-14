package com.ghostofpq.kulkan.entities.messages;

import com.ghostofpq.kulkan.entities.character.GameCharacter;

import java.io.Serializable;

public class MessagePositionToMoveRequest extends ClientMessage implements Serializable {
    private GameCharacter character;

    public MessagePositionToMoveRequest(String keyToken, GameCharacter character) {
        type = MessageType.CHARACTER_POSITION_TO_MOVE_REQUEST;
        this.keyToken = keyToken;
        this.character = character;
    }

    public GameCharacter getCharacter() {
        return character;
    }
}
