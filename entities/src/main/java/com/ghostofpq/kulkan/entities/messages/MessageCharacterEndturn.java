package com.ghostofpq.kulkan.entities.messages;

import com.ghostofpq.kulkan.entities.character.GameCharacter;

import java.io.Serializable;

public class MessageCharacterEndTurn extends ClientMessage implements Serializable {
    private GameCharacter character;

    public MessageCharacterEndTurn(String keyToken, GameCharacter character) {
        type = MessageType.CHARACTER_ACTION_END_TURN;
        this.keyToken = keyToken;
        this.character = character;
    }

    public GameCharacter getCharacter() {
        return character;
    }
}
