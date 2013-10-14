package com.ghostofpq.kulkan.entities.character;


import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.characteristics.SecondaryCharacteristics;
import com.ghostofpq.kulkan.entities.job.Job;
import com.ghostofpq.kulkan.entities.job.Warrior;
import com.ghostofpq.kulkan.entities.race.Race;
import com.ghostofpq.kulkan.entities.race.RaceType;

import java.io.Serializable;
import java.math.BigDecimal;

public class GameCharacter implements Serializable {
    private static final long serialVersionUID = 1519266158170332774L;
    // Evolution
    private final int DEFAULT_START_LEVEL = 1;
    private final int DEFAULT_START_XP = 0;
    private final int DEFAULT_START_NEXT_LEVEL = 100;
    /**
     * Name
     */
    private String name;
    /**
     * {@link com.ghostofpq.kulkan.entities.race.RaceType}
     */
    private Race race;
    /**
     * {@link Gender}
     */
    private Gender gender;
    /**
     * Background story of the character
     */
    private String story;
    /**
     * Level of the character
     */
    private int level;
    /**
     * Current experience of the character
     */
    private int experience;
    /**
     * Experience goal for the next level
     */
    private int nextLevel;

    // Learnings
    /**
     * Current {@link com.ghostofpq.kulkan.entities.job.Job} of the character
     */
    private Job currentJob;
    /**
     * {@link com.ghostofpq.kulkan.entities.job.Warrior} path of the character
     */
    private Warrior jobWarrior;

    // Caracteristics
    /**
     * {@link PrimaryCharacteristics} of the character acquired by leveling
     */
    private PrimaryCharacteristics characteristics;
    /**
     * {@link com.ghostofpq.kulkan.entities.characteristics.SecondaryCharacteristics} of the character acquired by calculation
     * from leveling
     */
    private SecondaryCharacteristics secondaryCharacteristics;
    /**
     * Aggregated {@link PrimaryCharacteristics} of the character (with job and
     * equipment)
     */
    private PrimaryCharacteristics aggregatedCharacteristics;
    /**
     * Aggregated {@link SecondaryCharacteristics} of the character (with job
     * and equipment)
     */
    private SecondaryCharacteristics aggregatedSecondaryCharacteristics;
    /**
     * Health point of the character
     */
    private int currentHealthPoint;
    private int maxHealthPoint;
    /**
     * Mana point of the character
     */
    private int currentManaPoint;
    private int maxManaPoint;

    private int hourglass;
    private PointOfView headingAngle;

    private boolean hasMoved;
    private boolean hasActed;

    /**
     * Creates a new Character level 1 Warrior.
     *
     * @param name   name of the character
     * @param race   {@link com.ghostofpq.kulkan.entities.race.RaceType} of the character
     * @param gender {@link Gender} of the character
     */
    public GameCharacter(String name, RaceType race, Gender gender) {
        // Identity
        this.name = name;
        this.race = Race.Race(race);
        this.gender = gender;

        // XP
        level = DEFAULT_START_LEVEL;
        experience = DEFAULT_START_XP;
        nextLevel = DEFAULT_START_NEXT_LEVEL;

        // Jobs
        jobWarrior = new Warrior();
        currentJob = jobWarrior;

        // Caracteristics
        characteristics = getRace().getBaseCaracteristics();
        secondaryCharacteristics = new SecondaryCharacteristics(characteristics);

        updateLifeAndManaPoint();

        currentHealthPoint = maxHealthPoint;
        currentManaPoint = maxManaPoint;
    }

    public void gainXp(double experience) {
        this.experience += experience;
        if (canLevelUp()) {
            this.experience -= nextLevel;
            levelUp();
        }
    }

    public void gainJobpoints(int jobPoints) {
        this.currentJob.gainJobPoints(jobPoints);
    }

    public boolean canLevelUp() {
        return (experience >= nextLevel);
    }

    public void levelUp() {
        level++;
        calculateNextLevel();
        characteristics.plus(getRace().getLevelUpCaracteristics());

        updateLifeAndManaPoint();
    }

    private void calculateNextLevel() {
        double coef = (Math.sqrt(level));
        this.nextLevel = (int) Math.floor(coef * nextLevel) + nextLevel;
    }

    private void updateLifeAndManaPoint() {
        calculateAggregatedCaracteristics();
        maxHealthPoint = getEndurance() * 10;
        maxManaPoint = getIntelligence() * 10;
    }

    private PrimaryCharacteristics getBonusFromJobs() {
        PrimaryCharacteristics result = new PrimaryCharacteristics(0, 0, 0, 0,
                0, 0);
        result.plus(jobWarrior.getAggregatedCaracteristics());
        return result;
    }

    private PrimaryCharacteristics getBonusFromEquipement() {
        PrimaryCharacteristics result = new PrimaryCharacteristics(0, 0, 0, 0,
                0, 0);

        return result;
    }

