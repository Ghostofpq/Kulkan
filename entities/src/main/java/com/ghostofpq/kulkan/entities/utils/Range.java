package com.ghostofpq.kulkan.entities.utils;

public class Range {

    private RangeType rangeType;
    private int minRange;
    private int maxRange;

    public Range(RangeType rangeType, int minRange, int maxRange) {
        this.rangeType = rangeType;
        this.minRange = minRange;
        this.maxRange = maxRange;
    }
}
