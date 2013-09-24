package com.ghostofpq.kulkan.entities.race;

public enum RaceType {
    ELVE("Elve"), DWARF("Dwarf"), HUMAN("Human");

    private final String propertyName;

    RaceType(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String toString() {
        return propertyName;
    }
}
