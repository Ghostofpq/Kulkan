package com.ghostofpq.kulkan.entities.job.capacity;

import com.ghostofpq.kulkan.entities.utils.Range;

import java.util.ArrayList;

public class Move extends Capacity {

    private static final long serialVersionUID = 768372563773451676L;
    private MoveRangeType moveRangeType;
    private MoveName moveName;
    private int manaCost;
    private Range range1;
    private Range range2;

    public Move(String name, String description, int price, MoveRangeType moveRangeType, MoveName moveName, int manaCost) {
        this.prerequisites = new ArrayList<Capacity>();
        this.sons = new ArrayList<Capacity>();
        this.name = name;
        this.description = description;
        this.moveRangeType = moveRangeType;
        this.moveName = moveName;
        this.type = CapacityType.MOVE;
        this.manaCost = manaCost;
        this.price = price;
        this.locked = true;
        range1 = null;
        range2 = null;
    }

    public MoveRangeType getMoveRangeType() {
        return moveRangeType;
    }

    public MoveName getMoveName() {
        return moveName;
    }

    public int getManaCost() {
        return manaCost;
    }

    public Range getRange1() {
        return range1;
    }

    public void setRange1(Range range1) {
        this.range1 = range1;
    }

    public Range getRange2() {
        return range2;
    }

    public void setRange2(Range range2) {
        this.range2 = range2;
    }
}
