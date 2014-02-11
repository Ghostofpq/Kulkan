package com.ghostofpq.kulkan.entities.clan;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.job.JobType;

import java.io.Serializable;

public abstract class Clan implements Serializable {

    private static final long serialVersionUID = -3933914420338387526L;
    protected final int BASE_STRENGTH = 10;
    protected final int BASE_ENDURANCE = 10;
    protected final int BASE_INTELLIGENCE = 10;
    protected final int BASE_WILL = 10;
    protected final int BASE_AGILITY = 10;
    protected final int BASE_MOVEMENT = 2;
    private ClanType clan;
    private PrimaryCharacteristics baseCharacteristics;
    private PrimaryCharacteristics levelUpCharacteristics;
    private String description;
    private String name;
    private JobType baseJob;

    public static Clan Clan(ClanType clan) {
        switch (clan) {
            case ELVE:
                return new Elve();
            case HUMAN:
                return new Human();
            case DWARF:
                return new Dwarf();
            case GORILLA:
                return new Gorilla();
            case TURTLE:
                return new Turtle();
            case JAGUAR:
                return new Jaguar();
            case MONKEY:
                return new Monkey();
            case PANTHER:
                return new Panther();
            case LIZARD:
                return new Lizard();
            case ARA:
                return new Ara();
            case EAGLE:
                return new Eagle();
            default:
                return null;
        }
    }

    /**
     * Getters and Setters
     */

    public ClanType getClanType() {
        return clan;
    }

    public PrimaryCharacteristics getBaseCharacteristics() {
        return baseCharacteristics;
    }

    public PrimaryCharacteristics getLevelUpCharacteristics() {
        return levelUpCharacteristics;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public JobType getBaseJob() {
        return baseJob;
    }

    protected void setClan(ClanType clan) {
        this.clan = clan;
    }

    protected void setBaseCharacteristics(PrimaryCharacteristics baseCharacteristics) {
        this.baseCharacteristics = baseCharacteristics;
    }

    protected void setLevelUpCharacteristics(PrimaryCharacteristics levelUpCharacteristics) {
        this.levelUpCharacteristics = levelUpCharacteristics;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected void setBaseJob(JobType baseJob) {
        this.baseJob = baseJob;
    }
}
