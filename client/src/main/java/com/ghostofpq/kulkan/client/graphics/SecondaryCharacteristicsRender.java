package com.ghostofpq.kulkan.client.graphics;

import com.ghostofpq.kulkan.client.utils.FontManager;
import com.ghostofpq.kulkan.entities.characteristics.SecondaryCharacteristics;
import org.newdawn.slick.Color;

public class SecondaryCharacteristicsRender extends HUDElement {
    private final String FONT = "optimus_princeps_16";
    private SecondaryCharacteristics secondaryCharacteristics;
    private int widthStep;
    private int heightStep;
    private int fontStep;
    // Characs Attack Label Position
    private int posXCharacsAttackLabel;
    private int posYCharacsAttackLabel;
    // Characs Magic Damage Label Position
    private int posXCharacsMagicAttackLabel;
    private int posYCharacsMagicAttackLabel;
    // Characs Armor Label Position
    private int posXCharacsArmorLabel;
    private int posYCharacsArmorLabel;
    // Characs Magic Armor Label Position
    private int posXCharacsMagicArmorLabel;
    private int posYCharacsMagicArmorLabel;
    // Characs Armor Pen Label Position
    private int posXCharacsArmorPenLabel;
    private int posYCharacsArmorPenLabel;
    // Characs Magic Pen Label Position
    private int posXCharacsMagicPenLabel;
    private int posYCharacsMagicPenLabel;
    // Characs Precision Label Position
    private int posXCharacsPrecisionLabel;
    private int posYCharacsPrecisionLabel;
    // Characs Escape Label Position
    private int posXCharacsEscapeLabel;
    private int posYCharacsEscapeLabel;
    // Characs CriticalStrike Label Position
    private int posXCharacsCriticalStrikeLabel;
    private int posYCharacsCriticalStrikeLabel;
    // Characs Resilience Label Position
    private int posXCharacsResilienceLabel;
    private int posYCharacsResilienceLabel;
    // Characs Attack Position
    private int posXCharacsAttack;
    private int posYCharacsAttack;
    // Characs Magic Damage Position
    private int posXCharacsMagicAttack;
    private int posYCharacsMagicAttack;
    // Characs Armor Position
    private int posXCharacsArmor;
    private int posYCharacsArmor;
    // Characs Magic Armor Position
    private int posXCharacsMagicArmor;
    private int posYCharacsMagicArmor;
    // Characs Armor Pen Position
    private int posXCharacsArmorPen;
    private int posYCharacsArmorPen;
    // Characs Magic Pen Position
    private int posXCharacsMagicPen;
    private int posYCharacsMagicPen;
    // Characs Precision Position
    private int posXCharacsPrecision;
    private int posYCharacsPrecision;
    // Characs Escape Position
    private int posXCharacsEscape;
    private int posYCharacsEscape;
    // Characs Critical Strike Position
    private int posXCharacsCriticalStrike;
    private int posYCharacsCriticalStrike;
    // Characs Resilience Position
    private int posXCharacsResilience;
    private int posYCharacsResilience;
    private FontManager fontManager = FontManager.getInstance();

    public SecondaryCharacteristicsRender(int posX, int posY, int length, int height, SecondaryCharacteristics secondaryCharacteristics) {
        this.posX = posX;
        this.posY = posY;
        this.width = length;
        this.height = height;
        this.hasFocus = false;
        this.secondaryCharacteristics = secondaryCharacteristics;

        widthStep = length / 6;
        heightStep = height / 5;
        fontStep = (heightStep - fontManager.getFontMap().get(FONT).getHeight("Testouilles")) / 2;

        posXCharacsAttackLabel = posX;
        posYCharacsAttackLabel = posY + fontStep;
        posXCharacsAttack = posX + 2 * widthStep;
        posYCharacsAttack = posY + fontStep;

        posXCharacsMagicAttackLabel = posX + 3 * widthStep;
        posYCharacsMagicAttackLabel = posY + fontStep;
        posXCharacsMagicAttack = posX + 5 * widthStep;
        posYCharacsMagicAttack = posY + fontStep;

        posXCharacsArmorLabel = posX;
        posYCharacsArmorLabel = posY + fontStep + heightStep;
        posXCharacsArmor = posX + 2 * widthStep;
        posYCharacsArmor = posY + fontStep + heightStep;

        posXCharacsMagicArmorLabel = posX + 3 * widthStep;
        posYCharacsMagicArmorLabel = posY + fontStep + heightStep;
        posXCharacsMagicArmor = posX + 5 * widthStep;
        posYCharacsMagicArmor = posY + fontStep + heightStep;

        posXCharacsArmorPenLabel = posX;
        posYCharacsArmorPenLabel = posY + fontStep + 2 * heightStep;
        posXCharacsArmorPen = posX + 2 * widthStep;
        posYCharacsArmorPen = posY + fontStep + 2 * heightStep;

        posXCharacsMagicPenLabel = posX + 3 * widthStep;
        posYCharacsMagicPenLabel = posY + fontStep + 2 * heightStep;
        posXCharacsMagicPen = posX + 5 * widthStep;
        posYCharacsMagicPen = posY + fontStep + 2 * heightStep;

        posXCharacsPrecisionLabel = posX;
        posYCharacsPrecisionLabel = posY + fontStep + 3 * heightStep;
        posXCharacsPrecision = posX + 2 * widthStep;
        posYCharacsPrecision = posY + fontStep + 3 * heightStep;

        posXCharacsEscapeLabel = posX + 3 * widthStep;
        posYCharacsEscapeLabel = posY + fontStep + 3 * heightStep;
        posXCharacsEscape = posX + 5 * widthStep;
        posYCharacsEscape = posY + fontStep + 3 * heightStep;

        posXCharacsCriticalStrikeLabel = posX;
        posYCharacsCriticalStrikeLabel = posY + fontStep + 4 * heightStep;
        posXCharacsCriticalStrike = posX + 2 * widthStep;
        posYCharacsCriticalStrike = posY + fontStep + 4 * heightStep;

        posXCharacsResilienceLabel = posX + 3 * widthStep;
        posYCharacsResilienceLabel = posY + fontStep + 4 * heightStep;
        posXCharacsResilience = posX + 5 * widthStep;
        posYCharacsResilience = posY + fontStep + 4 * heightStep;
    }

