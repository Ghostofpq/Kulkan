package com.ghostofpq.kulkan.entities.messages;


import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.character.GameCharacter;

import java.io.Serializable;
import java.util.Map;

public class MessageDeploymentPositionsOfPlayer extends Message implements Serializable {
    private Map<GameCharacter, Position> characterPositionMap;
    private int playerNumber;

    public MessageDeploymentPositionsOfPlayer(Map<GameCharacter, Position> characterPositionMap, int playerNumber) {
        type = MessageType.OTHER_PLAYER_DEPLOYMENT;
        this.characterPositionMap = characterPositionMap;
        this.playerNumber = playerNumber;
    }

    public Map<GameCharacter, Position> getCharacterPositionMap() {
        return characterPositionMap;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }
}
