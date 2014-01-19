package com.ghostofpq.kulkan.entities.messages.game;

import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;
import java.util.List;

public class MessagePositionToMoveResponse extends Message implements Serializable {
    private List<Position> possiblePositionsToMove;

    public MessagePositionToMoveResponse(List<Position> possiblePositionsToMove) {
        type = MessageType.CHARACTER_POSITION_TO_MOVE_RESPONSE;
        this.possiblePositionsToMove = possiblePositionsToMove;
    }

    public List<Position> getPossiblePositionsToMove() {
        return possiblePositionsToMove;
    }


    @Override
    public String toString() {
        return new StringBuffer()
                .append("Message Type :").append(type).append(System.getProperty("line.separator"))
                .append("PossiblePositionsToMove :").append(possiblePositionsToMove.toString())
                .toString();
    }
}
