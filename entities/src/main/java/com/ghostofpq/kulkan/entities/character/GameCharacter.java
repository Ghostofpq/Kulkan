package com.ghostofpq.kulkan.entities.character;


import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.characteristics.SecondaryCharacteristics;
import com.ghostofpq.kulkan.entities.clan.Clan;
import com.ghostofpq.kulkan.entities.clan.ClanType;
import com.ghostofpq.kulkan.entities.inventory.Equipment;
import com.ghostofpq.kulkan.entities.inventory.item.*;
import com.ghostofpq.kulkan.entities.job.Job;
import com.ghostofpq.kulkan.entities.job.JobType;
import com.ghostofpq.kulkan.entities.job.Mage;
import com.ghostofpq.kulkan.entities.job.Warrior;
import com.ghostofpq.kulkan.entities.utils.Range;
import com.ghostofpq.kulkan.entities.utils.RangeType;
import org.bson.types.ObjectId;

import java.io.Serializable;

public class GameCharacter implements Serializable {
    private static final long serialVersionUID = 1519266158170332774L;
    // Evolution
    private final int DEFAULT_START_LEVEL = 1;
    private final int DEFAULT_START_XP = 0;
    private final int DEFAULT_START_NEXT_LEVEL = 100;
    private ObjectId id;
    /**
     * Owner
     */
    private Player player;
    /**
     * Name
     */
    private String name;
    /**
     * {@link com.ghostofpq.kulkan.entities.clan.Clan}
     */
    private Clan clan;
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
     * Current {@link com.ghostofpq.kulkan.entities.job.JobType} of the character
     */
    private JobType currentJob;
    /**
     * {@link com.ghostofpq.kulkan.entities.job.Warrior} path of the character
     */
    private Warrior jobWarrior;
    private Mage jobMage;

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
     * Current Health point of the character
     */
    private int currentHealthPoint;
    /**
     * Max Health point of the character
     */
    private int maxHealthPoint;
    /**
     * Current Mana point of the character
     */
    private int currentManaPoint;
    /**
     * Max Mana point of the character
     */
    private int maxManaPoint;
    private int hourglass;
    private PointOfView headingAngle;
    private Position position;
    private boolean hasMoved;
    private boolean hasActed;
    private boolean isReadyToPlay;
    private Equipment equipment;

    /**
     * Creates a new Character level 1 Warrior.
     *
     * @param name   name of the character
     * @param race   {@link com.ghostofpq.kulkan.entities.clan.ClanType} of the character
     * @param gender {@link Gender} of the character
     */
    public GameCharacter(ObjectId id, Player player, String name, ClanType race, Gender gender) {
        // Identity
        this.id = id;
        this.name = name;
        this.clan = Clan.Clan(race);
        this.gender = gender;
        this.player = player;

        // XP
        level = DEFAULT_START_LEVEL;
        experience = DEFAULT_START_XP;
        nextLevel = DEFAULT_START_NEXT_LEVEL;

        // Jobs
        jobWarrior = new Warrior();
        jobMage = new Mage();
        currentJob = JobType.WARRIOR;
        equipment = new Equipment();

        // Caracteristics
        characteristics = getClan().getBaseCaracteristics();
        for (int i = 0; i < level; i++) {
            characteristics.plus(getClan().getLevelUpCaracteristics());
        }
        secondaryCharacteristics = new SecondaryCharacteristics(characteristics);

        updateLifeAndManaPoint();

        initChar();
    }

    public GameCharacter(ObjectId id, Player player, String name, ClanType race, Gender gender, int level, int experience) {
        // Identity
        this.id = id;
        this.name = name;
        this.clan = Clan.Clan(race);
        this.gender = gender;
        this.player = player;

        // XP
        this.level = level;
        this.experience = experience;
        calculateNextLevel();

        // Jobs
        jobWarrior = new Warrior();
        jobMage = new Mage();
        currentJob = JobType.WARRIOR;
        equipment = new Equipment();

        // Caracteristics
        characteristics = getClan().getBaseCaracteristics();
        for (int i = 0; i < level; i++) {
            characteristics.plus(getClan().getLevelUpCaracteristics());
        }
        secondaryCharacteristics = new SecondaryCharacteristics(characteristics);

        updateLifeAndManaPoint();

        initChar();
    }

    public void initChar() {
        currentHealthPoint = maxHealthPoint;
        currentManaPoint = maxManaPoint;
        position = null;
        isReadyToPlay = false;
    }

    public void gainXp(double experience) {
        this.experience += experience;
        while (canLevelUp()) {
            this.experience -= nextLevel;
            levelUp();
        }
    }

    public void gainJobPoints(int jobPoints) {
        getJob(this.currentJob).gainJobPoints(jobPoints);
    }

    public boolean canLevelUp() {
        return (experience >= nextLevel);
    }

    public void levelUp() {
        level++;
        calculateNextLevel();
        characteristics.plus(getClan().getLevelUpCaracteristics());

        updateLifeAndManaPoint();
    }

    private void calculateNextLevel() {
        nextLevel = DEFAULT_START_NEXT_LEVEL;
        if (level != DEFAULT_START_LEVEL) {
            for (int i = DEFAULT_START_LEVEL; i < level; i++) {
                double coef = (Math.sqrt(level));
                nextLevel = (int) Math.floor(coef * nextLevel) + nextLevel;
            }
        }
    }

