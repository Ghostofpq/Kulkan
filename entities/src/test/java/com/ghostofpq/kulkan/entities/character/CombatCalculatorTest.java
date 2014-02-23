package com.ghostofpq.kulkan.entities.character;

import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.clan.ClanType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@Slf4j
@RunWith(JUnit4.class)
public class CombatCalculatorTest {

    @Test
    public void test1() {
        GameCharacter gameCharacter1 = new GameCharacter("Jackie", ClanType.GORILLA, Gender.MALE);
        Position gameCharacter1Pos = new Position(0, 0, 0);
        gameCharacter1.setHeadingAngle(PointOfView.SOUTH);

        GameCharacter gameCharacter2 = new GameCharacter("Marcel", ClanType.GORILLA, Gender.MALE);
        Position gameCharacter2Pos = new Position(1, 0, 0);
        gameCharacter2.setHeadingAngle(PointOfView.NORTH);

        CombatCalculator combatCalculator = new CombatCalculator(gameCharacter1, gameCharacter1Pos, gameCharacter2, gameCharacter2Pos);

        log.debug("HIT : {}", combatCalculator.getChanceToHit());
        log.debug("CRIT : {}", combatCalculator.getChanceToCriticalHit());
        log.debug("DMG : {}", combatCalculator.getEstimatedDamage());
    }

}
