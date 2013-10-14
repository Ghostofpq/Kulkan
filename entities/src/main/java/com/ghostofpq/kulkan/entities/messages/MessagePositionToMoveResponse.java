package com.ghostofpq.kulkan.entities.messages;

import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.commons.Tree;

import java.io.Serializable;
import java.util.List;

public class MessagePositionToMoveResponse extends Message implements Serializable {
    private List<Position> possiblePositionsToMove;
    private Tree<Position> possiblePositionsToMoveTree;

    public MessagePositionToMoveResponse(Tree<Position> possiblePositionsToMoveTree, List<Position> possiblePositionsToMove) {
        type = MessageType.CHARACTER_POSITION_TO_MOVE_RESPONSE;
        this.possiblePositionsToMoveTree = possiblePositionsToMoveTree;
        this.possiblePositionsToMove = possiblePositionsToMove;
    }

    public Tree<Position> getPossiblePositionsToMoveTree() {
        return possiblePositionsToMoveTree;
    }

    public List<Position> getPossiblePositionsToMove() {
        return possiblePositionsToMove;
    }
}
