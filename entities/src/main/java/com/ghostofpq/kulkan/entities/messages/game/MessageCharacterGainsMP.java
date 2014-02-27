package com.ghostofpq.kulkan.entities.messages.game;

import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;

public class MessageCharacterGainsMP extends Message implements Serializable {
    private GameCharacter character;
    private int manaPoint;

    public MessageCharacterGainsMP(GameCharacter character, int manaPoint) {
        type = MessageType.CHARACTER_GAINS_MP;
        this.character = character;
        this.manaPoint = manaPoint;
    }

    public GameCharacter getCharacter() {
        return character;
    }

    public int getManaPoint() {
        return manaPoint;
    }

    @Override
    public String toString() {
        return new StringBuffer()
                .append("Message Type :").append(type).append(System.getProperty("line.separator"))
                .append("Character :").append(character.getName()).append(System.getProperty("line.separator"))
                .append("Mana Point :").append(manaPoint).append(System.getProperty("line.separator"))
                .toString();
    }
}
