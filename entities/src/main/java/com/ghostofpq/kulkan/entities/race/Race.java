package com.ghostofpq.kulkan.entities.race;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;

import java.io.Serializable;

public abstract class Race implements Serializable {

    private static final long serialVersionUID = -3933914420338387526L;
    private RaceType race;
    private PrimaryCharacteristics baseCaracteristics;
    private PrimaryCharacteristics levelUpCaracteristics;
    private String description;
    private String name;

    public static Race Race(RaceType race) {
        switch (race) {
            case ELVE:
                return new Elve();
            case HUMAN:
                return new Human();
            case DWARF:
                return new Dwarf();
            default:
                return null;
        }
    }

    /**
     * Getters and Setters
     */

    public RaceType getRaceType() {
        return race;
    }

    public void setRace(RaceType race) {
        this.race = race;
    }

    public PrimaryCharacteristics getBaseCaracteristics() {
        return baseCaracteristics;
    }

    public void setBaseCaracteristics(PrimaryCharacteristics baseCaracteristics) {
        this.baseCaracteristics = baseCaracteristics;
    }

    public PrimaryCharacteristics getLevelUpCaracteristics() {
        return levelUpCaracteristics;
    }

    public void setLevelUpCaracteristics(PrimaryCharacteristics levelUpCaracteristics) {
        this.levelUpCaracteristics = levelUpCaracteristics;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
