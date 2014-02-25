package com.ghostofpq.kulkan.server.database.model;

import com.ghostofpq.kulkan.entities.characteristics.Characteristics;
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
    private Map<Characteristics.fields, Integer> bonusCharacteristics;
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
        this.bonusCharacteristics = new HashMap<Characteristics.fields, Integer>();

        if (item.getCharacteristics().getAttackDamage() != 0) {
            bonusCharacteristics.put(Characteristics.fields.ATTACK_DAMAGE, item.getCharacteristics().getAttackDamage());
        }
        if (item.getCharacteristics().getMagicalDamage() != 0) {
            bonusCharacteristics.put(Characteristics.fields.MAGICAL_DAMAGE, item.getCharacteristics().getMagicalDamage());
        }
        if (item.getCharacteristics().getArmor() != 0) {
            bonusCharacteristics.put(Characteristics.fields.ARMOR, item.getCharacteristics().getArmor());
        }
        if (item.getCharacteristics().getMagicResist() != 0) {
            bonusCharacteristics.put(Characteristics.fields.MAGIC_RESIST, item.getCharacteristics().getMagicResist());
        }
        if (item.getCharacteristics().getArmorPenetration() != 0) {
            bonusCharacteristics.put(Characteristics.fields.ARMOR_PENETRATION, item.getCharacteristics().getArmorPenetration());
        }
        if (item.getCharacteristics().getMagicPenetration() != 0) {
            bonusCharacteristics.put(Characteristics.fields.MAGIC_PENETRATION, item.getCharacteristics().getMagicPenetration());
        }
        if (item.getCharacteristics().getMovement() != 0) {
            bonusCharacteristics.put(Characteristics.fields.MOVEMENT, item.getCharacteristics().getMovement());
        }
        if (item.getCharacteristics().getMovement() != 0) {
            bonusCharacteristics.put(Characteristics.fields.SPEED, item.getCharacteristics().getSpeed());
        }
        if (item.getCharacteristics().getMovement() != 0) {
            bonusCharacteristics.put(Characteristics.fields.MAX_HEALTH, item.getCharacteristics().getMaxHealthPoint());
        }
        if (item.getCharacteristics().getMovement() != 0) {
            bonusCharacteristics.put(Characteristics.fields.MAX_MANA, item.getCharacteristics().getMaxManaPoint());
        }
        if (item.getCharacteristics().getHealthRegeneration() != 0) {
            bonusCharacteristics.put(Characteristics.fields.LIFE_REGENERATION, item.getCharacteristics().getHealthRegeneration());
        }
        if (item.getCharacteristics().getManaRegeneration() != 0) {
            bonusCharacteristics.put(Characteristics.fields.MANA_REGENERATION, item.getCharacteristics().getManaRegeneration());
        }
        if (item.getCharacteristics().getCriticalStrike() != 0) {
            bonusCharacteristics.put(Characteristics.fields.CRITICAL_STRIKE, item.getCharacteristics().getCriticalStrike());
        }
        if (item.getCharacteristics().getResilience() != 0) {
            bonusCharacteristics.put(Characteristics.fields.RESILIENCE, item.getCharacteristics().getResilience());
        }
        if (item.getCharacteristics().getPrecision() != 0) {
            bonusCharacteristics.put(Characteristics.fields.PRECISION, item.getCharacteristics().getPrecision());
        }
        if (item.getCharacteristics().getEscape() != 0) {
            bonusCharacteristics.put(Characteristics.fields.ESCAPE, item.getCharacteristics().getEscape());
        }

        if (itemType.equals(ItemType.WEAPON)) {
            this.range = ((Weapon) item).getRange();
            this.weaponType = ((Weapon) item).getWeaponType();
        } else {
            this.range = null;
            this.weaponType = null;
        }
    }

    public ItemDB(String id, String name, String description, ItemType itemType, List<JobType> authorizedJobs, int price, Map<Characteristics.fields, Integer> bonusCharacteristics, Range range, WeaponType weaponType) {
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

        Characteristics characteristics = new Characteristics();
        for (Characteristics.fields key : bonusCharacteristics.keySet()) {
            switch (key) {
                case ATTACK_DAMAGE:
                    characteristics.setAttackDamage(bonusCharacteristics.get(key));
                    break;
                case MAGICAL_DAMAGE:
                    characteristics.setMagicalDamage(bonusCharacteristics.get(key));
                    break;
                case ARMOR:
                    characteristics.setArmor(bonusCharacteristics.get(key));
                    break;
                case MAGIC_RESIST:
                    characteristics.setMagicResist(bonusCharacteristics.get(key));
                    break;
                case ARMOR_PENETRATION:
                    characteristics.setArmorPenetration(bonusCharacteristics.get(key));
                    break;
                case MAGIC_PENETRATION:
                    characteristics.setMagicPenetration(bonusCharacteristics.get(key));
                    break;
                case MOVEMENT:
                    characteristics.setMovement(bonusCharacteristics.get(key));
                    break;
                case SPEED:
                    characteristics.setSpeed(bonusCharacteristics.get(key));
                    break;
                case MAX_HEALTH:
                    characteristics.setMaxHealthPoint(bonusCharacteristics.get(key));
                    break;
                case MAX_MANA:
                    characteristics.setMaxManaPoint(bonusCharacteristics.get(key));
                    break;
                case LIFE_REGENERATION:
                    characteristics.setHealthRegeneration(bonusCharacteristics.get(key));
                    break;
                case MANA_REGENERATION:
                    characteristics.setManaRegeneration(bonusCharacteristics.get(key));
                    break;
                case ESCAPE:
                    characteristics.setEscape(bonusCharacteristics.get(key));
                    break;
                case CRITICAL_STRIKE:
                    characteristics.setCriticalStrike(bonusCharacteristics.get(key));
                    break;
                case PRECISION:
                    characteristics.setPrecision(bonusCharacteristics.get(key));
                    break;
                case RESILIENCE:
                    characteristics.setResilience(bonusCharacteristics.get(key));
                    break;
            }
        }
        switch (itemType) {
            case HELD_ITEM:
                result = new HeldItem(id.toString(), name, description, characteristics, authorizedJobs, price);
                break;
            case ARMOR:
                result = new Armor(id.toString(), name, description, characteristics, authorizedJobs, price);
                break;
            case HELMET:
                result = new Helm(id.toString(), name, description, characteristics, authorizedJobs, price);
                break;
            case NECKLACE:
                result = new Necklace(id.toString(), name, description, characteristics, authorizedJobs, price);
                break;
            case RING:
                result = new Ring(id.toString(), name, description, characteristics, authorizedJobs, price);
                break;
            case WEAPON:
                result = new Weapon(id.toString(), name, description, characteristics, authorizedJobs, price, range, weaponType);
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

    public Map<Characteristics.fields, Integer> getBonusCharacteristics() {
        return bonusCharacteristics;
    }

    public void setBonusCharacteristics(Map<Characteristics.fields, Integer> bonusCharacteristics) {
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

}
