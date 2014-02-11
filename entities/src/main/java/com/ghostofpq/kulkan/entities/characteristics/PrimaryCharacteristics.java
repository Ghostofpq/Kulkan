package com.ghostofpq.kulkan.entities.characteristics;

import java.io.Serializable;

public class PrimaryCharacteristics implements Serializable {
    private static final long serialVersionUID = 6681761667504505940L;
    private int strength;
    private int endurance;
    private int intelligence;
    private int will;
    private int agility;
    private int movement;

    public PrimaryCharacteristics() {
        strength = 0;
        endurance = 0;
        intelligence = 0;
        will = 0;
        agility = 0;
        movement = 0;
    }

    public PrimaryCharacteristics(int strength, int endurance,
                                  int intelligence, int will, int agility, int movement) {
        setStrength(strength);
        setEndurance(endurance);
        setIntelligence(intelligence);
        setWill(will);
        setAgility(agility);
        setMovement(movement);
    }

    public void plus(PrimaryCharacteristics characteristics) {
        setStrength(strength + characteristics.getStrength());
        setEndurance(endurance + characteristics.getEndurance());
        setIntelligence(intelligence + characteristics.getIntelligence());
        setWill(will + characteristics.getWill());
        setAgility(agility + characteristics.getAgility());
        setMovement(movement + characteristics.getMovement());
    }

    public void plus(fields field, int value) {
        switch (field) {
            case STRENGTH:
                setStrength(strength + value);
                break;
            case ENDURANCE:
                setEndurance(endurance + value);
                break;
            case INTELLIGENCE:
                setIntelligence(intelligence + value);
                break;
            case WILL:
                setWill(will + value);
                break;
            case AGILITY:
                setAgility(agility + value);
                break;
            case MOVEMENT:
                setMovement(movement + value);
                break;
        }
    }

    private int validateValue(int value) {
        if (value < 0) {
            return 0;
        } else if (value > 255) {
            return 255;
        } else {
            return value;
        }
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = validateValue(strength);
    }

    public int getEndurance() {
        return endurance;
    }

    public void setEndurance(int endurance) {
        this.endurance = validateValue(endurance);
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = validateValue(intelligence);
    }

    public int getWill() {
        return will;
    }

    public void setWill(int will) {
        this.will = validateValue(will);
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = validateValue(agility);
    }

    public int getMovement() {
        return movement;
    }

    public void setMovement(int movement) {
        this.movement = movement;
    }

    public enum fields {
        STRENGTH, ENDURANCE, INTELLIGENCE, WILL, AGILITY, MOVEMENT
    }
}
