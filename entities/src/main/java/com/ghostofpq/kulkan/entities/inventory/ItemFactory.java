package com.ghostofpq.kulkan.entities.inventory;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.characteristics.SecondaryCharacteristics;
import com.ghostofpq.kulkan.entities.inventory.item.*;
import com.ghostofpq.kulkan.entities.job.JobType;
import com.ghostofpq.kulkan.entities.utils.Range;
import com.ghostofpq.kulkan.entities.utils.RangeType;

import java.util.ArrayList;
import java.util.List;

public class ItemFactory {

    public static Item createItem(String id) throws IllegalArgumentException {
        if (id.equals("000")) {
            return createClothArmor();
        } else if (id.equals("001")) {
            return createIronHelm();
        } else if (id.equals("002")) {
            return createYewWand();
        } else if (id.equals("003")) {
            return createStoneClub();
        } else if (id.equals("004")) {
            return createSling();
        } else if (id.equals("005")) {
            return createLifeRing();
        } else if (id.equals("006")) {
            return createStrengthRing();
        } else if (id.equals("007")) {
            return createWillNecklace();
        } else if (id.equals("008")) {
            return createAgilityNecklace();
        } else if (id.equals("009")) {
            return createWoodenShield();
        } else if (id.equals("010")) {
            return createTwoHandedSword();
        } else {
            throw new IllegalArgumentException("Invalid itemId");
        }
    }

    private static Item createClothArmor() {
        // id
        String id = "000";
        // name
        String name = "Cloth armor";
        // description
        String description = "A really light armor. +5 armor.";
        // primaryCharacteristics
        PrimaryCharacteristics primaryCharacteristics = new PrimaryCharacteristics();
        // secondaryCharacteristics
        SecondaryCharacteristics secondaryCharacteristics = new SecondaryCharacteristics();
        secondaryCharacteristics.setArmor(5);
        // authorizedJobs
        List<JobType> authorizedJobs = new ArrayList<JobType>();
        authorizedJobs.add(JobType.WARRIOR);
        authorizedJobs.add(JobType.MAGE);
        // price
        int price = 10;
        // create
        return new Armor(id, name, description, primaryCharacteristics, secondaryCharacteristics, authorizedJobs, price);
    }

    private static Item createIronHelm() {
        // id
        String id = "001";
        // name
        String name = "Iron Helm";
        // description
        String description = "An iron helm. +10 armor.";
        // primaryCharacteristics
        PrimaryCharacteristics primaryCharacteristics = new PrimaryCharacteristics();
        // secondaryCharacteristics
        SecondaryCharacteristics secondaryCharacteristics = new SecondaryCharacteristics();
        secondaryCharacteristics.setArmor(5);
        // authorizedJobs
        List<JobType> authorizedJobs = new ArrayList<JobType>();
        authorizedJobs.add(JobType.WARRIOR);
        // price
        int price = 10;
        // create
        return new Helm(id, name, description, primaryCharacteristics, secondaryCharacteristics, authorizedJobs, price);
    }

    private static Item createYewWand() {
        // id
        String id = "002";
        // name
        String name = "Yew wand";
        // description
        String description = "A held yew wand. +10 Intelligence. +10 Magic damage.";
        // primaryCharacteristics
        PrimaryCharacteristics primaryCharacteristics = new PrimaryCharacteristics();
        primaryCharacteristics.setIntelligence(10);
        // secondaryCharacteristics
        SecondaryCharacteristics secondaryCharacteristics = new SecondaryCharacteristics();
        secondaryCharacteristics.setMagicalDamage(10);
        // authorizedJobs
        List<JobType> authorizedJobs = new ArrayList<JobType>();
        authorizedJobs.add(JobType.MAGE);
        // price
        int price = 10;
        // create
        return new HeldItem(id, name, description, primaryCharacteristics, secondaryCharacteristics, authorizedJobs, price);
    }

    private static Item createStoneClub() {
        // id
        String id = "003";
        // name
        String name = "Stone club";
        // description
        String description = "A stone club. +10 attack damage.";
        // primaryCharacteristics
        PrimaryCharacteristics primaryCharacteristics = new PrimaryCharacteristics();
        // secondaryCharacteristics
        SecondaryCharacteristics secondaryCharacteristics = new SecondaryCharacteristics();
        secondaryCharacteristics.setAttackDamage(10);
        // authorizedJobs
        List<JobType> authorizedJobs = new ArrayList<JobType>();
        authorizedJobs.add(JobType.WARRIOR);
        // range
        Range range = new Range(RangeType.CROSS, 0, 1);
        // price
        int price = 10;
        // create
        return new Weapon(id, name, description, primaryCharacteristics, secondaryCharacteristics, authorizedJobs, price, range, WeaponType.ONE_HANDED);
    }

    private static Item createSling() {
        // id
        String id = "004";
        // name
        String name = "Sling";
        // description
        String description = "A simple sling. +2 attack damage.";
        // primaryCharacteristics
        PrimaryCharacteristics primaryCharacteristics = new PrimaryCharacteristics();
        // secondaryCharacteristics
        SecondaryCharacteristics secondaryCharacteristics = new SecondaryCharacteristics();
        secondaryCharacteristics.setAttackDamage(10);
        // authorizedJobs
        List<JobType> authorizedJobs = new ArrayList<JobType>();
        authorizedJobs.add(JobType.MAGE);
        // range
        Range range = new Range(RangeType.CIRCLE, 2, 5);
        // price
        int price = 10;
        // create
        return new Weapon(id, name, description, primaryCharacteristics, secondaryCharacteristics, authorizedJobs, price, range, WeaponType.RANGE);
    }

