package com.ghostofpq.kulkan.client.utils;


public class Resolution implements Comparable<Resolution> {
    private int width;
    private int height;
    private int offsetX;
    private int offsetY;
    private ResolutionRatio resolutionRatio;

    public Resolution(int width, int height, int offsetX, int offsetY, ResolutionRatio resolutionRatio) {
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.resolutionRatio = resolutionRatio;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public ResolutionRatio getResolutionRatio() {
        return resolutionRatio;
    }

    public void setResolutionRatio(ResolutionRatio resolutionRatio) {
        this.resolutionRatio = resolutionRatio;
    }

    @Override
    public int compareTo(Resolution o) {
        int nbPixels = this.getHeight() * this.getWidth();
        int otherNbPixels = o.getHeight() * o.getWidth();
        return Integer.compare(nbPixels, otherNbPixels);
    }
}
