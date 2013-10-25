package com.ghostofpq.kulkan.entities.messages.game;

import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;

public class MessageCharacterActionMove extends ClientMessage implements Serializable {
    private GameCharacter character;
    private Position positionToMove;

    public MessageCharacterActionMove(String keyToken, GameCharacter character, Position positionToMove) {
        type = MessageType.CHARACTER_ACTION_MOVE;
        this.keyToken = keyToken;
        this.character = character;
        this.positionToMove = positionToMove;
    }

    public GameCharacter getCharacter() {
        return character;
    }

    public Position getPositionToMove() {
        return positionToMove;
    }
}
