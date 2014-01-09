package com.ghostofpq.kulkan.entities.utils;

import java.io.Serializable;

public class Range implements Serializable {

    private RangeType rangeType;
    private int minRange;
    private int maxRange;

    public Range(RangeType rangeType, int minRange, int maxRange) {
        this.rangeType = rangeType;
        this.minRange = minRange;
        this.maxRange = maxRange;
    }

    public RangeType getRangeType() {
        return rangeType;
    }

    public int getMinRange() {
        return minRange;
    }

    public int getMaxRange() {
        return maxRange;
    }
}
