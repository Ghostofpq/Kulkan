package com.ghostofpq.kulkan.entities.messages;

import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.character.GameCharacter;

import java.io.Serializable;

public class MessageCharacterToPlay extends Message implements Serializable {
    private GameCharacter charactertoPlay;
    private Position positionOfChar;

    public MessageCharacterToPlay(GameCharacter charactertoPlay, Position positionOfChar) {
        type = MessageType.CHARACTER_TO_PLAY;
        this.charactertoPlay = charactertoPlay;
        this.positionOfChar = positionOfChar;
    }

    public GameCharacter getCharactertoPlay() {
        return charactertoPlay;
    }

    public Position getPositionOfChar() {
        return positionOfChar;
    }
}
