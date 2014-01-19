package com.ghostofpq.kulkan.entities.messages.game;


import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;
import java.util.List;

public class MessageCharacterPositionToUseCapacityResponse extends Message implements Serializable {
    private List<Position> possiblePositionsToUseCapacity;

    public MessageCharacterPositionToUseCapacityResponse(List<Position> possiblePositionsToUseCapacity) {
        type = MessageType.CHARACTER_POSITION_TO_USE_CAPACITY_RESPONSE;
        this.possiblePositionsToUseCapacity = possiblePositionsToUseCapacity;
    }

    public List<Position> getPossiblePositionsToUseCapacity() {
        return possiblePositionsToUseCapacity;
    }

    @Override
    public String toString() {
        return new StringBuffer().
                append("Message Type :").append(type).append(System.getProperty("line.separator"))
                .append("possiblePositionsToUseCapacity :").append(possiblePositionsToUseCapacity.toString())
                .toString();
    }
}
