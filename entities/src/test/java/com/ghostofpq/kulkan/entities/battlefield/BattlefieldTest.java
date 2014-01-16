package com.ghostofpq.kulkan.entities.battlefield;

import com.ghostofpq.kulkan.commons.Node;
import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.commons.Tree;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

//@Slf4j
@RunWith(JUnit4.class)
public class BattlefieldTest {


    @Test
    public void getPositionTreeTest() {
        int length = 10;
        int height = 5;
        int depth = 10;

        Battlefield battlefield = new Battlefield(length, height, depth, 2);

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < depth; j++) {
                battlefield.addBattlefieldElement(i, 0, j, BattlefieldElement.BattlefieldElementType.BLOC);
            }
        }

        Position originPoint = new Position(1, 0, 1);

        int dist = 3;
        int heightLimit = 2;
        int jumpLimit = 1;

        Tree<Position> result = battlefield.getPositionTree(originPoint, dist, heightLimit, jumpLimit, false, PointOfView.NORTH);
        //log.debug(" TEST 1 ");
        //log.debug("     {} : {}", result.getRoot().getData().toString(), result.getRoot().getDistanceFromTop());
        //log.debug("{} children", result.getRoot().getChildren().size());
        for (Node<Position> child : result.getRoot().getChildren()) {
            //log.debug("   ->{} : {}", child.getData().toString(), child.getDistanceFromTop());
            for (Node<Position> child2 : child.getChildren()) {
                //log.debug("  -->{} : {}", child2.getData().toString(), child2.getDistanceFromTop());
                for (Node<Position> child3 : child2.getChildren()) {
                    //log.debug(" --->{} : {}", child3.getData().toString(), child3.getDistanceFromTop());
                    for (Node<Position> child4 : child3.getChildren()) {
                        //log.debug("---->{} : {}", child4.getData().toString(), child4.getDistanceFromTop());
                    }
                }
            }
        }


        List<Position> positionList = result.getAllElements();
        for (Position pos : positionList) {
            //log.debug("{}",pos.toString() );
        }

    }

    @Test
    public void getPositionTreeTest2() {
        int length = 10;
        int height = 5;
        int depth = 10;

        Battlefield battlefield = new Battlefield(length, height, depth, 2);

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < depth; j++) {
                battlefield.addBattlefieldElement(i, 0, j, BattlefieldElement.BattlefieldElementType.BLOC);
            }
        }

        battlefield.addBattlefieldElement(0, 1, 0, BattlefieldElement.BattlefieldElementType.BLOC);
        battlefield.addBattlefieldElement(0, 2, 1, BattlefieldElement.BattlefieldElementType.BLOC);
        battlefield.addBattlefieldElement(0, 3, 2, BattlefieldElement.BattlefieldElementType.BLOC);

        Position originPoint = new Position(1, 0, 1);

        int dist = 3;
        int heightLimit = 2;
        int jumpLimit = 1;

        Tree<Position> result = battlefield.getPositionTree(originPoint, dist, heightLimit, jumpLimit, false, PointOfView.NORTH);
        //log.debug(" TEST 2");
        // log.debug("     {} : {}", result.getRoot().getData().toString(), result.getRoot().getDistanceFromTop());
        for (Node<Position> child : result.getRoot().getChildren()) {
            // log.debug("   ->{} : {}", child.getData().toString(), child.getDistanceFromTop());
            for (Node<Position> child2 : child.getChildren()) {
                // log.debug("  -->{} : {}", child2.getData().toString(), child2.getDistanceFromTop());
                for (Node<Position> child3 : child2.getChildren()) {
                    //log.debug(" --->{} : {}", child3.getData().toString(), child3.getDistanceFromTop());
                    for (Node<Position> child4 : child3.getChildren()) {
                        // log.debug("---->{} : {}", child4.getData().toString(), child4.getDistanceFromTop());
                    }
                }
            }
        }
    }
}
