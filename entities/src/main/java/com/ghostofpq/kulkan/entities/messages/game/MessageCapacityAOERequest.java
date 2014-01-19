package com.ghostofpq.kulkan.entities.messages.game;


import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.job.capacity.Move;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;

public class MessageCapacityAOERequest extends ClientMessage implements Serializable {
    private Move selectedMove;
    private Position position;
    private GameCharacter character;

    public MessageCapacityAOERequest(String keyToken, GameCharacter character, Move selectedMove, Position position) {
        type = MessageType.CHARACTER_CAPACITY_AOE_REQUEST;
        this.keyToken = keyToken;
        this.character = character;
        this.selectedMove = selectedMove;
        this.position = position;
    }

    public Move getSelectedMove() {
        return selectedMove;
    }

    public Position getPosition() {
        return position;
    }

    public GameCharacter getCharacter() {
        return character;
    }

    @Override
    public String toString() {
        return new StringBuffer().append(System.getProperty("line.separator"))
                .append("Message Type : ").append(type).append(System.getProperty("line.separator"))
                .append("KeyToken : ").append(keyToken).append(System.getProperty("line.separator"))
                .append("Character : ").append(character.getName()).append(System.getProperty("line.separator"))
                .append("Position : ").append(position.toString()).append(System.getProperty("line.separator"))
                .append("SelectedMove : ").append(selectedMove.getName())
                .toString();
    }
}