    private void calculateAggregatedCaracteristics() {
        this.aggregatedCharacteristics = characteristics;
        this.aggregatedCharacteristics.plus(getBonusFromJobs());
        this.aggregatedCharacteristics.plus(getBonusFromEquipement());
        this.aggregatedSecondaryCharacteristics = new SecondaryCharacteristics(aggregatedCharacteristics);
    }

    public void addHealthPoint(int healthPoint) {
        currentHealthPoint += healthPoint;
        if (currentHealthPoint < 0) {
            currentHealthPoint = 0;
        }
    }

    public void addManaPoint(int manaPoint) {
        currentManaPoint += manaPoint;
        if (currentManaPoint < 0) {
            currentManaPoint = 0;
        }
    }

    public boolean tickHourglass() {
        boolean result = false;
        if (isAlive()) {
            hourglass -= getAgility();
            // log.debug("{} : {}", getCharacter().getName(), hourglass);
            if (hourglass <= 0) {
                int delta = Math.abs(hourglass);
                hourglass = 100 - delta;
                setHasMoved(false);
                setHasActed(false);
                result = true;
            }
        } else {
            hourglass = 100;
            result = false;
        }

        return result;
    }

    /*
     * GETTERS & SETTERS
     */

    public String getName() {
        return name;
    }

    public Race getRace() {
        return race;
    }

    public Gender getGender() {
        return gender;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public int getNextLevel() {
        return nextLevel;
    }

    public Job getCurrentJob() {
        return currentJob;
    }

    public Warrior getJobWarrior() {
        return jobWarrior;
    }

    public PrimaryCharacteristics getCharacteristics() {
        return characteristics;
    }

    public SecondaryCharacteristics getSecondaryCharacteristics() {
        return secondaryCharacteristics;
    }

    public PrimaryCharacteristics getAggregatedCharacteristics() {
        return aggregatedCharacteristics;
    }

    public SecondaryCharacteristics getAggregatedSecondaryCharacteristics() {
        return aggregatedSecondaryCharacteristics;
    }

    public int getCurrentHealthPoint() {
        return currentHealthPoint;
    }

    public int getMaxHealthPoint() {
        return maxHealthPoint;
    }

    public int getCurrentManaPoint() {
        return currentManaPoint;
    }

    public int getMaxManaPoint() {
        return maxManaPoint;
    }

    public boolean isAlive() {
        return (currentHealthPoint > 0);
    }
    /*
    * CHARACTERISTICS GETTERS
    */

    public int getStrength() {
        return getAggregatedCharacteristics().getStrength();
    }

    public int getEndurance() {
        return getAggregatedCharacteristics().getEndurance();
    }

    public int getIntelligence() {
        return getAggregatedCharacteristics().getIntelligence();
    }

    public int getWill() {
        return getAggregatedCharacteristics().getWill();
    }

    public int getAgility() {
        return getAggregatedCharacteristics().getAgility();
    }

    public int getMovement() {
        return getAggregatedCharacteristics().getMovement();
    }

    public int getAttackDamage() {
        return getAggregatedSecondaryCharacteristics().getAttackDamage();
    }

    public int getMagicalDamage() {
        return getAggregatedSecondaryCharacteristics().getMagicalDamage();
    }

    public int getArmor() {
        return getAggregatedSecondaryCharacteristics().getArmor();
    }

    public int getMagicResist() {
        return getAggregatedSecondaryCharacteristics().getMagicResist();
    }

    public int getArmorPenetration() {
        return getAggregatedSecondaryCharacteristics().getArmorPenetration();
    }

    public int getMagicPenetration() {
        return getAggregatedSecondaryCharacteristics().getMagicPenetration();
    }

    public int getSpeed() {
        return getAggregatedSecondaryCharacteristics().getSpeed();
    }

    public int getLifeRegeneration() {
        return getAggregatedSecondaryCharacteristics().getLifeRegeneration();
    }

    public int getManaRegeneration() {
        return getAggregatedSecondaryCharacteristics().getManaRegeneration();
    }

    public BigDecimal getEscape() {
        return getAggregatedSecondaryCharacteristics().getEscape();
    }

    public BigDecimal getCriticalStrike() {
        return getAggregatedSecondaryCharacteristics().getCriticalStrike();
    }

    public BigDecimal getPrecision() {
        return getAggregatedSecondaryCharacteristics().getPrecision();
    }

    public BigDecimal getResilience() {
        return getAggregatedSecondaryCharacteristics().getResilience();
    }

    public int getHourglass() {
        return hourglass;
    }

    public void setHourglass(int hourglass) {
        this.hourglass = hourglass;
    }

    public PointOfView getHeadingAngle() {
        return headingAngle;
    }

    public void setHeadingAngle(PointOfView headingAngle) {
        this.headingAngle = headingAngle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameCharacter that = (GameCharacter) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public boolean hasActed() {
        return hasActed;
    }

    public void setHasActed(boolean hasActed) {
        this.hasActed = hasActed;
    }
}
