package com.ghostofpq.kulkan.entities.messages.game;

import com.ghostofpq.kulkan.entities.character.Alteration;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;

public class MessageCharacterAlteration extends Message implements Serializable {
    private GameCharacter character;
    private Alteration alteration;
    private boolean status;

    public MessageCharacterAlteration(GameCharacter character, Alteration alteration, boolean status) {
        type = MessageType.CHARACTER_MOVES;
        this.character = character;
        this.alteration = alteration;
        this.status = status;
    }

    public GameCharacter getCharacter() {
        return character;
    }

    public Alteration getAlteration() {
        return alteration;
    }

    public boolean isStatus() {
        return status;
    }

    @Override
    public String toString() {
        return new StringBuffer()
                .append("Message Type :").append(type).append(System.getProperty("line.separator"))
                .append("Character :").append(character.getName()).append(System.getProperty("line.separator"))
                .append("Alteration :").append(alteration.toString()).append(System.getProperty("line.separator"))
                .append("Status :").append(status)
                .toString();
    }
}
