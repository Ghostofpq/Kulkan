package com.ghostofpq.kulkan.entities.character;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    private static final long serialVersionUID = 3105715593359746378L;
    private String pseudo;
    private int grade;
    private List<GameCharacter> team;
    private List<GameCharacter> stock;

    public Player(String pseudo) {
        this.pseudo = pseudo;

        team = new ArrayList<GameCharacter>();
        stock = new ArrayList<GameCharacter>();
    }

    public Player() {
    }

    /**
     * Getters and Setters
     */

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public List<GameCharacter> getTeam() {
        return team;
    }

    public void setTeam(List<GameCharacter> team) {
        this.team = team;
    }

    public List<GameCharacter> getStock() {
        return stock;
    }

    public void setStock(List<GameCharacter> stock) {
        this.stock = stock;
    }

    public GameCharacter getGameCharacter(GameCharacter gameCharacter) {
        return team.get(team.indexOf(gameCharacter));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (!pseudo.equals(player.pseudo)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return pseudo.hashCode();
    }
}
