package com.ghostofpq.kulkan.entities.inventory.item;

import com.ghostofpq.kulkan.entities.characteristics.Characteristics;
import com.ghostofpq.kulkan.entities.job.JobType;

import java.util.List;

public class Necklace extends Item {
    public Necklace(String itemID, String name, String description, Characteristics characteristics, List<JobType> authorizedJobs, int price) {
        super(itemID, name, description, characteristics, authorizedJobs, ItemType.NECKLACE, price);
    }
}
