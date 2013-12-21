package com.ghostofpq.kulkan.entities.inventory.item;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.characteristics.SecondaryCharacteristics;
import com.ghostofpq.kulkan.entities.job.JobType;
import com.ghostofpq.kulkan.entities.utils.Range;

import java.util.List;

public class Weapon extends Item {

    private Range range;
    private WeaponType weaponType;

    public Weapon(String itemID, String name, String description, PrimaryCharacteristics primaryCharacteristics, SecondaryCharacteristics secondaryCharacteristics, List<JobType> authorizedJobs, int price, Range range, WeaponType weaponType) {
        super(itemID, name, description, primaryCharacteristics, secondaryCharacteristics, authorizedJobs, ItemType.WEAPON, price);
        this.range = range;
        this.weaponType = weaponType;
    }

    public Range getRange() {
        return range;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }
}
