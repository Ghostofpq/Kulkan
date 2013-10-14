package com.ghostofpq.kulkan.entities.messages;

import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.character.GameCharacter;

import java.io.Serializable;

public class MessageCharacterActionAttack extends ClientMessage implements Serializable {
    private GameCharacter character;
    private Position positionToAttack;

    public MessageCharacterActionAttack(String keyToken, GameCharacter character, Position positionToAttack) {
        type = MessageType.CHARACTER_ACTION_ATTACK;
        this.keyToken = keyToken;
        this.character = character;
        this.positionToAttack = positionToAttack;
    }

    public GameCharacter getCharacter() {
        return character;
    }

    public Position getPositionToAttack() {
        return positionToAttack;
    }
}