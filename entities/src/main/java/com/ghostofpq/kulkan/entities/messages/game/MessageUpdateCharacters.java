package com.ghostofpq.kulkan.entities.messages.game;


import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;
import java.util.Map;

public class MessageUpdateCharacters extends Message implements Serializable {
    private Map<GameCharacter, Position> characterPositionMap;

    public MessageUpdateCharacters(Map<GameCharacter, Position> characterPositionMap) {
        type = MessageType.ALL_CHARACTERS;
        this.characterPositionMap = characterPositionMap;
    }

    public Map<GameCharacter, Position> getCharacterPositionMap() {
        return characterPositionMap;
    }

}
