package com.ghostofpq.kulkan.entities.clan;

public enum ClanType {
    GORILLA("Gorilla"),
    PANTHER("Panther"),
    ARA("Ara");

    private final String propertyName;

    ClanType(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String toString() {
        return propertyName;
    }
}
