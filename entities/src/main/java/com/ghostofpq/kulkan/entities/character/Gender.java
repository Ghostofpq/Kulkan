package com.ghostofpq.kulkan.entities.character;

/**
 * Male or Female
 * User: GhostOfPQ
 * Date: 11/06/13
 */
public enum Gender {

    FEMALE("\u2640"), MALE("\u2642");

    private final String propertyName;

    Gender(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String toString() {
        return propertyName;
    }
}
