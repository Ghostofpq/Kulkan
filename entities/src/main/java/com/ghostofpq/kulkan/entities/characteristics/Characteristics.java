package com.ghostofpq.kulkan.entities.characteristics;

import java.io.Serializable;
import java.util.Map;

public class Characteristics implements Serializable {
    private static final long serialVersionUID = -2135901452592205936L;

    private int attackDamage;
    private int magicalDamage;

    private int armor;
    private int magicResist;

    private int armorPenetration;
    private int magicPenetration;

    private int movement;
    private int speed;

    private int maxHealthPoint;
    private int maxManaPoint;
    private int healthRegeneration;
    private int manaRegeneration;

    private int escape;
    private int criticalStrike;
    private int precision;
    private int resilience;

    private void initFields() {
        this.attackDamage = 0;
        this.magicalDamage = 0;

        this.armor = 0;
        this.magicResist = 0;

        this.armorPenetration = 0;
        this.magicPenetration = 0;

        this.movement = 0;
        this.speed = 0;

        this.maxHealthPoint = 100;
        this.maxManaPoint = 20;
        this.healthRegeneration = 0;
        this.manaRegeneration = 0;

        this.escape = 0;
        this.criticalStrike = 0;
        this.precision = 0;
        this.resilience = 0;
    }

    public Characteristics() {
        initFields();
    }

    public Characteristics(Map<fields, Integer> characteristicsMap) {
        initFields();
        for (fields field : characteristicsMap.keySet()) {
            if (null != characteristicsMap.get(field)) {
                plus(field, characteristicsMap.get(field));
            }
        }
    }

    /**
     * @param attackDamage       physical damage
     * @param magicalDamage      magical damage
     * @param armor              physical resistance
     * @param magicResist        magical resistance
     * @param armorPenetration   physical penetration
     * @param magicPenetration   magical penetration
     * @param movement           movement possibility
     * @param speed              action speed
     * @param maxHealthPoint     max health point
     * @param maxManaPoint       max mana point
     * @param healthRegeneration health regeneration per turn
     * @param manaRegeneration   mana regeneration per turn
     * @param escape             % to dodge an attack
     * @param criticalStrike     % to double the damages of an attack
     * @param precision          % bonus to hit
     * @param resilience         % to reduce critical strike damage
     */
    public Characteristics(int attackDamage, int magicalDamage, int armor, int magicResist,
                           int armorPenetration, int magicPenetration, int movement, int speed,
                           int maxHealthPoint, int maxManaPoint, int healthRegeneration, int manaRegeneration,
                           int escape, int criticalStrike, int precision, int resilience) {
        this.attackDamage = attackDamage;
        this.magicalDamage = magicalDamage;

        this.armor = armor;
        this.magicResist = magicResist;

        this.armorPenetration = armorPenetration;
        this.magicPenetration = magicPenetration;

        this.movement = movement;
        this.speed = speed;

        this.maxHealthPoint = maxHealthPoint;
        this.maxManaPoint = maxManaPoint;
        this.healthRegeneration = healthRegeneration;
        this.manaRegeneration = manaRegeneration;

        this.escape = escape;
        this.criticalStrike = criticalStrike;
        this.precision = precision;
        this.resilience = resilience;
    }

    public void plus(Characteristics characteristics) {
        this.attackDamage += characteristics.getAttackDamage();
        this.magicalDamage += characteristics.getMagicalDamage();

        this.armor += characteristics.getArmor();
        this.magicResist += characteristics.getMagicResist();

        this.armorPenetration += characteristics.getArmorPenetration();
        this.magicPenetration += characteristics.getMagicPenetration();

        this.movement += characteristics.getMovement();
        this.speed += characteristics.getSpeed();

        this.maxHealthPoint += characteristics.getMaxHealthPoint();
        this.maxManaPoint += characteristics.getMaxManaPoint();
        this.healthRegeneration += characteristics.getHealthRegeneration();
        this.manaRegeneration += characteristics.getManaRegeneration();

        this.escape += characteristics.getEscape();
        this.criticalStrike += characteristics.getCriticalStrike();
        this.precision += characteristics.getPrecision();
        this.resilience += characteristics.getResilience();
    }

    public void plus(fields field, int value) {
        switch (field) {
            case ARMOR:
                this.armor += value;
                break;
            case MAGIC_RESIST:
                this.magicResist += value;
                break;
            case ATTACK_DAMAGE:
                this.attackDamage += value;
                break;
            case MAGICAL_DAMAGE:
                this.magicalDamage += value;
                break;
            case ARMOR_PENETRATION:
                this.armorPenetration += value;
                break;
            case MAGIC_PENETRATION:
                this.magicPenetration += value;
                break;
            case LIFE_REGENERATION:
                this.healthRegeneration += value;
                break;
            case MANA_REGENERATION:
                this.manaRegeneration += value;
                break;
            case ESCAPE:
                this.escape += value;
                break;
            case CRITICAL_STRIKE:
                this.criticalStrike += value;
                break;
            case PRECISION:
                this.precision += value;
                break;
            case RESILIENCE:
                this.resilience += value;
                break;
            case SPEED:
                this.speed += value;
                break;
        }
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

    public int getHealthRegeneration() {
        return healthRegeneration;
    }

    public void setHealthRegeneration(int healthRegeneration) {
        this.healthRegeneration = healthRegeneration;
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

    public int getMovement() {
        return movement;
    }

    public void setMovement(int movement) {
        this.movement = movement;
    }

    public int getMaxHealthPoint() {
        return maxHealthPoint;
    }

    public void setMaxHealthPoint(int maxHealthPoint) {
        this.maxHealthPoint = maxHealthPoint;
    }

    public int getMaxManaPoint() {
        return maxManaPoint;
    }

    public void setMaxManaPoint(int maxManaPoint) {
        this.maxManaPoint = maxManaPoint;
    }

    public enum fields {
        ATTACK_DAMAGE,
        MAGICAL_DAMAGE,

        ARMOR,
        MAGIC_RESIST,

        ARMOR_PENETRATION,
        MAGIC_PENETRATION,

        MOVEMENT,
        SPEED,

        MAX_HEALTH,
        MAX_MANA,
        LIFE_REGENERATION,
        MANA_REGENERATION,

        ESCAPE,
        CRITICAL_STRIKE,
        PRECISION,
        RESILIENCE;
    }
}
