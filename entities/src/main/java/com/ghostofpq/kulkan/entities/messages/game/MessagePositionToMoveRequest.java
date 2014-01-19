package com.ghostofpq.kulkan.entities.messages.game;

import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

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

    @Override
    public String toString() {
        return new StringBuffer()
                .append("Message Type :").append(type).append(System.getProperty("line.separator"))
                .append("Character :").append(character.getName())
                .toString();
    }
}
