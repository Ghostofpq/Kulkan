package com.ghostofpq.kulkan.server.database.model;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.characteristics.SecondaryCharacteristics;
import com.ghostofpq.kulkan.entities.inventory.item.*;
import com.ghostofpq.kulkan.entities.job.JobType;
import com.ghostofpq.kulkan.entities.utils.Range;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document
public class ItemDB {
    @Id
    private String id;
    @Indexed
    private ItemType itemType;
    @Indexed
    private String name;
    private String description;
    private Map<CharacteristicName, Integer> bonusCharacteristics;
    private List<JobType> authorizedJobs;
    private int price;
    private Range range;
    private WeaponType weaponType;

    public ItemDB() {
    }

    public ItemDB(Item item) {
        this.id = item.getItemID();
        this.name = item.getName();
        this.description = item.getDescription();
        this.authorizedJobs = item.getAuthorizedJobs();
        this.itemType = item.getItemType();
        this.price = item.getPrice();
        this.bonusCharacteristics = new HashMap<CharacteristicName, Integer>();

        if (item.getPrimaryCharacteristics().getStrength() != 0) {
            bonusCharacteristics.put(CharacteristicName.STRENGTH, item.getPrimaryCharacteristics().getStrength());
        }
        if (item.getPrimaryCharacteristics().getEndurance() != 0) {
            bonusCharacteristics.put(CharacteristicName.ENDURANCE, item.getPrimaryCharacteristics().getEndurance());
        }
        if (item.getPrimaryCharacteristics().getIntelligence() != 0) {
            bonusCharacteristics.put(CharacteristicName.INTELLIGENCE, item.getPrimaryCharacteristics().getIntelligence());
        }
        if (item.getPrimaryCharacteristics().getWill() != 0) {
            bonusCharacteristics.put(CharacteristicName.WILL, item.getPrimaryCharacteristics().getWill());
        }
        if (item.getPrimaryCharacteristics().getAgility() != 0) {
            bonusCharacteristics.put(CharacteristicName.AGILITY, item.getPrimaryCharacteristics().getAgility());
        }
        if (item.getPrimaryCharacteristics().getMovement() != 0) {
            bonusCharacteristics.put(CharacteristicName.MOVEMENT, item.getPrimaryCharacteristics().getMovement());
        }

        if (item.getSecondaryCharacteristics().getAttackDamage() != 0) {
            bonusCharacteristics.put(CharacteristicName.ATTACK_DAMAGE, item.getSecondaryCharacteristics().getAttackDamage());
        }
        if (item.getSecondaryCharacteristics().getMagicalDamage() != 0) {
            bonusCharacteristics.put(CharacteristicName.MAGICAL_DAMAGE, item.getSecondaryCharacteristics().getMagicalDamage());
        }
        if (item.getSecondaryCharacteristics().getArmor() != 0) {
            bonusCharacteristics.put(CharacteristicName.ARMOR, item.getSecondaryCharacteristics().getArmor());
        }
        if (item.getSecondaryCharacteristics().getMagicResist() != 0) {
            bonusCharacteristics.put(CharacteristicName.MAGIC_RESIST, item.getSecondaryCharacteristics().getMagicResist());
        }
        if (item.getSecondaryCharacteristics().getArmorPenetration() != 0) {
            bonusCharacteristics.put(CharacteristicName.ARMOR_PENETRATION, item.getSecondaryCharacteristics().getArmorPenetration());
        }
        if (item.getSecondaryCharacteristics().getMagicPenetration() != 0) {
            bonusCharacteristics.put(CharacteristicName.MAGIC_PENETRATION, item.getSecondaryCharacteristics().getMagicPenetration());
        }
        if (item.getSecondaryCharacteristics().getLifeRegeneration() != 0) {
            bonusCharacteristics.put(CharacteristicName.LIFE_REGENERATION, item.getSecondaryCharacteristics().getLifeRegeneration());
        }
        if (item.getSecondaryCharacteristics().getManaRegeneration() != 0) {
            bonusCharacteristics.put(CharacteristicName.MANA_REGENERATION, item.getSecondaryCharacteristics().getManaRegeneration());
        }
        if (item.getSecondaryCharacteristics().getCriticalStrike() != 0) {
            bonusCharacteristics.put(CharacteristicName.CRITICAL_STRIKE, item.getSecondaryCharacteristics().getCriticalStrike());
        }
        if (item.getSecondaryCharacteristics().getResilience() != 0) {
            bonusCharacteristics.put(CharacteristicName.RESILIENCE, item.getSecondaryCharacteristics().getResilience());
        }
        if (item.getSecondaryCharacteristics().getPrecision() != 0) {
            bonusCharacteristics.put(CharacteristicName.PRECISION, item.getSecondaryCharacteristics().getPrecision());
        }
        if (item.getSecondaryCharacteristics().getEscape() != 0) {
            bonusCharacteristics.put(CharacteristicName.ESCAPE, item.getSecondaryCharacteristics().getEscape());
        }

        if (itemType.equals(ItemType.WEAPON)) {
            this.range = ((Weapon) item).getRange();
            this.weaponType = ((Weapon) item).getWeaponType();
        } else {
            this.range = null;
            this.weaponType = null;
        }
    }

