package com.ghostofpq.kulkan.entities.clan;

public enum ClanType {
    GORILLA("Gorilla"),
    TURTLE("Turtle"),
    JAGUAR("Jaguar"),
    MONKEY("Monkey"),
    PANTHER("Panther"),
    LIZARD("Lizard"),
    ARA("Ara"),
    EAGLE("Eagle"),


    ELVE("Elve"), DWARF("Dwarf"), HUMAN("Human");
    private final String propertyName;

    ClanType(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String toString() {
        return propertyName;
    }
}
