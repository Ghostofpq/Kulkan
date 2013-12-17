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
        this.strength = strength;
        this.endurance = endurance;
        this.intelligence = intelligence;
        this.will = will;
        this.agility = agility;
        this.movement = movement;
    }

    public void plus(PrimaryCharacteristics characteristics) {
        strength += characteristics.getStrength();
        endurance += characteristics.getEndurance();
        intelligence += characteristics.getIntelligence();
        will += characteristics.getWill();
        agility += characteristics.getAgility();
        movement += characteristics.getMovement();
    }

    /*
     * GETTERS
     */

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getEndurance() {
        return endurance;
    }

    public void setEndurance(int endurance) {
        this.endurance = endurance;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getWill() {
        return will;
    }

    public void setWill(int will) {
        this.will = will;
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public int getMovement() {
        return movement;
    }

    public void setMovement(int movement) {
        this.movement = movement;
    }
}
