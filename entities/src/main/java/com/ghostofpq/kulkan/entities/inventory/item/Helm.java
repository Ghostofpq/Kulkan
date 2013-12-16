package com.ghostofpq.kulkan.entities.inventory.item;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.characteristics.SecondaryCharacteristics;
import com.ghostofpq.kulkan.entities.job.JobType;

import java.util.List;

public class Helm extends Item {
    public Helm(int itemID, String name, String description, PrimaryCharacteristics primaryCaracteristics, SecondaryCharacteristics secondaryCaracteristics, List<JobType> authorizedJobs) {
        super(itemID, name, description, primaryCaracteristics, secondaryCaracteristics, authorizedJobs, ItemType.HELMET);
    }
}
