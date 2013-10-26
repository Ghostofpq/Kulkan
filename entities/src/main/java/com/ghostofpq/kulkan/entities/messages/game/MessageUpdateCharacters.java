package com.ghostofpq.kulkan.entities.messages.game;


import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;
import java.util.List;

public class MessageUpdateCharacters extends Message implements Serializable {
    private List<GameCharacter> characterList;

    public MessageUpdateCharacters(List<GameCharacter> characterList) {
        type = MessageType.ALL_CHARACTERS;
        this.characterList = characterList;
    }

    public List<GameCharacter> getCharacterList() {
        return characterList;
    }
}
