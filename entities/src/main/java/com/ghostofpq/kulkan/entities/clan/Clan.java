package com.ghostofpq.kulkan.entities.clan;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;

import java.io.Serializable;

public abstract class Clan implements Serializable {

    private static final long serialVersionUID = -3933914420338387526L;
    protected final int BASE_STRENGTH = 10;
    protected final int BASE_ENDURANCE = 10;
    protected final int BASE_INTELLIGENCE = 10;
    protected final int BASE_WILL = 10;
    protected final int BASE_AGILITY = 10;
    protected final int BASE_MOVEMENT = 2;
    private ClanType race;
    private PrimaryCharacteristics baseCaracteristics;
    private PrimaryCharacteristics levelUpCaracteristics;
    private String description;
    private String name;

    public static Clan Race(ClanType race) {
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

    public ClanType getRaceType() {
        return race;
    }

    public void setRace(ClanType race) {
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
