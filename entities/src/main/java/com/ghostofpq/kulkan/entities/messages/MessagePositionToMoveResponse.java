package com.ghostofpq.kulkan.entities.messages;

import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.commons.Tree;

import java.io.Serializable;

public class MessagePositionToMoveResponse extends Message implements Serializable {

    private Tree<Position> possiblePositionsToMoveTree;

    public MessagePositionToMoveResponse(Tree<Position> possiblePositionsToMoveTree) {
        type = MessageType.CHARACTER_POSITION_TO_MOVE_RESPONSE;
        this.possiblePositionsToMoveTree = possiblePositionsToMoveTree;
    }

    public Tree<Position> getPossiblePositionsToMoveTree() {
        return possiblePositionsToMoveTree;
    }
}
