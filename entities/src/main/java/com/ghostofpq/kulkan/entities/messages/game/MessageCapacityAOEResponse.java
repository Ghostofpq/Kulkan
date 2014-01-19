package com.ghostofpq.kulkan.entities.messages.game;


import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;
import java.util.List;

public class MessageCapacityAOEResponse extends Message implements Serializable {
    private List<Position> areaOfEffect;

    public MessageCapacityAOEResponse(List<Position> areaOfEffect) {
        type = MessageType.CHARACTER_CAPACITY_AOE_RESPONSE;
        this.areaOfEffect = areaOfEffect;
    }

    public List<Position> getAreaOfEffect() {
        return areaOfEffect;
    }

    @Override
    public String toString() {
        return new StringBuffer()
                .append("Message Type : ").append(type).append(System.getProperty("line.separator"))
                .append("areaOfEffect : ").append(areaOfEffect.toString())
                .toString();
    }
}
