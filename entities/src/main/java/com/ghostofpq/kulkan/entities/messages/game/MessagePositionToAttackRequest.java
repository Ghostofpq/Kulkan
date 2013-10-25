package com.ghostofpq.kulkan.entities.messages.game;

import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;

public class MessagePositionToAttackRequest extends ClientMessage implements Serializable {
    private GameCharacter character;

    public MessagePositionToAttackRequest(String keyToken, GameCharacter character) {
        type = MessageType.CHARACTER_POSITION_TO_ATTACK_REQUEST;
        this.keyToken = keyToken;
        this.character = character;
    }

    public GameCharacter getCharacter() {
        return character;
    }
}
