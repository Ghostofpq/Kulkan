package com.ghostofpq.kulkan.entities.messages.game;

import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;

public class MessageCharacterActionCapacity extends ClientMessage implements Serializable {
    private GameCharacter character;
    private Position positionToUseCapacity;

    public MessageCharacterActionCapacity(String keyToken, GameCharacter character, Position positionToUseCapacity) {
        type = MessageType.CHARACTER_ACTION_CAPACITY_USE;
        this.keyToken = keyToken;
        this.character = character;
        this.positionToUseCapacity = positionToUseCapacity;
    }

    public GameCharacter getCharacter() {
        return character;
    }

    public Position getPositionToUseCapacity() {
        return positionToUseCapacity;
    }

    @Override
    public String toString() {
        return new StringBuffer()
                .append("Message Type :").append(type).append(System.getProperty("line.separator"))
                .append("KeyToken :").append(keyToken).append(System.getProperty("line.separator"))
                .append("PositionToUseCapacity :").append(positionToUseCapacity.toString())
                .toString();
    }
}