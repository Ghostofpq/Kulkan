package com.ghostofpq.kulkan.client.graphics.ingame;

import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.commons.PositionAbsolute;

public abstract class DrawableObject implements Comparable<DrawableObject> {

    protected Position position;
    protected PositionAbsolute positionAbsolute;
    protected PositionAbsolute positionToCompare;
    protected float height;
    protected boolean isMoving;

    public abstract void draw();

    public abstract void update(long deltaTime);

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    @Override
    public int compareTo(DrawableObject other) {
        return this.getPosition().compareTo(other.getPosition());
    }

    public abstract String toString();

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public PositionAbsolute getPositionAbsolute() {
        return positionAbsolute;
    }

    public void setPositionAbsolute(PositionAbsolute positionAbsolute) {
        this.positionAbsolute = positionAbsolute;
    }

    public abstract PositionAbsolute getPositionToCompare(PointOfView pointOfView);

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
