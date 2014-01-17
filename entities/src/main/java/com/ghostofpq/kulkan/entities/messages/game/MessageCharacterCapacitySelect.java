package com.ghostofpq.kulkan.entities.messages.game;


import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.job.capacity.Move;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;

public class MessageCharacterCapacitySelect extends ClientMessage implements Serializable {
    private GameCharacter character;
    private Move selectedMove;

    public MessageCharacterCapacitySelect(String keyToken, GameCharacter character, Move selectedMove) {
        type = MessageType.CHARACTER_ACTION_CAPACITY_SELECT;
        this.keyToken = keyToken;
        this.character = character;
        this.selectedMove = selectedMove;
    }

    public GameCharacter getCharacter() {
        return character;
    }

    public Move getSelectedMove() {
        return selectedMove;
    }
}
