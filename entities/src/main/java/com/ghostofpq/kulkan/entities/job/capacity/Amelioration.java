package com.ghostofpq.kulkan.entities.job.capacity;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;

import java.util.ArrayList;

public class Amelioration extends Capacity {

    private static final long serialVersionUID = -7036564291741868266L;
    private PrimaryCharacteristics caracteristics;

    public Amelioration(String name, String description,
                        PrimaryCharacteristics caracteristics, int price) {
        this.prerequisites = new ArrayList<Capacity>();

        this.name = name;
        this.description = description;

        this.type = CapacityType.AMELIORATION;

        this.price = price;
        this.locked = true;

        this.caracteristics = caracteristics;
    }

    /**
     * Getters and Setters
     */
    public PrimaryCharacteristics getCaracteristics() {
        return caracteristics;
    }

    public void setCaracteristics(PrimaryCharacteristics caracteristics) {
        this.caracteristics = caracteristics;
    }
}
