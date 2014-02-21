package com.ghostofpq.kulkan.entities.job.capacity;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.characteristics.SecondaryCharacteristics;

import java.util.ArrayList;

public class AmeliorationPrimary extends Capacity {
    private static final long serialVersionUID = -7036564291741868266L;

    private PrimaryCharacteristics primaryCharacteristics;
    private SecondaryCharacteristics secondaryCharacteristics;

    public AmeliorationPrimary(String name, String description,
                               PrimaryCharacteristics primaryCharacteristics,
                               SecondaryCharacteristics secondaryCharacteristics,
                               int price) {
        this.prerequisites = new ArrayList<Capacity>();
        this.sons = new ArrayList<Capacity>();
        this.name = name;
        this.description = description;

        this.type = CapacityType.AMELIORATION;

        this.price = price;
        this.locked = true;

        this.primaryCharacteristics = primaryCharacteristics;
        this.secondaryCharacteristics = secondaryCharacteristics;
    }

    /**
     * Getters and Setters
     */
    public PrimaryCharacteristics getPrimaryCharacteristics() {
        return primaryCharacteristics;
    }

    public void setPrimaryCharacteristics(PrimaryCharacteristics primaryCharacteristics) {
        this.primaryCharacteristics = primaryCharacteristics;
    }

    public SecondaryCharacteristics getSecondaryCharacteristics() {
        return secondaryCharacteristics;
    }

    public void setSecondaryCharacteristics(SecondaryCharacteristics secondaryCharacteristics) {
        this.secondaryCharacteristics = secondaryCharacteristics;
    }
}