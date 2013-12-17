package com.ghostofpq.kulkan.entities.inventory.item;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.characteristics.SecondaryCharacteristics;
import com.ghostofpq.kulkan.entities.job.JobType;

import java.util.List;

public abstract class Item {
    private String itemID;
    private String name;
    private String description;
    private PrimaryCharacteristics primaryCharacteristics;
    private SecondaryCharacteristics secondaryCharacteristics;
    private List<JobType> authorizedJobs;
    private ItemType itemType;

    protected Item(String itemID, String name, String description, PrimaryCharacteristics primaryCharacteristics, SecondaryCharacteristics secondaryCharacteristics, List<JobType> authorizedJobs, ItemType itemType) {
        this.itemID = itemID;
        this.name = name;
        this.description = description;
        this.primaryCharacteristics = primaryCharacteristics;
        this.secondaryCharacteristics = secondaryCharacteristics;
        this.authorizedJobs = authorizedJobs;
        this.itemType = itemType;
    }

    public String getItemID() {
        return itemID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public PrimaryCharacteristics getPrimaryCharacteristics() {
        return primaryCharacteristics;
    }

    public SecondaryCharacteristics getSecondaryCharacteristics() {
        return secondaryCharacteristics;
    }

    public List<JobType> getAuthorizedJobs() {
        return authorizedJobs;
    }

    public ItemType getItemType() {
        return itemType;
    }
}
