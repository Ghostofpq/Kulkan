package com.ghostofpq.kulkan.entities.clan;

import com.ghostofpq.kulkan.entities.characteristics.Characteristics;
import com.ghostofpq.kulkan.entities.job.JobType;

import java.io.Serializable;

public abstract class Clan implements Serializable {

    private static final long serialVersionUID = -3933914420338387526L;

    private ClanType clan;
    private Characteristics baseCharacteristics;
    private Characteristics levelUpCharacteristics;
    private String description;
    private String name;
    private JobType baseJob;

    public static Clan Clan(ClanType clan) {
        switch (clan) {
            case GORILLA:
                return new Gorilla();
            case PANTHER:
                return new Panther();
            case ARA:
                return new Ara();
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

    public Characteristics getBaseCharacteristics() {
        return baseCharacteristics;
    }

    public Characteristics getLevelUpCharacteristics() {
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

    protected void setBaseCharacteristics(Characteristics baseCharacteristics) {
        this.baseCharacteristics = baseCharacteristics;
    }

    protected void setLevelUpCharacteristics(Characteristics levelUpCharacteristics) {
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
