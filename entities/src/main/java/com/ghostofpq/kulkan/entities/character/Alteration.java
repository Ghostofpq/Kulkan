package com.ghostofpq.kulkan.entities.character;

import com.ghostofpq.kulkan.entities.characteristics.Characteristics;

import java.io.Serializable;

public class Alteration implements Serializable {
    private String name;
    private String description;
    private Characteristics characteristics;
    private int duration;

    public Alteration(String name, String description, Characteristics characteristics, int duration) {
        this.name = name;
        this.description = description;
        this.characteristics = characteristics;
        this.duration = duration;
    }

    public void tick() {
        this.duration--;
    }

    public boolean isActive() {
        return duration > 0;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Characteristics getCharacteristics() {
        return characteristics;
    }

    public int getDuration() {
        return duration;
    }
}
