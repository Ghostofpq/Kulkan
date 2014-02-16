package com.ghostofpq.kulkan.entities.job.capacity;

import com.ghostofpq.kulkan.entities.utils.Range;

import java.util.ArrayList;

public class Move extends Capacity {

    private static final long serialVersionUID = 768372563773451676L;
    private MoveRangeType moveRangeType;
    private MoveName moveName;
    private int manaCost;
    private Range range;
    private Range areaOfEffect;

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
        range = null;
        areaOfEffect = null;
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

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public Range getAreaOfEffect() {
        return areaOfEffect;
    }

    public void setAreaOfEffect(Range areaOfEffect) {
        this.areaOfEffect = areaOfEffect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Move move = (Move) o;

        if (moveName != move.moveName) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (moveName != null ? moveName.hashCode() : 0);
        return result;
    }
}
