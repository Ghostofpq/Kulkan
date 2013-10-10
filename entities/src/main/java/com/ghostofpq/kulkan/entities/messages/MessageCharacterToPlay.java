package com.ghostofpq.kulkan.entities.messages;

import com.ghostofpq.kulkan.entities.character.GameCharacter;

import java.io.Serializable;

public class MessageCharacterToPlay extends Message implements Serializable {
    private GameCharacter charactertoPlay;

    public MessageCharacterToPlay(GameCharacter charactertoPlay) {
        type = MessageType.CHARACTER_TO_PLAY;
        this.charactertoPlay = charactertoPlay;
    }

    public GameCharacter getCharactertoPlay() {
        return charactertoPlay;
    }
}
