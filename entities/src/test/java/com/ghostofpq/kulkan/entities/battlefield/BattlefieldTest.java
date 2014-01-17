package com.ghostofpq.kulkan.entities.battlefield;

import com.ghostofpq.kulkan.commons.Node;
import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.commons.Tree;
import com.ghostofpq.kulkan.entities.utils.Range;
import com.ghostofpq.kulkan.entities.utils.RangeType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

@Slf4j
@RunWith(JUnit4.class)
public class BattlefieldTest {


    @Test
    public void getPositionTreeTest() {
        Battlefield battlefield = flatBattlefield(10, 5, 10);

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
        Battlefield battlefield = flatBattlefield(10, 5, 10);

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

    @Test
    public void getPossiblePositionsToAttackRangeSquare() {
        Battlefield battlefield = flatBattlefield(10, 5, 10);

        Position originPoint = new Position(2, 0, 2);

        Range range = new Range(RangeType.SQUARE, 0, 2);

        List<Position> result = battlefield.getPossiblePositionsToAttack(originPoint, range);

        //   0 1 2 3 4 X
        // 0     X
        // 1   X X X
        // 2 X X O X X
        // 3   X X X
        // 4     X
        // Z
        assert (result.contains(new Position(2, 0, 2)));
        assert (result.contains(new Position(1, 0, 2)));
        assert (result.contains(new Position(0, 0, 2)));
        assert (result.contains(new Position(3, 0, 2)));
        assert (result.contains(new Position(4, 0, 2)));
        assert (result.contains(new Position(2, 0, 1)));
        assert (result.contains(new Position(2, 0, 0)));
        assert (result.contains(new Position(2, 0, 3)));
        assert (result.contains(new Position(2, 0, 4)));
        assert (result.contains(new Position(1, 0, 1)));
        assert (result.contains(new Position(1, 0, 3)));
        assert (result.contains(new Position(3, 0, 1)));
        assert (result.contains(new Position(3, 0, 3)));

        assert (!result.contains(new Position(0, 0, 0)));
        assert (!result.contains(new Position(1, 0, 0)));
        assert (!result.contains(new Position(3, 0, 0)));
        assert (!result.contains(new Position(4, 0, 0)));
        assert (!result.contains(new Position(0, 0, 1)));
        assert (!result.contains(new Position(4, 0, 1)));
        assert (!result.contains(new Position(0, 0, 3)));
        assert (!result.contains(new Position(4, 0, 3)));
        assert (!result.contains(new Position(0, 0, 4)));
        assert (!result.contains(new Position(1, 0, 4)));
        assert (!result.contains(new Position(3, 0, 4)));
        assert (!result.contains(new Position(4, 0, 4)));
    }

    @Test
    public void getPossiblePositionsToAttackRangeCircle() {
        Battlefield battlefield = flatBattlefield(10, 5, 10);

        Position originPoint = new Position(2, 0, 2);

        Range range = new Range(RangeType.CIRCLE, 1, 2);

        List<Position> result = battlefield.getPossiblePositionsToAttack(originPoint, range);

        //   0 1 2 3 4 X
        // 0     X
        // 1   X   X
        // 2 X   O   X
        // 3   X   X
        // 4     X
        // Z

        assert (result.contains(new Position(0, 0, 2)));
        assert (result.contains(new Position(1, 0, 1)));
        assert (result.contains(new Position(1, 0, 3)));
        assert (result.contains(new Position(2, 0, 0)));
        assert (result.contains(new Position(2, 0, 4)));
        assert (result.contains(new Position(3, 0, 1)));
        assert (result.contains(new Position(3, 0, 3)));
        assert (result.contains(new Position(4, 0, 2)));

        assert (!result.contains(new Position(0, 0, 0)));
        assert (!result.contains(new Position(1, 0, 0)));
        assert (!result.contains(new Position(3, 0, 0)));
        assert (!result.contains(new Position(4, 0, 0)));
        assert (!result.contains(new Position(0, 0, 1)));
        assert (!result.contains(new Position(2, 0, 1)));
        assert (!result.contains(new Position(4, 0, 1)));
        assert (!result.contains(new Position(1, 0, 2)));
        assert (!result.contains(new Position(3, 0, 2)));
        assert (!result.contains(new Position(0, 0, 3)));
        assert (!result.contains(new Position(2, 0, 3)));
        assert (!result.contains(new Position(4, 0, 3)));
        assert (!result.contains(new Position(0, 0, 4)));
        assert (!result.contains(new Position(1, 0, 4)));
        assert (!result.contains(new Position(3, 0, 4)));
        assert (!result.contains(new Position(4, 0, 4)));
    }

    @Test
    public void getPossiblePositionsToAttackRangeCross() {
        Battlefield battlefield = flatBattlefield(10, 5, 10);

        Position originPoint = new Position(2, 0, 2);

        Range range = new Range(RangeType.CROSS, 0, 2);

        List<Position> result = battlefield.getPossiblePositionsToAttack(originPoint, range);

        //   0 1 2 3 4 X
        // 0     X
        // 1     X
        // 2 X X O X X
        // 3     X
        // 4     X
        // Z
        assert (result.contains(new Position(2, 0, 2)));
        assert (result.contains(new Position(1, 0, 2)));
        assert (result.contains(new Position(0, 0, 2)));
        assert (result.contains(new Position(3, 0, 2)));
        assert (result.contains(new Position(4, 0, 2)));
        assert (result.contains(new Position(2, 0, 1)));
        assert (result.contains(new Position(2, 0, 0)));
        assert (result.contains(new Position(2, 0, 3)));
        assert (result.contains(new Position(2, 0, 4)));

        assert (!result.contains(new Position(0, 0, 0)));
        assert (!result.contains(new Position(1, 0, 0)));
        assert (!result.contains(new Position(3, 0, 0)));
        assert (!result.contains(new Position(4, 0, 0)));
        assert (!result.contains(new Position(0, 0, 1)));
        assert (!result.contains(new Position(1, 0, 1)));
        assert (!result.contains(new Position(3, 0, 1)));
        assert (!result.contains(new Position(4, 0, 1)));
        assert (!result.contains(new Position(0, 0, 3)));
        assert (!result.contains(new Position(1, 0, 3)));
        assert (!result.contains(new Position(3, 0, 3)));
        assert (!result.contains(new Position(4, 0, 3)));
        assert (!result.contains(new Position(0, 0, 0)));
        assert (!result.contains(new Position(1, 0, 0)));
        assert (!result.contains(new Position(3, 0, 4)));
        assert (!result.contains(new Position(4, 0, 4)));
    }

    private Battlefield flatBattlefield(int length, int height, int depth) {
        Battlefield battlefield = new Battlefield(length, height, depth, 2);

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < depth; j++) {
                battlefield.addBattlefieldElement(i, 0, j, BattlefieldElement.BattlefieldElementType.BLOC);
            }
        }

        return battlefield;
    }
}
