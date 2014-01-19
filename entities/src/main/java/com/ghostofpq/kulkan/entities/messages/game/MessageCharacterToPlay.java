package com.ghostofpq.kulkan.entities.messages.game;

import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;

public class MessageCharacterToPlay extends Message implements Serializable {
    private GameCharacter characterToPlay;
    private Position positionOfCursor;

    public MessageCharacterToPlay(GameCharacter characterToPlay, Position positionOfCursor) {
        type = MessageType.CHARACTER_TO_PLAY;
        this.characterToPlay = characterToPlay;
        this.positionOfCursor = positionOfCursor;
    }

    public GameCharacter getCharacterToPlay() {
        return characterToPlay;
    }

    public Position getPositionOfCursor() {
        return positionOfCursor;
    }


    @Override
    public String toString() {
        return new StringBuffer().
                append("Message Type :").append(type).append(System.getProperty("line.separator"))
                .append("Character :").append(characterToPlay.getName()).append(System.getProperty("line.separator"))
                .append("PositionOfCursor :").append(positionOfCursor.toString())
                .toString();
    }
}
