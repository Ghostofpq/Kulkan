package com.ghostofpq.kulkan.entities.job;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.job.capacity.AmeliorationPrimary;
import com.ghostofpq.kulkan.entities.job.capacity.Capacity;
import com.ghostofpq.kulkan.entities.job.capacity.Move;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Job implements Serializable {

    private static final long serialVersionUID = 7613901055857944135L;
    private String name;
    private String description;

    private List<Capacity> skillTree;

    private List<Move> unlockedMoves;
    private List<AmeliorationPrimary> unlockedAmeliorationPrimaries;

    private int jobPoints;
    private int cumulatedJobPoints;

    public Job(String name, String description) {
        this.name = name;
        this.description = description;

        this.jobPoints = 0;
        this.cumulatedJobPoints = 0;

        this.unlockedMoves = new ArrayList<Move>();
        this.unlockedAmeliorationPrimaries = new ArrayList<AmeliorationPrimary>();
    }

    public void gainJobPoints(int jobPoints) {
        this.jobPoints += jobPoints;
    }

    public boolean canUnlockCapacity(Capacity capacity) {
        if (capacity.isAvailable() && this.jobPoints >= capacity.getPrice()
                && !capacity.isLocked()) {
            return true;
        }
        return false;
    }

    public void unlockCapacity(Capacity capacity) {
        if (canUnlockCapacity(capacity)) {
            this.jobPoints -= capacity.getPrice();
            capacity.setLocked(false);

            switch (capacity.getType()) {
                case AMELIORATION:
                    unlockedAmeliorationPrimaries.add((AmeliorationPrimary) capacity);
                    break;
                case MOVE:
                    unlockedMoves.add((Move) capacity);
                    break;
            }
        }
    }

    public PrimaryCharacteristics getAggregatedCaracteristics() {
        PrimaryCharacteristics result = new PrimaryCharacteristics(0, 0, 0, 0,
                0, 0);

        for (AmeliorationPrimary ameliorationPrimary : unlockedAmeliorationPrimaries) {
            result.plus(ameliorationPrimary.getCaracteristics());
        }

        return result;
    }

    /**
     * Getters and Setters
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Capacity> getSkillTree() {
        return skillTree;
    }

    public void setSkillTree(List<Capacity> skillTree) {
        this.skillTree = skillTree;
    }

    public List<Move> getUnlockedMoves() {
        return unlockedMoves;
    }

    public void setUnlockedMoves(List<Move> unlockedMoves) {
        this.unlockedMoves = unlockedMoves;
    }

    public List<AmeliorationPrimary> getUnlockedAmeliorationPrimaries() {
        return unlockedAmeliorationPrimaries;
    }

    public void setUnlockedAmeliorationPrimaries(List<AmeliorationPrimary> unlockedAmeliorationPrimaries) {
        this.unlockedAmeliorationPrimaries = unlockedAmeliorationPrimaries;
    }

    public int getJobPoints() {
        return jobPoints;
    }

    public void setJobPoints(int jobPoints) {
        this.jobPoints = jobPoints;
    }

    public int getCumulatedJobPoints() {
        return cumulatedJobPoints;
    }

    public void setCumulatedJobPoints(int cumulatedJobPoints) {
        this.cumulatedJobPoints = cumulatedJobPoints;
    }
}
