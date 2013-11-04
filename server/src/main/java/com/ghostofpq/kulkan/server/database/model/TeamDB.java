package com.ghostofpq.kulkan.server.database.model;


import java.util.ArrayList;
import java.util.List;

public class TeamDB {
    private String name;
    private List<GameCharacterDB> gameCharacterList;

    public TeamDB(String name) {
        gameCharacterList = new ArrayList<GameCharacterDB>();
    }

    public List<GameCharacterDB> getGameCharacterList() {
        return gameCharacterList;
    }

    public void setGameCharacterList(List<GameCharacterDB> gameCharacterList) {
        this.gameCharacterList = gameCharacterList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
