package com.ghostofpq.kulkan.entities.messages;

import com.ghostofpq.kulkan.commons.Position;

import java.io.Serializable;
import java.util.List;

public class MessagePositionToAttackResponse extends ClientMessage implements Serializable {
    private List<Position> possiblePositionsToAttack;

    public MessagePositionToAttackResponse(List<Position> possiblePositionsToAttack) {
        type = MessageType.CHARACTER_POSITION_TO_ATTACK_RESPONSE;
        this.possiblePositionsToAttack = possiblePositionsToAttack;
    }

    public List<Position> getPossiblePositionsToAttack() {
        return possiblePositionsToAttack;
    }
}
