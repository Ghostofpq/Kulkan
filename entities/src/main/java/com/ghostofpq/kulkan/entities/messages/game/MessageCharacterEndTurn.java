package com.ghostofpq.kulkan.entities.messages.game;

import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;

public class MessageCharacterEndTurn extends ClientMessage implements Serializable {
    private GameCharacter character;
    private int playerNumber;

    public MessageCharacterEndTurn(String keyToken, int playerNumber, GameCharacter character) {
        type = MessageType.CHARACTER_ACTION_END_TURN;
        this.keyToken = keyToken;
        this.character = character;
        this.playerNumber = playerNumber;
    }

    public GameCharacter getCharacter() {
        return character;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    @Override
    public String toString() {
        return new StringBuffer().
                append("Message Type :").append(type).append(System.getProperty("line.separator"))
                .append("KeyToken :").append(keyToken).append(System.getProperty("line.separator"))
                .append("Character :").append(character.getName()).append(System.getProperty("line.separator"))
                .append("PlayerNumber :").append(playerNumber)
                .toString();
    }
}
