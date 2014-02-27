package com.ghostofpq.kulkan.commons;

import java.io.Serializable;

public class Position implements Serializable, Comparable<Position> {
    private static final long serialVersionUID = 8427348252935902307L;
    private int x;  // Length
    private int y;  // Height
    private int z;  // Depth

    public Position(int x, int y, int z) {
        this.setX(x);
        this.setY(y);
        this.setZ(z);
    }

    public Position(Position position) {
        this.setX(position.getX());
        this.setY(position.getY());
        this.setZ(position.getZ());
    }

    public PointOfView getHeadingAngleFor(Position targetedPosition) {
        PointOfView result = null;

        if (Math.abs(targetedPosition.getX() - getX()) >= Math.abs(targetedPosition.getZ() - getZ())) {
            if (targetedPosition.getX() > getX()) {
                result = PointOfView.EAST;
            } else if (targetedPosition.getX() == getX()) {
                if (targetedPosition.getZ() > getZ()) {
                    result = PointOfView.SOUTH;
                } else if (targetedPosition.getZ() < getZ()) {
                    result = PointOfView.NORTH;
                }
            } else {
                result = PointOfView.WEST;
            }
        } else {
            if (targetedPosition.getZ() > getZ()) {
                result = PointOfView.SOUTH;
            } else if (targetedPosition.getZ() == getZ()) {
                if (targetedPosition.getX() > getX()) {
                    result = PointOfView.EAST;
                } else if (targetedPosition.getX() < getX()) {
                    result = PointOfView.WEST;
                }
            } else {
                result = PointOfView.NORTH;
            }
        }
        return result;
    }

    public PositionAbsolute toAbsolute() {
        return new PositionAbsolute((float) x, (float) y, (float) z);
    }

    @Override
    public int compareTo(Position other) {
        // Priority on Y axis
        if (this.getY() < other.getY()) {
            return -1;
        } else if (this.getY() > other.getY()) {
            return 1;
        } else {
            // Then X axis
            if (this.getX() < other.getX()) {
                return -1;
            } else if (this.getX() > other.getX()) {
                return 1;
            } else {
                // Finally Z axis
                if (this.getZ() < other.getZ()) {
                    return -1;
                } else if (this.getZ() > other.getZ()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (x != position.x) return false;
        if (y != position.y) return false;
        if (z != position.z) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    public void plusX(int x) {
        this.setX(this.getX() + x);
    }

    public void plusY(int y) {
        this.setY(this.getY() + y);
    }

    public void plusZ(int z) {
        this.setZ(this.getZ() + z);
    }

    public Position plusXNew(int x) {
        Position result = new Position(this);
        result.plusX(x);
        return result;
    }

    public Position plusYNew(int y) {
        Position result = new Position(this);
        result.plusY(y);
        return result;
    }

    public Position plusZNew(int z) {
        Position result = new Position(this);
        result.plusZ(z);
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("X: ");
        sb.append(this.getX());
        sb.append(" Y: ");
        sb.append(this.getY());
        sb.append(" Z: ");
        sb.append(this.getZ());
        return sb.toString();
    }

    public double distanceWith(Position position) {
        return Math.sqrt((this.getX() - position.getX()) * (this.getX() - position.getX()) +
                (this.getY() - position.getY()) * (this.getY() - position.getY()) +
                (this.getZ() - position.getZ()) * (this.getZ() - position.getZ()));
    }

    /**
     * Getters and Setters
     */

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
