package com.ghostofpq.kulkan.entities.messages.game;


import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.job.capacity.Move;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;

public class MessageCharacterPositionToUseCapacityRequest extends ClientMessage implements Serializable {
    private GameCharacter character;
    private Move selectedMove;

    public MessageCharacterPositionToUseCapacityRequest(String keyToken, GameCharacter character, Move selectedMove) {
        type = MessageType.CHARACTER_POSITION_TO_USE_CAPACITY_REQUEST;
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

    @Override
    public String toString() {
        return new StringBuffer()
                .append("Message Type :").append(type).append(System.getProperty("line.separator"))
                .append("KeyToken :").append(keyToken).append(System.getProperty("line.separator"))
                .append("Character :").append(character.getName()).append(System.getProperty("line.separator"))
                .append("SelectedMove :").append(selectedMove.getName())
                .toString();
    }
}
