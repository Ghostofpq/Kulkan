package com.ghostofpq.kulkan.entities.messages.game;

import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;
import java.util.List;

public class MessageDeploymentFinishedForPlayer extends ClientMessage implements Serializable {
    private List<GameCharacter> charactersList;
    private int playerNumber;

    public MessageDeploymentFinishedForPlayer(String keyToken, int playerNumber, List<GameCharacter> charactersList) {
        type = MessageType.FINISH_DEPLOYMENT;
        this.keyToken = keyToken;
        this.charactersList = charactersList;
        this.playerNumber = playerNumber;
    }

    public List<GameCharacter> getCharactersList() {
        return charactersList;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }
}
