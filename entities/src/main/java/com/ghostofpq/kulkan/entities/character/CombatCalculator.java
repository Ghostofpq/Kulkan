package com.ghostofpq.kulkan.entities.character;

import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CombatCalculator {

    private final String FONT = "optimus_princeps_16";
    private int estimatedDamage;
    private int chanceToHit;
    private int chanceToCriticalHit;

    public CombatCalculator(GameCharacter attackingChar, Position attackingCharPosition, GameCharacter targetedChar, Position targetedCharPosition) {
        int hitBonus = 0;
        int critBonus = 0;

        switch (getFacing(attackingChar, attackingCharPosition, targetedChar, targetedCharPosition)) {
            case BACK:
                hitBonus = 20;
                critBonus = 20;
                break;
            case FACE:
                break;
            case FLANK:
                hitBonus = 10;
                critBonus = 10;
                break;
        }

        int armor = (targetedChar.getAggregatedSecondaryCharacteristics().getArmor() - attackingChar.getAggregatedSecondaryCharacteristics().getArmorPenetration());
        double ratio = 100 / (100 - armor);
        double estimatedDamageD = ratio * attackingChar.getAttackDamage() * 10;
        estimatedDamage = (int) Math.floor(estimatedDamageD);

        int applicableEscapeRate = Math.max(targetedChar.getEscape() - attackingChar.getPrecision(), 0);
        int applicableCriticalChance = Math.max(attackingChar.getCriticalStrike() - targetedChar.getResilience(), 0);

        chanceToHit = 100 - (applicableEscapeRate / 100) + hitBonus;
        chanceToHit = Math.min(Math.max(chanceToHit, 0), 100);
        chanceToCriticalHit = (applicableCriticalChance / 100) + critBonus;
        chanceToCriticalHit = Math.min(Math.max(chanceToCriticalHit, 0), 100);
    }

    public PointOfView getHeadingAngleForAttack(GameCharacter attackingChar, Position attackingCharPosition, Position targetedCharPosition) {
        PointOfView result = attackingChar.getHeadingAngle();

        if (Math.abs(targetedCharPosition.getX() - attackingCharPosition.getX()) >= Math.abs(targetedCharPosition.getZ() - attackingCharPosition.getZ())) {
            if (targetedCharPosition.getX() > attackingCharPosition.getX()) {
                result = PointOfView.EAST;
            } else if (targetedCharPosition.getX() == attackingCharPosition.getX()) {
                if (targetedCharPosition.getZ() > attackingCharPosition.getZ()) {
                    result = PointOfView.SOUTH;
                } else if (targetedCharPosition.getZ() < attackingCharPosition.getZ()) {
                    result = PointOfView.NORTH;
                }
            } else {
                result = PointOfView.WEST;
            }
        } else {
            if (targetedCharPosition.getZ() > attackingCharPosition.getZ()) {
                result = PointOfView.SOUTH;
            } else if (targetedCharPosition.getZ() == attackingCharPosition.getZ()) {
                if (targetedCharPosition.getX() > attackingCharPosition.getX()) {
                    result = PointOfView.EAST;
                } else if (targetedCharPosition.getX() < attackingCharPosition.getX()) {
                    result = PointOfView.WEST;
                }
            } else {
                result = PointOfView.NORTH;
            }
        }
        return result;
    }

    public Facing getFacing(GameCharacter attackingChar, Position attackingCharPosition, GameCharacter targetedChar, Position targetedCharPosition) {
        Facing result = null;
        switch (getHeadingAngleForAttack(attackingChar, attackingCharPosition, targetedCharPosition)) {
            case NORTH:
                switch (targetedChar.getHeadingAngle()) {
                    case NORTH:
                        result = Facing.BACK;
                        break;
                    case EAST:
                        result = Facing.FLANK;
                        break;
                    case SOUTH:
                        result = Facing.FACE;
                        break;
                    case WEST:
                        result = Facing.FLANK;
                        break;
                }
                break;
            case EAST:
                switch (targetedChar.getHeadingAngle()) {
                    case NORTH:
                        result = Facing.FLANK;
                        break;
                    case EAST:
                        result = Facing.BACK;
                        break;
                    case SOUTH:
                        result = Facing.FLANK;
                        break;
                    case WEST:
                        result = Facing.FACE;
                        break;
                }
                break;
            case SOUTH:
                switch (targetedChar.getHeadingAngle()) {
                    case NORTH:
                        result = Facing.FACE;
                        break;
                    case EAST:
                        result = Facing.FLANK;
                        break;
                    case SOUTH:
                        result = Facing.BACK;
                        break;
                    case WEST:
                        result = Facing.FLANK;
                        break;
                }
                break;
            case WEST:
                switch (targetedChar.getHeadingAngle()) {
                    case NORTH:
                        result = Facing.FLANK;
                        break;
                    case EAST:
                        result = Facing.FACE;
                        break;
                    case SOUTH:
                        result = Facing.FLANK;
                        break;
                    case WEST:
                        result = Facing.BACK;
                        break;
                }
                break;
        }
        return result;
    }

    public int getEstimatedDamage() {
        return estimatedDamage;
    }

    public int getChanceToHit() {
        return chanceToHit;
    }

    public int getChanceToCriticalHit() {
        return chanceToCriticalHit;
    }

    public enum Facing {
        FACE, FLANK, BACK
    }
}
