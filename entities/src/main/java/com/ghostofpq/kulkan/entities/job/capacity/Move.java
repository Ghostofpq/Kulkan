package com.ghostofpq.kulkan.entities.job.capacity;

import java.util.ArrayList;

public class Move extends Capacity {

    private static final long serialVersionUID = 768372563773451676L;

    public Move(String name, String description, int price) {
        this.prerequisites = new ArrayList<Capacity>();

        this.name = name;
        this.description = description;

        this.type = CapacityType.MOVE;

        this.price = price;
        this.locked = true;
    }
}