    private static Item createLifeRing() {
        // id
        String id = "005";
        // name
        String name = "Life Ring";
        // description
        String description = "A ring empowered with life. +10 Endurance. ";
        // primaryCharacteristics
        PrimaryCharacteristics primaryCharacteristics = new PrimaryCharacteristics();
        primaryCharacteristics.setEndurance(10);
        // secondaryCharacteristics
        SecondaryCharacteristics secondaryCharacteristics = new SecondaryCharacteristics();
        // authorizedJobs
        List<JobType> authorizedJobs = new ArrayList<JobType>();
        authorizedJobs.add(JobType.WARRIOR);
        authorizedJobs.add(JobType.MAGE);
        // price
        int price = 10;
        // create
        return new Ring(id, name, description, primaryCharacteristics, secondaryCharacteristics, authorizedJobs, price);
    }

    private static Item createStrengthRing() {
        // id
        String id = "006";
        // name
        String name = "Strength Ring";
        // description
        String description = "A ring empowered with pure strength. +10 Strength. ";
        // primaryCharacteristics
        PrimaryCharacteristics primaryCharacteristics = new PrimaryCharacteristics();
        primaryCharacteristics.setStrength(10);
        // secondaryCharacteristics
        SecondaryCharacteristics secondaryCharacteristics = new SecondaryCharacteristics();
        // authorizedJobs
        List<JobType> authorizedJobs = new ArrayList<JobType>();
        authorizedJobs.add(JobType.WARRIOR);
        authorizedJobs.add(JobType.MAGE);
        // price
        int price = 10;
        // create
        return new Ring(id, name, description, primaryCharacteristics, secondaryCharacteristics, authorizedJobs, price);
    }

    private static Item createWillNecklace() {
        // id
        String id = "007";
        // name
        String name = "Will Necklace";
        // description
        String description = "A necklace empowered with will. +10 Will. ";
        // primaryCharacteristics
        PrimaryCharacteristics primaryCharacteristics = new PrimaryCharacteristics();
        primaryCharacteristics.setWill(10);
        // secondaryCharacteristics
        SecondaryCharacteristics secondaryCharacteristics = new SecondaryCharacteristics();
        // authorizedJobs
        List<JobType> authorizedJobs = new ArrayList<JobType>();
        authorizedJobs.add(JobType.WARRIOR);
        authorizedJobs.add(JobType.MAGE);
        // price
        int price = 10;
        // create
        return new Necklace(id, name, description, primaryCharacteristics, secondaryCharacteristics, authorizedJobs, price);
    }

    private static Item createAgilityNecklace() {
        // id
        String id = "008";
        // name
        String name = "Agility Necklace";
        // description
        String description = "A necklace empowered with pure agility. +10 Agility. ";
        // primaryCharacteristics
        PrimaryCharacteristics primaryCharacteristics = new PrimaryCharacteristics();
        primaryCharacteristics.setAgility(10);
        // secondaryCharacteristics
        SecondaryCharacteristics secondaryCharacteristics = new SecondaryCharacteristics();
        // authorizedJobs
        List<JobType> authorizedJobs = new ArrayList<JobType>();
        authorizedJobs.add(JobType.WARRIOR);
        authorizedJobs.add(JobType.MAGE);
        // price
        int price = 10;
        // create
        return new Necklace(id, name, description, primaryCharacteristics, secondaryCharacteristics, authorizedJobs, price);
    }

    private static Item createWoodenShield() {
        // id
        String id = "009";
        // name
        String name = "Wooden Shield";
        // description
        String description = "A wooden shield. +10 armor. ";
        // primaryCharacteristics
        PrimaryCharacteristics primaryCharacteristics = new PrimaryCharacteristics();
        // secondaryCharacteristics
        SecondaryCharacteristics secondaryCharacteristics = new SecondaryCharacteristics();
        secondaryCharacteristics.setArmor(10);
        // authorizedJobs
        List<JobType> authorizedJobs = new ArrayList<JobType>();
        authorizedJobs.add(JobType.WARRIOR);
        authorizedJobs.add(JobType.MAGE);
        // price
        int price = 10;
        // create
        return new HeldItem(id, name, description, primaryCharacteristics, secondaryCharacteristics, authorizedJobs, price);
    }

    private static Item createTwoHandedSword() {
        // id
        String id = "010";
        // name
        String name = "Two Handed Sword";
        // description
        String description = "A two handed sword. +20 attack damage.";
        // primaryCharacteristics
        PrimaryCharacteristics primaryCharacteristics = new PrimaryCharacteristics();
        // secondaryCharacteristics
        SecondaryCharacteristics secondaryCharacteristics = new SecondaryCharacteristics();
        secondaryCharacteristics.setAttackDamage(20);
        // authorizedJobs
        List<JobType> authorizedJobs = new ArrayList<JobType>();
        authorizedJobs.add(JobType.WARRIOR);
        // range
        Range range = new Range(RangeType.CROSS, 0, 2);
        // price
        int price = 100;
        // create
        return new Weapon(id, name, description, primaryCharacteristics, secondaryCharacteristics, authorizedJobs, price, range, WeaponType.TWO_HANDED);
    }
}