    public ItemDB(String id, String name, String description, ItemType itemType, List<JobType> authorizedJobs, int price, Map<CharacteristicName, Integer> bonusCharacteristics, Range range, WeaponType weaponType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.authorizedJobs = authorizedJobs;
        this.itemType = itemType;
        this.price = price;
        this.bonusCharacteristics = bonusCharacteristics;
        this.range = range;
        this.weaponType = weaponType;
    }

    public Item toItem() {
        Item result = null;

        PrimaryCharacteristics primaryCharacteristics = new PrimaryCharacteristics();
        SecondaryCharacteristics secondaryCharacteristics = new SecondaryCharacteristics();
        for (CharacteristicName key : bonusCharacteristics.keySet()) {
            switch (key) {
                case STRENGTH:
                    primaryCharacteristics.setStrength(bonusCharacteristics.get(key));
                    break;
                case ENDURANCE:
                    primaryCharacteristics.setEndurance(bonusCharacteristics.get(key));
                    break;
                case INTELLIGENCE:
                    primaryCharacteristics.setIntelligence(bonusCharacteristics.get(key));
                    break;
                case WILL:
                    primaryCharacteristics.setWill(bonusCharacteristics.get(key));
                    break;
                case AGILITY:
                    primaryCharacteristics.setAgility(bonusCharacteristics.get(key));
                    break;
                case MOVEMENT:
                    primaryCharacteristics.setMovement(bonusCharacteristics.get(key));
                    break;
                case ATTACK_DAMAGE:
                    secondaryCharacteristics.setAttackDamage(bonusCharacteristics.get(key));
                    break;
                case MAGICAL_DAMAGE:
                    secondaryCharacteristics.setMagicalDamage(bonusCharacteristics.get(key));
                    break;
                case ARMOR:
                    secondaryCharacteristics.setArmor(bonusCharacteristics.get(key));
                    break;
                case MAGIC_RESIST:
                    secondaryCharacteristics.setMagicResist(bonusCharacteristics.get(key));
                    break;
                case ARMOR_PENETRATION:
                    secondaryCharacteristics.setArmorPenetration(bonusCharacteristics.get(key));
                    break;
                case MAGIC_PENETRATION:
                    secondaryCharacteristics.setMagicPenetration(bonusCharacteristics.get(key));
                    break;
                case SPEED:
                    secondaryCharacteristics.setSpeed(bonusCharacteristics.get(key));
                    break;
                case LIFE_REGENERATION:
                    secondaryCharacteristics.setLifeRegeneration(bonusCharacteristics.get(key));
                    break;
                case MANA_REGENERATION:
                    secondaryCharacteristics.setManaRegeneration(bonusCharacteristics.get(key));
                    break;
                case ESCAPE:
                    secondaryCharacteristics.setEscape(bonusCharacteristics.get(key));
                    break;
                case CRITICAL_STRIKE:
                    secondaryCharacteristics.setCriticalStrike(bonusCharacteristics.get(key));
                    break;
                case PRECISION:
                    secondaryCharacteristics.setPrecision(bonusCharacteristics.get(key));
                    break;
                case RESILIENCE:
                    secondaryCharacteristics.setResilience(bonusCharacteristics.get(key));
                    break;
            }
        }
        switch (itemType) {
            case HELD_ITEM:
                result = new HeldItem(id.toString(), name, description, primaryCharacteristics, secondaryCharacteristics, authorizedJobs, price);
                break;
            case ARMOR:
                result = new Armor(id.toString(), name, description, primaryCharacteristics, secondaryCharacteristics, authorizedJobs, price);
                break;
            case HELMET:
                result = new Helm(id.toString(), name, description, primaryCharacteristics, secondaryCharacteristics, authorizedJobs, price);
                break;
            case NECKLACE:
                result = new Necklace(id.toString(), name, description, primaryCharacteristics, secondaryCharacteristics, authorizedJobs, price);
                break;
            case RING:
                result = new Ring(id.toString(), name, description, primaryCharacteristics, secondaryCharacteristics, authorizedJobs, price);
                break;
            case WEAPON:
                result = new Weapon(id.toString(), name, description, primaryCharacteristics, secondaryCharacteristics, authorizedJobs, price, range, weaponType);
                break;
        }
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<CharacteristicName, Integer> getBonusCharacteristics() {
        return bonusCharacteristics;
    }

    public void setBonusCharacteristics(Map<CharacteristicName, Integer> bonusCharacteristics) {
        this.bonusCharacteristics = bonusCharacteristics;
    }

    public List<JobType> getAuthorizedJobs() {
        return authorizedJobs;
    }

    public void setAuthorizedJobs(List<JobType> authorizedJobs) {
        this.authorizedJobs = authorizedJobs;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public void setWeaponType(WeaponType weaponType) {
        this.weaponType = weaponType;
    }

    public enum CharacteristicName {
        STRENGTH,
        ENDURANCE,
        INTELLIGENCE,
        WILL,
        AGILITY,
        MOVEMENT,

        ATTACK_DAMAGE,
        MAGICAL_DAMAGE,
        ARMOR,
        MAGIC_RESIST,
        ARMOR_PENETRATION,
        MAGIC_PENETRATION,
        SPEED,
        LIFE_REGENERATION,
        MANA_REGENERATION,
        ESCAPE,
        CRITICAL_STRIKE,
        PRECISION,
        RESILIENCE
    }
}
