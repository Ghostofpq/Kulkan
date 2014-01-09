package com.ghostofpq.kulkan.entities.battlefield;

import com.ghostofpq.kulkan.commons.Node;
import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.commons.Tree;
import com.ghostofpq.kulkan.entities.utils.Range;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Battlefield implements Serializable {

    private static final long serialVersionUID = -6878010880627782277L;
    private int length;  //x
    private int height;  //y
    private int depth;  //z
    private Map<Position, BattlefieldElement> battlefieldElementMap;
    private int numberOfPlayers;
    private Map<Integer, List<Position>> deploymentZones;
    private Map<Integer, PointOfView> startingPointsOfView;

    public Battlefield(int length, int height, int depth, int numberOfPlayers) {
        this.length = length;
        this.height = height;
        this.depth = depth;
        this.numberOfPlayers = numberOfPlayers;
        battlefieldElementMap = new HashMap<Position, BattlefieldElement>();
        deploymentZones = new HashMap<Integer, List<Position>>();
        startingPointsOfView = new HashMap<Integer, PointOfView>();
    }

    public void addDeployementZone(Integer playerNumber, Position position) {
        if (null != playerNumber) {
            List<Position> deploymentZone = deploymentZones.get(playerNumber);
            if (null == deploymentZone) {
                deploymentZone = new ArrayList<Position>();
            }
            deploymentZone.add(position);
            deploymentZones.put(playerNumber, deploymentZone);
            startingPointsOfView.put(playerNumber, PointOfView.SOUTH);
        }
    }

    public void setStartingPointsOfViewForPlayer(Integer playerNumber, PointOfView pointOfView) {
        startingPointsOfView.put(playerNumber, pointOfView);
    }

    public PointOfView getStartingPointsOfViewForPlayer(Integer playerNumber) {
        return startingPointsOfView.get(playerNumber);
    }

    public void addBattlefieldElement(int x, int y, int z, BattlefieldElement.BattlefieldElementType type) {
        Position position = new Position(x, y, z);
        BattlefieldElement battlefieldElement = null;
        switch (type) {
            case BLOC:
                battlefieldElement = new Block();
                break;
        }
        if (battlefieldElement != null) {
            battlefieldElementMap.put(position, battlefieldElement);
        }
    }

    /**
     * Getters and Setters
     */

    public int getLength() {
        return length;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    public Map<Position, BattlefieldElement> getBattlefieldElementMap() {
        return battlefieldElementMap;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public Map<Integer, List<Position>> getDeploymentZones() {
        return deploymentZones;
    }

    public boolean canMoveTo(Position position, int height) {
        Position testPos = new Position(position.getX(), position.getY() + 1, position.getZ());

        if (height == 0) {
            return true;
        }
        if (getBattlefieldElementMap().containsKey(testPos)) {
            return false;
        } else {
            return canMoveTo(testPos, height - 1);
        }
    }

    public List<Position> getPath(Position position, Position positionToGo) {
        List<Position> path = new ArrayList<Position>();
        if (position.getX() != positionToGo.getX()) {
            if (position.getX() < positionToGo.getX()) {
                // We clone the position to test if we can move.
                Position altPos = new Position(position);
                altPos.plusX(1);

                if (canMoveTo(altPos, 2)) {
                    position.plusX(1);
                    path.add(new Position(position));
                    path.addAll(getPath(position, positionToGo));
                }
            } else {
                // We clone the position to test if we can move.
                Position altPos = new Position(position);
                altPos.plusX(-1);

                if (canMoveTo(altPos, 2)) {
                    position.plusX(-1);
                    path.add(new Position(position));
                    path.addAll(getPath(position, positionToGo));
                }
            }
        }

        if (position.getZ() != positionToGo.getZ()) {
            if (position.getZ() < positionToGo.getZ()) {
                // We clone the position to test if we can move.
                Position altPos = new Position(position);
                altPos.plusZ(1);

                if (canMoveTo(altPos, 2)) {
                    position.plusZ(1);
                    path.add(new Position(position));
                    path.addAll(getPath(position, positionToGo));
                }
            } else {
                // We clone the position to test if we can move.
                Position altPos = new Position(position);
                altPos.plusZ(-1);

                if (canMoveTo(altPos, 2)) {
                    position.plusZ(-1);
                    path.add(new Position(position));
                    path.addAll(getPath(position, positionToGo));
                }
            }
        }

        if (position.getY() != positionToGo.getY()) {
            if (position.getY() < positionToGo.getY()) {
                // We clone the position to test if we can move.
                Position altPos = new Position(position);
                altPos.plusY(1);

                if (canMoveTo(altPos, 2)) {
                    position.plusY(1);
                    path.add(new Position(position));
                    path.addAll(getPath(position, positionToGo));
                }
            } else {
                // We clone the position to test if we can move.
                Position altPos = new Position(position);
                altPos.plusY(-1);

                if (canMoveTo(altPos, 2)) {
                    position.plusY(-1);
                    path.add(new Position(position));
                    path.addAll(getPath(position, positionToGo));
                }
            }
        }

        return path;
    }

    public Tree<Position> getPositionTree(Position position, int dist, int heightLimit, int jumpLimit) {
        Tree<Position> positionTree = new Tree<Position>(position);
        getPossiblePositions(position, positionTree.getRoot(), dist, heightLimit, jumpLimit);
        return positionTree;
    }

    public void getPossiblePositions(Position position, Node<Position> parent, int dist, int heightLimit, int jumpLimit) {
        if (dist <= 0) {
            return;
        }

        List<Position> possiblePositions = new ArrayList<Position>();

        if (position.getX() > 0) {
            possiblePositions.addAll(getPossiblePositionsAt(position.getX() - 1, position.getZ(), heightLimit));
        }
        if (position.getX() < getLength()) {
            possiblePositions.addAll(getPossiblePositionsAt(position.getX() + 1, position.getZ(), heightLimit));
        }
        if (position.getZ() > 0) {
            possiblePositions.addAll(getPossiblePositionsAt(position.getX(), position.getZ() - 1, heightLimit));
        }
        if (position.getZ() < getDepth()) {
            possiblePositions.addAll(getPossiblePositionsAt(position.getX(), position.getZ() + 1, heightLimit));
        }

        for (Position possiblePosition : possiblePositions) {
            if (position.getY() == possiblePosition.getY()) {
                Node<Position> child = parent.addChild(possiblePosition, 1);

                if (child != null) {
                    getPossiblePositions(possiblePosition, child, dist - 1, heightLimit, jumpLimit);
                }
            } else if (Math.abs(position.getY() - possiblePosition.getY()) <= jumpLimit) {
                Node<Position> child = parent.addChild(possiblePosition, 1 + jumpLimit);
                if (child != null) {
                    getPossiblePositions(possiblePosition, child, dist - (1 + jumpLimit), heightLimit, jumpLimit);
                }
            }
        }
    }

    public List<Position> getPossiblePositionsToAttack(Position position, Range range) {
        Tree<Position> positionTree = new Tree<Position>(position);
        List<Position> result = new ArrayList<Position>();
        switch (range.getRangeType()) {
            case CIRCLE:
                getPossiblePositions(position, positionTree.getRoot(), range.getMaxRange(), range.getMaxRange(), 1);
                Tree<Position> innerCircle = new Tree<Position>(position);
                getPossiblePositions(position, innerCircle.getRoot(), range.getMinRange(), range.getMinRange(), 1);
                result = positionTree.getAllElements();
                result.removeAll(innerCircle.getAllElements());
                break;
            case CROSS:
                getPossiblePositions(position, positionTree.getRoot(), range.getMaxRange(), range.getMaxRange(), 1);
                result = positionTree.getAllElements();
                break;
        }
        return result;
    }

    private List<Position> getPossiblePositionsAt(int x, int z, int height) {
        List<Position> possiblePositions = new ArrayList<Position>();
        for (Position position : battlefieldElementMap.keySet()) {
            if ((position.getX() == x) && (position.getZ() == z)) {
                if (canMoveTo(position, height)) {
                    possiblePositions.add(position);
                }
            }
        }
        return possiblePositions;
    }
}
