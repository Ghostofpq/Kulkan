package com.ghostofpq.kulkan.entities.messages.game;

import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;
import java.util.List;

public class MessageCharacterMoves extends Message implements Serializable {
    private GameCharacter character;
    private List<Position> path;

    public MessageCharacterMoves(GameCharacter character, List<Position> path) {
        type = MessageType.CHARACTER_MOVES;
        this.character = character;
        this.path = path;
    }

    public GameCharacter getCharacter() {
        return character;
    }

    public List<Position> getPath() {
        return path;
    }
}
