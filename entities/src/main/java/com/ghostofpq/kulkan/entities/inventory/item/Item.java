package com.ghostofpq.kulkan.entities.inventory.item;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.characteristics.SecondaryCharacteristics;
import com.ghostofpq.kulkan.entities.job.JobType;

import java.util.List;

public abstract class Item {
    private int itemID;
    private String name;
    private String description;
    private PrimaryCharacteristics primaryCaracteristics;
    private SecondaryCharacteristics secondaryCaracteristics;
    private List<JobType> authorizedJobs;
    private ItemType itemType;

    protected Item(int itemID, String name, String description, PrimaryCharacteristics primaryCaracteristics, SecondaryCharacteristics secondaryCaracteristics, List<JobType> authorizedJobs, ItemType itemType) {
        this.itemID = itemID;
        this.name = name;
        this.description = description;
        this.primaryCaracteristics = primaryCaracteristics;
        this.secondaryCaracteristics = secondaryCaracteristics;
        this.authorizedJobs = authorizedJobs;
        this.itemType = itemType;
    }
}
