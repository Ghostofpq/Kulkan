package com.ghostofpq.kulkan.server.utils;

import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.battlefield.Battlefield;
import com.ghostofpq.kulkan.entities.battlefield.BattlefieldElement;

public class MapCreator {
    public static void main(String[] argv) {

        int length = 10;
        int height = 5;
        int depth = 10;

        Battlefield battlefield = new Battlefield(length, height, depth, 2);

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < depth; j++) {
                battlefield.addBattlefieldElement(i, 0, j, BattlefieldElement.BattlefieldElementType.BLOC);
            }
        }

        for (int i = 0; i < length; i++) {
            Position position = new Position(i, 0, 0);
            battlefield.addDeploymentZone(0, position);
            Position position2 = new Position(i, 0, depth - 1);
            battlefield.addDeploymentZone(1, position2);
        }
        battlefield.setStartingPointsOfViewForPlayer(0, PointOfView.SOUTH);
        battlefield.setStartingPointsOfViewForPlayer(1, PointOfView.NORTH);

        battlefield.addBattlefieldElement(0, 1, 0, BattlefieldElement.BattlefieldElementType.BLOC);
        battlefield.addBattlefieldElement(0, 2, 1, BattlefieldElement.BattlefieldElementType.BLOC);
        battlefield.addBattlefieldElement(0, 3, 2, BattlefieldElement.BattlefieldElementType.BLOC);

        SaveManager saveManager = SaveManager.getInstance();
        saveManager.saveMap(battlefield, "mapTest1");

    }
}
