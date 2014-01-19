package com.ghostofpq.kulkan.entities.messages.game;

import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;

public class MessageCharacterActionMove extends ClientMessage implements Serializable {
    private GameCharacter character;
    private Position positionToMove;
    private int playerNumber;

    public MessageCharacterActionMove(String keyToken, int playerNumber, GameCharacter character, Position positionToMove) {
        type = MessageType.CHARACTER_ACTION_MOVE;
        this.keyToken = keyToken;
        this.character = character;
        this.positionToMove = positionToMove;
        this.playerNumber = playerNumber;
    }

    public GameCharacter getCharacter() {
        return character;
    }

    public Position getPositionToMove() {
        return positionToMove;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    @Override
    public String toString() {
        return new StringBuffer()
                .append("Message Type :").append(type).append(System.getProperty("line.separator"))
                .append("KeyToken :").append(keyToken).append(System.getProperty("line.separator"))
                .append("PositionToMove :").append(positionToMove.toString()).append(System.getProperty("line.separator"))
                .append("PlayerNumber :").append(playerNumber)
                .toString();
    }
}
