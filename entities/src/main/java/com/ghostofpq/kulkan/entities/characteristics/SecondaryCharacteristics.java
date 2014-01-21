package com.ghostofpq.kulkan.entities.characteristics;

import java.io.Serializable;

public class SecondaryCharacteristics implements Serializable {

    private static final long serialVersionUID = -2135901452592205936L;
    private int attackDamage;
    private int magicalDamage;
    private int armor;
    private int magicResist;
    private int armorPenetration;
    private int magicPenetration;
    private int speed;
    private int lifeRegeneration;
    private int manaRegeneration;
    private int escape;
    private int criticalStrike;
    private int precision;
    private int resilience;

    public SecondaryCharacteristics() {
        attackDamage = 0;
        magicalDamage = 0;

        armor = 0;
        magicResist = 0;

        armorPenetration = 0;
        magicPenetration = 0;

        speed = 0;

        lifeRegeneration = 0;
        manaRegeneration = 0;

        escape = 0;
        criticalStrike = 0;
        precision = 0;
        resilience = 0;
    }

    public SecondaryCharacteristics(int attackDamage, int magicalDamage,
                                    int armor, int magicResist, int armorPenetration,
                                    int magicPenetration, int speed, int lifeRegeneration,
                                    int manaRegeneration, int escape, int criticalStrike,
                                    int precision, int resilience) {
        this.attackDamage = attackDamage;
        this.magicalDamage = magicalDamage;

        this.armor = armor;
        this.magicResist = magicResist;

        this.armorPenetration = armorPenetration;
        this.magicPenetration = magicPenetration;

        this.speed = speed;

        this.lifeRegeneration = lifeRegeneration;
        this.manaRegeneration = manaRegeneration;

        this.escape = escape;
        this.criticalStrike = criticalStrike;
        this.precision = precision;
        this.resilience = resilience;
    }

    public SecondaryCharacteristics(PrimaryCharacteristics characteristics) {
        attackDamage = characteristics.getStrength();
        magicalDamage = characteristics.getIntelligence();

        armor = characteristics.getEndurance();
        magicResist = characteristics.getWill();

        armorPenetration = 0;
        magicPenetration = 0;

        speed = 0;

        lifeRegeneration = 0;
        manaRegeneration = 0;

        escape = characteristics.getAgility() / 10;
        criticalStrike = characteristics.getAgility() / 10;
        precision = Math.max(characteristics.getStrength(), characteristics.getIntelligence()) / 10;
        resilience = Math.max(characteristics.getEndurance(), characteristics.getWill()) / 10;
    }

    public void plus(SecondaryCharacteristics secondaryCharacteristics) {
        attackDamage += secondaryCharacteristics.getAttackDamage();
        magicalDamage += secondaryCharacteristics.getMagicalDamage();

        armor += secondaryCharacteristics.getArmor();
        magicResist += secondaryCharacteristics.getMagicResist();

        armorPenetration += secondaryCharacteristics.getArmorPenetration();
        magicPenetration += secondaryCharacteristics.getMagicPenetration();

        speed += secondaryCharacteristics.getSpeed();

        lifeRegeneration += secondaryCharacteristics.getLifeRegeneration();
        manaRegeneration += secondaryCharacteristics.getManaRegeneration();

        escape += secondaryCharacteristics.getEscape();
        criticalStrike += secondaryCharacteristics.getCriticalStrike();
        precision += secondaryCharacteristics.getPrecision();
        resilience += secondaryCharacteristics.getResilience();
    }

    /*
     * GETTERS
     */

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getMagicalDamage() {
        return magicalDamage;
    }

    public void setMagicalDamage(int magicalDamage) {
        this.magicalDamage = magicalDamage;
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public int getMagicResist() {
        return magicResist;
    }

    public void setMagicResist(int magicResist) {
        this.magicResist = magicResist;
    }

    public int getArmorPenetration() {
        return armorPenetration;
    }

    public void setArmorPenetration(int armorPenetration) {
        this.armorPenetration = armorPenetration;
    }

    public int getMagicPenetration() {
        return magicPenetration;
    }

    public void setMagicPenetration(int magicPenetration) {
        this.magicPenetration = magicPenetration;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getLifeRegeneration() {
        return lifeRegeneration;
    }

    public void setLifeRegeneration(int lifeRegeneration) {
        this.lifeRegeneration = lifeRegeneration;
    }

    public int getManaRegeneration() {
        return manaRegeneration;
    }

    public void setManaRegeneration(int manaRegeneration) {
        this.manaRegeneration = manaRegeneration;
    }

    public int getEscape() {
        return escape;
    }

    public void setEscape(int escape) {
        this.escape = escape;
    }

    public int getCriticalStrike() {
        return criticalStrike;
    }

    public void setCriticalStrike(int criticalStrike) {
        this.criticalStrike = criticalStrike;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getResilience() {
        return resilience;
    }

    public void setResilience(int resilience) {
        this.resilience = resilience;
    }
}
