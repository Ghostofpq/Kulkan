package com.ghostofpq.kulkan.server.database.model;

import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.character.Gender;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.clan.ClanType;
import com.ghostofpq.kulkan.entities.inventory.Equipment;
import com.ghostofpq.kulkan.entities.inventory.ItemFactory;
import com.ghostofpq.kulkan.entities.inventory.item.*;
import com.ghostofpq.kulkan.entities.job.Job;
import com.ghostofpq.kulkan.entities.job.JobType;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class GameCharacterDB {
    @Id
    private ObjectId id;
    private String name;
    private Gender gender;
    private ClanType clanType;
    private Integer lvl;
    private Integer currentXp;
    private Integer currentJP;
    private Integer cumulativeJP;
    private JobType currentJob;
    private List<JobStatusDB> jobStatusDBs;
    private String armor;
    private String weapon;
    private String heldItem;
    private String necklace;
    private String ring;
    private String helm;

    public GameCharacterDB() {
    }

    public GameCharacterDB(GameCharacter gameCharacter) {
        if (null != gameCharacter.getId()) {
            this.id = gameCharacter.getId();
        } else {
            this.id = new ObjectId();
        }
        this.name = gameCharacter.getName();
        this.gender = gameCharacter.getGender();
        this.clanType = gameCharacter.getClan().getClanType();
        this.lvl = gameCharacter.getLevel();
        this.currentXp = gameCharacter.getExperience();
        this.currentJP = gameCharacter.getJobPoints();
        this.cumulativeJP = gameCharacter.getCumulativeJobPoints();
        this.currentJob = gameCharacter.getCurrentJob();
        this.jobStatusDBs = new ArrayList<JobStatusDB>();
        for (Job job : gameCharacter.getJobs()) {
            jobStatusDBs.add(new JobStatusDB(job));
        }
        this.armor = null;
        this.weapon = null;
        this.heldItem = null;
        this.necklace = null;
        this.ring = null;
        this.helm = null;

        if (null != gameCharacter.getEquipment().getArmor()) {
            this.armor = gameCharacter.getEquipment().getArmor().getItemID();
        }
        if (null != gameCharacter.getEquipment().getHelm()) {
            this.helm = gameCharacter.getEquipment().getHelm().getItemID();
        }
        if (null != gameCharacter.getEquipment().getWeapon()) {
            this.weapon = gameCharacter.getEquipment().getWeapon().getItemID();
        }
        if (null != gameCharacter.getEquipment().getHeldItem()) {
            this.heldItem = gameCharacter.getEquipment().getHeldItem().getItemID();
        }
        if (null != gameCharacter.getEquipment().getNecklace()) {
            this.necklace = gameCharacter.getEquipment().getNecklace().getItemID();
        }
        if (null != gameCharacter.getEquipment().getRing()) {
            this.ring = gameCharacter.getEquipment().getRing().getItemID();
        }
    }

    public GameCharacter toGameCharacter(Player player) {
        List<Job> jobs = new ArrayList<Job>();
        if (null != getJobStatusDBs()) {
            for (JobStatusDB jobStatusDB : getJobStatusDBs()) {
                Job job = jobStatusDB.toJob();
                jobs.add(job);
            }
        }
        Equipment equipment = new Equipment();
        if (null != getHelm()) {
            Helm helm = (Helm) ItemFactory.createItem(getHelm());
            equipment.setHelm(helm);
        }
        if (null != getArmor()) {
            Armor armor = (Armor) ItemFactory.createItem(getArmor());
            equipment.setArmor(armor);
        }
        if (null != getWeapon()) {
            Weapon weapon = (Weapon) ItemFactory.createItem(getWeapon());
            equipment.setWeapon(weapon);
        }
        if (null != getHeldItem()) {
            HeldItem heldItem = (HeldItem) ItemFactory.createItem(getHeldItem());
            equipment.setHeldItem(heldItem);
        }
        if (null != getNecklace()) {
            Necklace necklace = (Necklace) ItemFactory.createItem(getNecklace());
            equipment.setNecklace(necklace);
        }
        if (null != getRing()) {
            Ring ring = (Ring) ItemFactory.createItem(getRing());
            equipment.setRing(ring);
        }

        GameCharacter gameCharacter = new GameCharacter(
                getId(),
                player,
                getName(),
                getClanType(),
                getGender(),
                getLvl(),
                getCurrentXp(),
                getCurrentJP(),
                getCumulativeJP(),
                getCurrentJob(),
                jobs,
                equipment
        );

        gameCharacter.updateAggregatedCharacteristics();
        return gameCharacter;
    }

    public ObjectId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Integer getLvl() {
        return lvl;
    }

    public void setLvl(Integer lvl) {
        this.lvl = lvl;
    }

    public Integer getCurrentXp() {
        return currentXp;
    }

    public void setCurrentXp(Integer currentXp) {
        this.currentXp = currentXp;
    }

    public ClanType getClanType() {
        return clanType;
    }

    public void setClanType(ClanType clanType) {
        this.clanType = clanType;
    }

    public List<JobStatusDB> getJobStatusDBs() {
        return jobStatusDBs;
    }

    public void setJobStatusDBs(List<JobStatusDB> jobStatusDBs) {
        this.jobStatusDBs = jobStatusDBs;
    }

    public JobType getCurrentJob() {
        return currentJob;
    }

    public void setCurrentJob(JobType currentJob) {
        this.currentJob = currentJob;
    }

    public String getArmor() {
        return armor;
    }

    public void setArmor(String armor) {
        this.armor = armor;
    }

    public String getWeapon() {
        return weapon;
    }

    public void setWeapon(String weapon) {
        this.weapon = weapon;
    }

    public String getHeldItem() {
        return heldItem;
    }

    public void setHeldItem(String heldItem) {
        this.heldItem = heldItem;
    }

    public String getNecklace() {
        return necklace;
    }

    public void setNecklace(String necklace) {
        this.necklace = necklace;
    }

    public String getRing() {
        return ring;
    }

    public void setRing(String ring) {
        this.ring = ring;
    }

    public String getHelm() {
        return helm;
    }

    public void setHelm(String helm) {
        this.helm = helm;
    }

    public Integer getCurrentJP() {
        return currentJP;
    }

    public void setCurrentJP(Integer currentJP) {
        this.currentJP = currentJP;
    }

    public Integer getCumulativeJP() {
        return cumulativeJP;
    }

    public void setCumulativeJP(Integer cumulativeJP) {
        this.cumulativeJP = cumulativeJP;
    }

    public void equipItem(ItemType itemType, String itemId) {
        switch (itemType) {
            case WEAPON:
                setWeapon(itemId);
                break;
            case RING:
                setRing(itemId);
                break;
            case NECKLACE:
                setNecklace(itemId);
                break;
            case HELMET:
                setHelm(itemId);
                break;
            case ARMOR:
                setArmor(itemId);
                break;
            case HELD_ITEM:
                setHeldItem(itemId);
                break;
        }
    }

    public String unequipItem(ItemType itemType) {
        String itemId = null;
        switch (itemType) {
            case WEAPON:
                itemId = getWeapon();
                setWeapon(null);
                break;
            case RING:
                itemId = getRing();
                setRing(null);
                break;
            case NECKLACE:
                itemId = getNecklace();
                setNecklace(null);
                break;
            case HELMET:
                itemId = getHelm();
                setHelm(null);
                break;
            case ARMOR:
                itemId = getArmor();
                setArmor(null);
                break;
            case HELD_ITEM:
                itemId = getHeldItem();
                setHeldItem(null);
                break;
        }
        return itemId;
    }
}
