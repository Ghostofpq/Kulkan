package com.ghostofpq.kulkan.entities.inventory.item;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.characteristics.SecondaryCharacteristics;
import com.ghostofpq.kulkan.entities.job.JobType;
import com.ghostofpq.kulkan.entities.utils.Range;

import java.util.List;

public class Weapon extends Item {

    private Range range;

    public Weapon(String itemID, String name, String description, PrimaryCharacteristics primaryCharacteristics, SecondaryCharacteristics secondaryCharacteristics, List<JobType> authorizedJobs, ItemType itemType, Range range) {
        super(itemID, name, description, primaryCharacteristics, secondaryCharacteristics, authorizedJobs, itemType);
        this.range = range;
    }
}