    @Override
    public void draw() {
        fontManager.drawString(FONT, posXCharacsAttackLabel, posYCharacsAttackLabel, "Attack", Color.white);
        fontManager.drawString(FONT, posXCharacsAttack, posYCharacsAttack, String.valueOf(secondaryCharacteristics.getAttackDamage()), Color.white);

        fontManager.drawString(FONT, posXCharacsMagicAttackLabel, posYCharacsMagicAttackLabel, "Magic Power", Color.white);
        fontManager.drawString(FONT, posXCharacsMagicAttack, posYCharacsMagicAttack, String.valueOf(secondaryCharacteristics.getMagicalDamage()), Color.white);

        fontManager.drawString(FONT, posXCharacsArmorLabel, posYCharacsArmorLabel, "Armor", Color.white);
        fontManager.drawString(FONT, posXCharacsArmor, posYCharacsArmor, String.valueOf(secondaryCharacteristics.getArmor()), Color.white);

        fontManager.drawString(FONT, posXCharacsMagicArmorLabel, posYCharacsMagicArmorLabel, "Magic Armor", Color.white);
        fontManager.drawString(FONT, posXCharacsMagicArmor, posYCharacsMagicArmor, String.valueOf(secondaryCharacteristics.getMagicResist()), Color.white);

        fontManager.drawString(FONT, posXCharacsArmorPenLabel, posYCharacsArmorPenLabel, "Armor Pen", Color.white);
        fontManager.drawString(FONT, posXCharacsArmorPen, posYCharacsArmorPen, String.valueOf(secondaryCharacteristics.getArmor()), Color.white);

        fontManager.drawString(FONT, posXCharacsMagicPenLabel, posYCharacsMagicPenLabel, "Magic Pen", Color.white);
        fontManager.drawString(FONT, posXCharacsMagicPen, posYCharacsMagicPen, String.valueOf(secondaryCharacteristics.getMagicPenetration()), Color.white);

        fontManager.drawString(FONT, posXCharacsPrecisionLabel, posYCharacsPrecisionLabel, "Precision", Color.white);
        fontManager.drawString(FONT, posXCharacsPrecision, posYCharacsPrecision, String.valueOf((float) (secondaryCharacteristics.getPrecision() / 100)), Color.white);

        fontManager.drawString(FONT, posXCharacsEscapeLabel, posYCharacsEscapeLabel, "Escape", Color.white);
        fontManager.drawString(FONT, posXCharacsEscape, posYCharacsEscape, String.valueOf((float) (secondaryCharacteristics.getEscape() / 100)), Color.white);

        fontManager.drawString(FONT, posXCharacsCriticalStrikeLabel, posYCharacsCriticalStrikeLabel, "Critical Strike", Color.white);
        fontManager.drawString(FONT, posXCharacsCriticalStrike, posYCharacsCriticalStrike, String.valueOf((float) (secondaryCharacteristics.getCriticalStrike() / 100)), Color.white);

        fontManager.drawString(FONT, posXCharacsResilienceLabel, posYCharacsResilienceLabel, "Resilience", Color.white);
        fontManager.drawString(FONT, posXCharacsResilience, posYCharacsResilience, String.valueOf((float) (secondaryCharacteristics.getResilience() / 100)), Color.white);
    }
}
