package com.ghostofpq.kulkan.entities.characteristics;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
    private BigDecimal escape;
    private BigDecimal criticalStrike;
    private BigDecimal precision;
    private BigDecimal resilience;

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

        escape = new BigDecimal("0.00");
        escape.setScale(2, RoundingMode.DOWN);
        criticalStrike = new BigDecimal("0.00");
        criticalStrike.setScale(2, RoundingMode.DOWN);
        precision = new BigDecimal("0.00");
        precision.setScale(2, RoundingMode.DOWN);
        resilience = new BigDecimal("0.00");
        resilience.setScale(2, RoundingMode.DOWN);
    }

    public SecondaryCharacteristics(int attackDamage, int magicalDamage,
                                    int armor, int magicResist, int armorPenetration,
                                    int magicPenetration, int speed, int lifeRegeneration,
                                    int manaRegeneration, BigDecimal escape, BigDecimal criticalStrike,
                                    BigDecimal precision, BigDecimal resilience) {
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
        this.escape.setScale(2, RoundingMode.DOWN);
        this.criticalStrike = criticalStrike;
        this.criticalStrike.setScale(2, RoundingMode.DOWN);
        this.precision = precision;
        this.precision.setScale(2, RoundingMode.DOWN);
        this.resilience = resilience;
        this.resilience.setScale(2, RoundingMode.DOWN);
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

        double escapeD = characteristics.getAgility() / 1000;
        escape = new BigDecimal(escapeD);
        escape.setScale(2, RoundingMode.DOWN);
        double criticalStrikeD = characteristics.getAgility() / 1000;
        criticalStrike = new BigDecimal(criticalStrikeD);
        criticalStrike.setScale(2, RoundingMode.DOWN);
        double precisionD = Math.max(characteristics.getStrength(),
                characteristics.getIntelligence()) / 1000;
        precision = new BigDecimal(precisionD);
        precision.setScale(2, RoundingMode.DOWN);
        double resilienceD = Math.max(characteristics.getEndurance(),
                characteristics.getWill()) / 1000;
        resilience = new BigDecimal(resilienceD);
        resilience.setScale(2, RoundingMode.DOWN);
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

        escape = escape.add(secondaryCharacteristics.getEscape());
        criticalStrike = criticalStrike.add(secondaryCharacteristics.getCriticalStrike());
        precision = precision.add(secondaryCharacteristics.getPrecision());
        resilience = resilience.add(secondaryCharacteristics.getResilience());
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

    public BigDecimal getEscape() {
        return escape;
    }

    public void setEscape(BigDecimal escape) {
        this.escape = escape;
    }

    public BigDecimal getCriticalStrike() {
        return criticalStrike;
    }

    public void setCriticalStrike(BigDecimal criticalStrike) {
        this.criticalStrike = criticalStrike;
    }

    public BigDecimal getPrecision() {
        return precision;
    }

    public void setPrecision(BigDecimal precision) {
        this.precision = precision;
    }

    public BigDecimal getResilience() {
        return resilience;
    }

    public void setResilience(BigDecimal resilience) {
        this.resilience = resilience;
    }
}
