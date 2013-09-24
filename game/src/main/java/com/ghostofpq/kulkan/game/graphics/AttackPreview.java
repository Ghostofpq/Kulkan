package com.ghostofpq.kulkan.game.graphics;

import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.game.utils.FontManager;
import lombok.extern.slf4j.Slf4j;
import org.newdawn.slick.Color;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
public class AttackPreview {

    private final String FONT = "optimus_princeps_16";
    private int posX;
    private int posY;
    private int frameWidth;
    private int frameLength;
    private int frameHeight;
    private int estimatedDamage;
    private int chanceToHit;
    private int chanceToCriticalHit;
    private int posXDamage;
    private int posYDamage;
    private int posXChanceToHit;
    private int posYChanceToHit;
    private int posXChanceToCriticalHit;
    private int posYChanceToCriticalHit;


    public AttackPreview(int posX, int posY, int frameLength, int frameHeight, int frameWidth, GameCharacter attackingChar, GameCharacter targetedChar, GameCharacterRepresentation.Facing facing) {
        this.posX = posX;
        this.posY = posY;

        this.frameLength = frameLength;
        this.frameHeight = frameHeight;
        this.frameWidth = frameWidth;

        int hitBonus = 0;
        int critBonus = 0;

        switch (facing) {
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
        double estimatedDamageD = ratio * attackingChar.getAttackDamage();
        estimatedDamage = (int) Math.floor(estimatedDamageD);

        BigDecimal applicableEscapeRate = targetedChar.getEscape().min(attackingChar.getPrecision());
        applicableEscapeRate.setScale(0, RoundingMode.DOWN);
        BigDecimal applicableCriticalChance = attackingChar.getCriticalStrike().min(targetedChar.getResilience());
        applicableCriticalChance.setScale(0, RoundingMode.DOWN);
        if (applicableEscapeRate.intValue() <= 0) {
            applicableEscapeRate = new BigDecimal("0");
        }
        if (applicableCriticalChance.intValue() <= 0) {
            applicableCriticalChance = new BigDecimal("0");
        }
        chanceToHit = 100 - applicableEscapeRate.intValue() + hitBonus;
        chanceToHit = Math.min(Math.max(chanceToHit, 0), 100);
        chanceToCriticalHit = applicableCriticalChance.intValue() + critBonus;
        chanceToCriticalHit = Math.min(Math.max(chanceToCriticalHit, 0), 100);
    }

    public void render(Color color) {
        Toolbox.drawFrame(posX, posY, frameLength, frameHeight, frameWidth, color);
        StringBuilder damage = new StringBuilder();
        damage.append(estimatedDamage);
        damage.append("Dmg");
        posXDamage = posX + (frameLength - FontManager.getInstance().getFontMap().get(FONT).getWidth(damage.toString())) / 2;
        posYDamage = posY + ((frameHeight / 2) - FontManager.getInstance().getFontMap().get(FONT).getHeight(damage.toString())) / 2;

        StringBuilder hitChance = new StringBuilder();
        hitChance.append(chanceToHit);
        hitChance.append("%");
        posXChanceToHit = posX + ((frameLength / 2) - FontManager.getInstance().getFontMap().get(FONT).getWidth(hitChance.toString())) / 2;
        posYChanceToHit = posY + (frameHeight / 2) + ((frameHeight / 2) - FontManager.getInstance().getFontMap().get(FONT).getHeight(hitChance.toString())) / 2;

        StringBuilder critChance = new StringBuilder();
        critChance.append(chanceToCriticalHit);
        critChance.append("%");
        posXChanceToCriticalHit = posX + (frameLength / 2) + ((frameLength / 2) - FontManager.getInstance().getFontMap().get(FONT).getWidth(critChance.toString())) / 2;
        posYChanceToCriticalHit = posY + (frameHeight / 2) + ((frameHeight / 2) - FontManager.getInstance().getFontMap().get(FONT).getHeight(critChance.toString())) / 2;


        FontManager.getInstance().drawString(FONT, posXDamage, posYDamage, damage.toString(), Color.white);
        FontManager.getInstance().drawString(FONT, posXChanceToHit, posYChanceToHit, hitChance.toString(), Color.white);
        FontManager.getInstance().drawString(FONT, posXChanceToCriticalHit, posYChanceToCriticalHit, critChance.toString(), Color.white);
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
}
