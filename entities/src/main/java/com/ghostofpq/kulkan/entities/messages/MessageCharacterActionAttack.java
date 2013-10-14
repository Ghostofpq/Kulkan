package com.ghostofpq.kulkan.entities.messages;

import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.character.GameCharacter;

import java.io.Serializable;

public class MessageCharacterActionAttack extends ClientMessage implements Serializable {
    private GameCharacter character;
    private Position positionToMove;

    public MessageCharacterActionAttack(String keyToken, GameCharacter character, Position positionToMove) {
        type = MessageType.CHARACTER_ACTION_ATTACK;
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