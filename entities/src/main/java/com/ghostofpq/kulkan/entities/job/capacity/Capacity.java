package com.ghostofpq.kulkan.entities.job.capacity;

import java.io.Serializable;
import java.util.List;

public abstract class Capacity implements Serializable {

    private static final long serialVersionUID = 6338786566341517746L;
    protected String name;
    protected String description;
    protected CapacityType type;
    protected int price;
    protected boolean locked;
    protected List<Capacity> prerequisites;

    public boolean isAvailable() {
        if (prerequisites.isEmpty()) {
            return true;
        } else {
            for (Capacity prerequisite : prerequisites) {
                if (prerequisite.isLocked()) {
                    return false;
                }
            }
            return true;
        }
    }

    public void addPrerequisites(Capacity prerequisite) {
        prerequisites.add(prerequisite);
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

    public CapacityType getType() {
        return type;
    }

    public void setType(CapacityType type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public List<Capacity> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<Capacity> prerequisites) {
        this.prerequisites = prerequisites;
    }
}
