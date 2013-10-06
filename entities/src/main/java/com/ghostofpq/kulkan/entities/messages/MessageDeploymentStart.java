package com.ghostofpq.kulkan.entities.messages;

import com.ghostofpq.kulkan.entities.character.GameCharacter;

import java.io.Serializable;
import java.util.List;

public class MessageDeploymentStart extends Message implements Serializable {

    private List<GameCharacter> characterList;
    private int playerNumber;

    public MessageDeploymentStart(List<GameCharacter> characterList, int playerNumber) {
        type = MessageType.START_DEPLOYMENT;
        this.characterList = characterList;
        this.playerNumber = playerNumber;
    }

    public List<GameCharacter> getCharacterList() {
        return characterList;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }
}
