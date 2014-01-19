package com.ghostofpq.kulkan.entities.messages.game.capacity;


import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MessageCapacityFireball extends Message implements Serializable {
    private Map<GameCharacter, Integer> gameCharacterDamageMap;
    private Position position;
    private List<Position> areaOfEffect;
    private GameCharacter gameCharacter;
    private int manaCost;

    public MessageCapacityFireball(GameCharacter gameCharacter, Map<GameCharacter, Integer> gameCharacterDamageMap, Position position, List<Position> areaOfEffect, int manaCost) {
        type = MessageType.CAPACITY_FIREBALL;
        this.gameCharacter = gameCharacter;
        this.gameCharacterDamageMap = gameCharacterDamageMap;
        this.position = position;
        this.areaOfEffect = areaOfEffect;
        this.manaCost = manaCost;
    }

    public Map<GameCharacter, Integer> getGameCharacterDamageMap() {
        return gameCharacterDamageMap;
    }

    public Position getPosition() {
        return position;
    }

    public List<Position> getAreaOfEffect() {
        return areaOfEffect;
    }

    public GameCharacter getGameCharacter() {
        return gameCharacter;
    }

    public int getManaCost() {
        return manaCost;
    }

    @Override
    public String toString() {
        return new StringBuffer().append(System.getProperty("line.separator"))
                .append("Message Type : ").append(type).append(System.getProperty("line.separator"))
                .append("gameChar :").append(gameCharacter.getName()).append(System.getProperty("line.separator"))
                .append("manaCost : ").append(manaCost).append(System.getProperty("line.separator"))
                .append("gameCharacterDamageMap : ").append(gameCharacterDamageMap.toString()).append(System.getProperty("line.separator"))
                .append("position : ").append(position.toString()).append(System.getProperty("line.separator"))
                .append("areaOfEffect : ").append(areaOfEffect.toString())
                .toString();
    }
}