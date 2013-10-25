package com.ghostofpq.kulkan.entities.messages.game;

import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;
import java.util.Map;

public class MessageDeploymentFinishedForPlayer extends ClientMessage implements Serializable {
    private Map<GameCharacter, Position> characterPositionMap;
    private int playerNumber;

    public MessageDeploymentFinishedForPlayer(String keyToken, Map<GameCharacter, Position> characterPositionMap, int playerNumber) {
        type = MessageType.FINISH_DEPLOYMENT;
        this.keyToken = keyToken;
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
