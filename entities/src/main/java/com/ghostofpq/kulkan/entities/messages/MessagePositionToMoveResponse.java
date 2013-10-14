package com.ghostofpq.kulkan.entities.messages;

import com.ghostofpq.kulkan.commons.Position;

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
}
