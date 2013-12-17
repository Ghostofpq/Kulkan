package com.ghostofpq.kulkan.entities.inventory.item;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.characteristics.SecondaryCharacteristics;
import com.ghostofpq.kulkan.entities.job.JobType;

import java.util.List;

public class LeftHand extends Item {
    public LeftHand(String itemID, String name, String description, PrimaryCharacteristics primaryCharacteristics, SecondaryCharacteristics secondaryCharacteristics, List<JobType> authorizedJobs) {
        super(itemID, name, description, primaryCharacteristics, secondaryCharacteristics, authorizedJobs, ItemType.NECKLACE);
    }
}