package com.ghostofpq.kulkan.entities.messages.game;

import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;

public class MessageCharacterGainsHP extends Message implements Serializable {
    private GameCharacter character;
    private int healthPoint;

    public MessageCharacterGainsHP(GameCharacter character, int healthPoint) {
        type = MessageType.CHARACTER_GAINS_HP;
        this.character = character;
        this.healthPoint = healthPoint;
    }

    public GameCharacter getCharacter() {
        return character;
    }

    public int getHealthPoint() {
        return healthPoint;
    }

    @Override
    public String toString() {
        return new StringBuffer()
                .append("Message Type :").append(type).append(System.getProperty("line.separator"))
                .append("Character :").append(character.getName()).append(System.getProperty("line.separator"))
                .append("Health Point :").append(healthPoint).append(System.getProperty("line.separator"))
                .toString();
    }
}
