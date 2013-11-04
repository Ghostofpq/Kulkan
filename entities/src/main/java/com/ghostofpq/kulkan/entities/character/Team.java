package com.ghostofpq.kulkan.entities.character;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Team implements Serializable {

    private String name;
    private List<GameCharacter> team;

    public Team(String name) {
        this.name = name;
        team = new ArrayList<GameCharacter>();
    }

    public Team() {
        team = new ArrayList<GameCharacter>();
    }

    public boolean isAlive() {
        boolean result = false;
        for (GameCharacter gameCharacter : team) {
            if (gameCharacter.isAlive()) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Getters and Setters
     */

    public List<GameCharacter> getTeam() {
        return team;
    }

    public void setTeam(List<GameCharacter> team) {
        this.team = team;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
