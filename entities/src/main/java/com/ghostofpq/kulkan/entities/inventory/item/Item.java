package com.ghostofpq.kulkan.entities.inventory.item;

import com.ghostofpq.kulkan.entities.characteristics.Characteristics;
import com.ghostofpq.kulkan.entities.job.JobType;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public abstract class Item implements Serializable {
    private String itemID;
    private String name;
    private String description;
    private Characteristics characteristics;
    private List<JobType> authorizedJobs;
    private ItemType itemType;
    private int price;

    public static Comparator<Item> nameComparatorAlpha = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
            return res;
        }
    };

    public static Comparator<Item> nameComparatorAntiAlpha = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            int res = -String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
            return res;
        }
    };

    public static Comparator<Item> typeComparatorAlpha = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(o1.getItemType().toString(), o2.getItemType().toString());
            if (res == 0) {
                res = o1.getName().compareTo(o2.getName());
            }
            return res;
        }
    };

    public static Comparator<Item> typeComparatorAntiAlpha = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            int res = -String.CASE_INSENSITIVE_ORDER.compare(o1.getItemType().toString(), o2.getItemType().toString());
            if (res == 0) {
                res = o1.getName().compareTo(o2.getName());
            }
            return res;
        }
    };

    public static Comparator<Item> priceComparatorIncr = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            int res = Integer.compare(o1.getPrice(), o2.getPrice());
            if (res == 0) {
                res = o1.getName().compareTo(o2.getName());
            }
            return res;
        }
    };

    public static Comparator<Item> priceComparatorDesc = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            int res = -Integer.compare(o1.getPrice(), o2.getPrice());
            if (res == 0) {
                res = o1.getName().compareTo(o2.getName());
            }
            return res;
        }
    };


    protected Item(String itemID, String name, String description, Characteristics characteristics, List<JobType> authorizedJobs, ItemType itemType, int price) {
        this.itemID = itemID;
        this.name = name;
        this.description = description;
        this.characteristics = characteristics;
        this.authorizedJobs = authorizedJobs;
        this.itemType = itemType;
        this.price = price;
    }

    public int getPrice() {
        return price;
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

    public Characteristics getCharacteristics() {
        return characteristics;
    }

    public List<JobType> getAuthorizedJobs() {
        return authorizedJobs;
    }

    public ItemType getItemType() {
        return itemType;
    }
}
