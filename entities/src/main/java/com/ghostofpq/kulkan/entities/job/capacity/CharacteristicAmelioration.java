package com.ghostofpq.kulkan.entities.job.capacity;

import com.ghostofpq.kulkan.entities.characteristics.Characteristics;

import java.util.ArrayList;

public class CharacteristicAmelioration extends Capacity {
    private static final long serialVersionUID = -7036564291741868266L;

    private Characteristics characteristics;

    public CharacteristicAmelioration(String name, String description,
                                      Characteristics characteristics,
                                      int price) {
        this.prerequisites = new ArrayList<Capacity>();
        this.sons = new ArrayList<Capacity>();
        this.name = name;
        this.description = description;

        this.type = CapacityType.AMELIORATION;

        this.price = price;
        this.locked = true;

        this.characteristics = characteristics;
    }

    /**
     * Getters and Setters
     */

    public Characteristics getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(Characteristics characteristics) {
        this.characteristics = characteristics;
    }
}