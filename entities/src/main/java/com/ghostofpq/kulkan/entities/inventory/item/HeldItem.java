package com.ghostofpq.kulkan.entities.inventory.item;

import com.ghostofpq.kulkan.entities.characteristics.Characteristics;
import com.ghostofpq.kulkan.entities.job.JobType;

import java.util.List;

public class HeldItem extends Item {
    public HeldItem(String itemID, String name, String description, Characteristics characteristics, List<JobType> authorizedJobs, int price) {
        super(itemID, name, description, characteristics, authorizedJobs, ItemType.HELD_ITEM, price);
    }
}
