package com.ghostofpq.kulkan.entities.messages.game;


import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.job.capacity.Move;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;

public class MessageCharacterCapacityUse extends ClientMessage implements Serializable {
    private GameCharacter character;
    private Move selectedMove;
    private Position selectedPosition;

    public MessageCharacterCapacityUse(String keyToken, GameCharacter character, Move selectedMove, Position selectedPosition) {
        type = MessageType.CHARACTER_ACTION_CAPACITY_USE;
        this.keyToken = keyToken;
        this.character = character;
        this.selectedMove = selectedMove;
        this.selectedPosition = selectedPosition;
    }

    public GameCharacter getCharacter() {
        return character;
    }

    public Move getSelectedMove() {
        return selectedMove;
    }

    public Position getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public String toString() {
        return new StringBuffer()
                .append("Message Type :").append(type).append(System.getProperty("line.separator"))
                .append("KeyToken :").append(keyToken).append(System.getProperty("line.separator"))
                .append("Character :").append(character.getName()).append(System.getProperty("line.separator"))
                .append("SelectedMove :").append(selectedMove.getName()).append(System.getProperty("line.separator"))
                .append("SelectedPosition :").append(selectedPosition.toString())
                .toString();
    }
}
