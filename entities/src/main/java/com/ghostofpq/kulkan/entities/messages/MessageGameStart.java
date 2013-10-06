package com.ghostofpq.kulkan.entities.messages;

import com.ghostofpq.kulkan.entities.battlefield.Battlefield;
import com.ghostofpq.kulkan.entities.character.Player;

import java.io.Serializable;
import java.util.List;

public class MessageGameStart extends Message implements Serializable {
    private Battlefield battlefield;
    private String gameID;

    public MessageGameStart(String gameID, Battlefield battlefield, List<Player> players) {
        type = MessageType.GAME_START;
        this.battlefield = battlefield;
        this.gameID = gameID;
    }

    public Battlefield getBattlefield() {
        return battlefield;
    }

    public String getGameID() {
        return gameID;
    }
}
