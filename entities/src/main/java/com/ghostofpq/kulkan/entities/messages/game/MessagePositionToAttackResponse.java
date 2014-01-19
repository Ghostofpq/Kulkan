package com.ghostofpq.kulkan.entities.messages.game;

import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;
import java.util.List;

public class MessagePositionToAttackResponse extends Message implements Serializable {
    private List<Position> possiblePositionsToAttack;

    public MessagePositionToAttackResponse(List<Position> possiblePositionsToAttack) {
        type = MessageType.CHARACTER_POSITION_TO_ATTACK_RESPONSE;
        this.possiblePositionsToAttack = possiblePositionsToAttack;
    }

    public List<Position> getPossiblePositionsToAttack() {
        return possiblePositionsToAttack;
    }
}
