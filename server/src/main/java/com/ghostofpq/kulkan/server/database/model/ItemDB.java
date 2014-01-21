package com.ghostofpq.kulkan.server.database.model;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.characteristics.SecondaryCharacteristics;
import com.ghostofpq.kulkan.entities.inventory.item.*;
import com.ghostofpq.kulkan.entities.job.JobType;
import com.ghostofpq.kulkan.entities.utils.Range;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document
@Slf4j
public class ItemDB {
    @Id
    private ObjectId id;
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

    public ItemDB(String name, String description, ItemType itemType, List<JobType> authorizedJobs, int price, Map<CharacteristicName, Integer> bonusCharacteristics, Range range, WeaponType weaponType) {
        this.id = new ObjectId();
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