    private void updateLifeAndManaPoint() {
        calculateAggregatedCharacteristics();
        maxHealthPoint = getEndurance() * 10;
        maxManaPoint = getIntelligence() * 10;
    }

    public void calculateAggregatedCharacteristics() {
        this.aggregatedCharacteristics = new PrimaryCharacteristics();
        this.aggregatedCharacteristics.plus(characteristics);
        this.aggregatedCharacteristics.plus(getJob(this.currentJob).getAggregatedCharacteristics());
        this.aggregatedCharacteristics.plus(equipment.getPrimaryCharacteristics());

        this.aggregatedSecondaryCharacteristics = new SecondaryCharacteristics(aggregatedCharacteristics);
        this.aggregatedSecondaryCharacteristics.plus(equipment.getSecondaryCharacteristics());
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

    public boolean isReadyToPlay() {
        return (isAlive() && isReadyToPlay);
    }

    public void setReadyToPlay(boolean readyToPlay) {
        this.isReadyToPlay = readyToPlay;
    }

    public void tickHourglass() {
        if (isAlive()) {
            hourglass -= getAgility();
            // log.debug("{} : {}", getCharacter().getName(), hourglass);
            if (hourglass <= 0) {
                int delta = Math.abs(hourglass);
                hourglass = 100 - delta;
                setHasMoved(false);
                setHasActed(false);
                isReadyToPlay = true;
            }
        } else {
            hourglass = 100;
            isReadyToPlay = false;
        }
    }

    public Job getJob(JobType jobType) {
        switch (jobType) {
            case WARRIOR:
                return jobWarrior;
            case MAGE:
                return jobMage;
            default:
                return null;
        }
    }

    /*
     * GETTERS & SETTERS
     */

    public String getName() {
        return name;
    }

    public Clan getClan() {
        return clan;
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

    public JobType getCurrentJob() {
        return currentJob;
    }

    public void setCurrentJob(JobType currentJob) {
        this.currentJob = currentJob;
        updateLifeAndManaPoint();
    }

    public Warrior getJobWarrior() {
        return jobWarrior;
    }

    public void setJobWarrior(Warrior jobWarrior) {
        this.jobWarrior = jobWarrior;
    }

    public Mage getJobMage() {
        return jobMage;
    }

    public void setJobMage(Mage jobMage) {
        this.jobMage = jobMage;
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

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Player getPlayer() {
        return player;
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

    public int getEscape() {
        return getAggregatedSecondaryCharacteristics().getEscape();
    }

    public int getCriticalStrike() {
        return getAggregatedSecondaryCharacteristics().getCriticalStrike();
    }

    public int getPrecision() {
        return getAggregatedSecondaryCharacteristics().getPrecision();
    }

    public int getResilience() {
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

    public Range getRange() {
        if (equipment != null) {
            return equipment.getRange();
        } else {
            return new Range(RangeType.CROSS, 0, 1);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameCharacter that = (GameCharacter) o;

        if (!id.equals(that.id)) return false;

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

    public ObjectId getId() {
        return id;
    }

    public void equip(Item item) throws IllegalArgumentException {
        if (null != item) {
            player.getInventory().removeOne(item.getItemID());
        } else {
            throw new IllegalArgumentException("item can't be null");
        }
        switch (item.getItemType()) {
            case WEAPON:
                Weapon weapon = (Weapon) item;
                if (weapon.getWeaponType().equals(WeaponType.TWO_HANDED)) {
                    unequip(ItemType.HELD_ITEM);
                }
                equipment.setWeapon(weapon);
                break;
            case RING:
                Ring ring = (Ring) item;
                equipment.setRing(ring);
                break;
            case NECKLACE:
                Necklace necklace = (Necklace) item;
                equipment.setNecklace(necklace);
                break;
            case HELMET:
                Helm helm = (Helm) item;
                equipment.setHelm(helm);
                break;
            case ARMOR:
                Armor armor = (Armor) item;
                equipment.setArmor(armor);
                break;
            case HELD_ITEM:
                HeldItem heldItem = (HeldItem) item;
                equipment.setHeldItem(heldItem);
                break;
        }

    }

    public void unequip(ItemType itemType) {
        Item itemToRemove = null;
        switch (itemType) {
            case WEAPON:
                itemToRemove = equipment.getWeapon();
                equipment.setWeapon(null);
                break;
            case RING:
                itemToRemove = equipment.getRing();
                equipment.setRing(null);
                break;
            case NECKLACE:
                itemToRemove = equipment.getNecklace();
                equipment.setNecklace(null);
                break;
            case HELMET:
                itemToRemove = equipment.getHelm();
                equipment.setHelm(null);
                break;
            case ARMOR:
                itemToRemove = equipment.getArmor();
                equipment.setArmor(null);
                break;
            case HELD_ITEM:
                itemToRemove = equipment.getHeldItem();
                equipment.setHeldItem(null);
                break;
        }
        if (null != itemToRemove) {
            player.getInventory().addOne(itemToRemove.getItemID());
        }
    }

    public Equipment getEquipment() {
        return equipment;
    }
}
