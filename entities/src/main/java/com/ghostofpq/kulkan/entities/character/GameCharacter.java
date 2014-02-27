package com.ghostofpq.kulkan.entities.character;


import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.characteristics.Characteristics;
import com.ghostofpq.kulkan.entities.clan.Clan;
import com.ghostofpq.kulkan.entities.clan.ClanType;
import com.ghostofpq.kulkan.entities.inventory.Equipment;
import com.ghostofpq.kulkan.entities.inventory.item.*;
import com.ghostofpq.kulkan.entities.job.Job;
import com.ghostofpq.kulkan.entities.job.JobType;
import com.ghostofpq.kulkan.entities.job.capacity.Capacity;
import com.ghostofpq.kulkan.entities.utils.Range;
import com.ghostofpq.kulkan.entities.utils.RangeType;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameCharacter implements Serializable {
    private static final long serialVersionUID = 1519266158170332774L;
    private final double LEVEL_COEF = 1.5;
    private final int LEVEL_BASE = 100;
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

    protected int jobPoints;
    protected int cumulativeJobPoints;
    // Learnings
    /**
     * Current {@link com.ghostofpq.kulkan.entities.job.JobType} of the character
     */
    private JobType currentJob;

    private List<Job> jobs;
    private List<Alteration> alterations;
    // Caracteristics
    /**
     * {@link com.ghostofpq.kulkan.entities.characteristics.Characteristics} of the character acquired by calculation
     * from leveling
     */
    private Characteristics characteristics;
    /**
     * Aggregated {@link com.ghostofpq.kulkan.entities.characteristics.Characteristics} of the character (with job
     * and equipment)
     */
    private Characteristics aggregatedCharacteristics;
    /**
     * Current Health point of the character
     */
    private int currentHealthPoint;
    /**
     * Current Mana point of the character
     */
    private int currentManaPoint;

    private int hourglass;
    private PointOfView headingAngle;
    private Position position;
    private boolean hasMoved;
    private boolean hasActed;
    private boolean isReadyToPlay;
    private Equipment equipment;

    public GameCharacter(String name, ClanType clan, Gender gender) {
        if (null == name) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (null == clan) {
            throw new IllegalArgumentException("Clan cannot be null");
        }
        if (null == gender) {
            throw new IllegalArgumentException("Gender cannot be null");
        }

        // Identity
        this.name = name;
        this.clan = Clan.Clan(clan);
        this.gender = gender;

        // XP
        this.level = 1;
        this.experience = 0;
        this.jobPoints = 0;
        this.cumulativeJobPoints = 0;
        calculateNextLevel();

        // Jobs
        this.jobs = new ArrayList<Job>();
        this.currentJob = this.clan.getBaseJob();
        this.jobs.add(Job.Job(this.currentJob));

        this.equipment = new Equipment();

        // Caracteristics
        this.characteristics = getClan().getBaseCharacteristics();
        for (int i = 0; i < level; i++) {
            this.characteristics.plus(getClan().getLevelUpCharacteristics());
        }

        alterations = new ArrayList<Alteration>();

        updateAggregatedCharacteristics();

        initChar();
    }

    public void setId(ObjectId id) {
        if (null == id) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        this.id = id;
    }

    public void setPlayer(Player player) {
        if (null == player) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        this.player = player;
    }

    public GameCharacter(ObjectId id, Player player, String name, ClanType clan, Gender gender, int level, int experience, int jobPoints, int cumulativeJobPoints, JobType currentJob, List<Job> jobs, Equipment equipment) {
        if (null == id) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (null == player) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (null == name) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (null == clan) {
            throw new IllegalArgumentException("Clan cannot be null");
        }
        if (null == gender) {
            throw new IllegalArgumentException("Gender cannot be null");
        }

        // Identity
        this.id = id;
        this.name = name;
        this.clan = Clan.Clan(clan);
        this.gender = gender;
        this.player = player;

        // XP
        this.level = level;
        this.experience = experience;
        this.jobPoints = jobPoints;
        this.cumulativeJobPoints = cumulativeJobPoints;
        calculateNextLevel();

        // Jobs
        this.currentJob = currentJob;
        this.jobs = jobs;
        this.equipment = equipment;

        // Caracteristics
        this.characteristics = getClan().getBaseCharacteristics();
        for (int i = 0; i < level; i++) {
            this.characteristics.plus(getClan().getLevelUpCharacteristics());
        }

        alterations = new ArrayList<Alteration>();

        updateAggregatedCharacteristics();

        initChar();
    }

    public void updateAggregatedCharacteristics() {
        this.aggregatedCharacteristics = new Characteristics();
        this.aggregatedCharacteristics.plus(characteristics);
        for (Job job : jobs) {
            this.aggregatedCharacteristics.plus(job.getCharacteristics());
        }
        for (Alteration alteration : alterations) {
            this.aggregatedCharacteristics.plus(alteration.getCharacteristics());
        }
        this.aggregatedCharacteristics.plus(equipment.getCharacteristics());
    }

    public void initChar() {
        currentHealthPoint = getMaxHealthPoint();
        currentManaPoint = getMaxManaPoint();
        position = null;
        isReadyToPlay = false;
        alterations = new ArrayList<Alteration>();
    }

    public void gainXp(double experience) {
        this.experience += experience;
        while (canLevelUp()) {
            levelUp();
        }
    }

    public void gainJobPoints(int jobPoints) {
        this.jobPoints += jobPoints;
        this.cumulativeJobPoints += jobPoints;
    }

    public boolean canLevelUp() {
        return (experience >= nextLevel);
    }

    public void levelUp() {
        level++;
        calculateNextLevel();
        characteristics.plus(getClan().getLevelUpCharacteristics());
    }

    private void calculateNextLevel() {
        nextLevel = (int) (LEVEL_BASE * Math.pow(level, LEVEL_COEF));
    }

    public void addHealthPoint(int healthPoint) {
        currentHealthPoint += healthPoint;
        if (currentHealthPoint > getMaxHealthPoint()) {
            currentHealthPoint = getMaxHealthPoint();
        }
        if (currentHealthPoint < 0) {
            currentHealthPoint = 0;
        }
    }

    public void addManaPoint(int manaPoint) {
        currentManaPoint += manaPoint;
        if (currentManaPoint > getMaxManaPoint()) {
            currentManaPoint = getMaxManaPoint();
        }
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
            hourglass -= getSpeed();
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
        for (Job job : jobs) {
            if (jobType.equals(job.getJobType())) {
                return job;
            }
        }
        return null;
    }


    public boolean canUnlockCapacity(Capacity capacity) {
        return capacity.canBeUnlock(this.jobPoints);
    }

    public void unlockCapacity(JobType jobType, Capacity capacity) {
        if (canUnlockCapacity(capacity)) {
            this.jobPoints -= capacity.getPrice();
            capacity.setLocked(false);
            getJob(jobType).unlockCapacity(capacity);
        }
    }

    public Job getActiveJob() {
        return getJob(currentJob);
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
        updateAggregatedCharacteristics();
    }

    public Characteristics getCharacteristics() {
        return characteristics;
    }

    public Characteristics getAggregatedCharacteristics() {
        return aggregatedCharacteristics;
    }

    public int getCurrentHealthPoint() {
        return currentHealthPoint;
    }


    public int getCurrentManaPoint() {
        return currentManaPoint;
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

    public int getJobPoints() {
        return jobPoints;
    }

    public int getCumulativeJobPoints() {
        return cumulativeJobPoints;
    }

    public List<Alteration> getAlterations() {
        return alterations;
    }

    /*
        * CHARACTERISTICS GETTERS
        */
    public int getAttackDamage() {
        return getAggregatedCharacteristics().getAttackDamage();
    }

    public int getMagicalDamage() {
        return getAggregatedCharacteristics().getMagicalDamage();
    }

    public int getArmor() {
        return getAggregatedCharacteristics().getArmor();
    }

    public int getMagicResist() {
        return getAggregatedCharacteristics().getMagicResist();
    }

    public int getArmorPenetration() {
        return getAggregatedCharacteristics().getArmorPenetration();
    }

    public int getMagicPenetration() {
        return getAggregatedCharacteristics().getMagicPenetration();
    }

    public int getMovement() {
        return getAggregatedCharacteristics().getMovement();
    }

    public int getSpeed() {
        return getAggregatedCharacteristics().getSpeed();
    }

    public int getMaxHealthPoint() {
        return characteristics.getMaxHealthPoint();
    }

    public int getMaxManaPoint() {
        return characteristics.getMaxManaPoint();
    }

    public int getHealthRegeneration() {
        return getAggregatedCharacteristics().getHealthRegeneration();
    }

    public int getManaRegeneration() {
        return getAggregatedCharacteristics().getManaRegeneration();
    }

    public int getEscape() {
        return getAggregatedCharacteristics().getEscape();
    }

    public int getCriticalStrike() {
        return getAggregatedCharacteristics().getCriticalStrike();
    }

    public int getPrecision() {
        return getAggregatedCharacteristics().getPrecision();
    }

    public int getResilience() {
        return getAggregatedCharacteristics().getResilience();
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

    public List<Job> getJobs() {
        return jobs;
    }

    public Equipment getEquipment() {
        return equipment;
    }
}
