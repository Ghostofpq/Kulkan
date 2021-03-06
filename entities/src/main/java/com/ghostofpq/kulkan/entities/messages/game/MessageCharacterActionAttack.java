package com.ghostofpq.kulkan.entities.messages.game;

import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

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

    @Override
    public String toString() {
        return new StringBuffer().append(System.getProperty("line.separator"))
                .append("Message Type : ").append(type).append(System.getProperty("line.separator"))
                .append("KeyToken : ").append(keyToken).append(System.getProperty("line.separator"))
                .append("PositionToAttack : ").append(positionToAttack.toString())
                .toString();
    }
}