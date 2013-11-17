package com.ghostofpq.kulkan.client.graphics;

import com.ghostofpq.kulkan.client.utils.FontManager;
import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import org.newdawn.slick.Color;

public class PrimaryCharacteristicsRender extends HUDElement {
    private final String FONT = "optimus_princeps_16";
    private PrimaryCharacteristics primaryCharacteristics;
    private int widthStep;
    private int heightStep;
    private int fontStep;
    // Characs Strength Label Position
    private int posXCharacsStrengthLabel;
    private int posYCharacsStrengthLabel;
    // Characs Endurance Label Position
    private int posXCharacsEnduranceLabel;
    private int posYCharacsEnduranceLabel;
    // Characs Intelligence Label Position
    private int posXCharacsIntelligenceLabel;
    private int posYCharacsIntelligenceLabel;
    // Characs Will Label Position
    private int posXCharacsWillLabel;
    private int posYCharacsWillLabel;
    // Characs Agility Label Position
    private int posXCharacsAgilityLabel;
    private int posYCharacsAgilityLabel;
    // Characs Move Label Position
    private int posXCharacsMoveLabel;
    private int posYCharacsMoveLabel;
    // Characs Strength Position
    private int posXCharacsStrength;
    private int posYCharacsStrength;
    // Characs Endurance Position
    private int posXCharacsEndurance;
    private int posYCharacsEndurance;
    // Characs Intelligence Position
    private int posXCharacsIntelligence;
    private int posYCharacsIntelligence;
    // Characs Will Position
    private int posXCharacsWill;
    private int posYCharacsWill;
    // Characs Agility Position
    private int posXCharacsAgility;
    private int posYCharacsAgility;
    // Characs Move Position
    private int posXCharacsMove;
    private int posYCharacsMove;

    public PrimaryCharacteristicsRender(int posX, int posY, int length, int height, PrimaryCharacteristics primaryCharacteristics) {
        this.posX = posX;
        this.posY = posY;
        this.width = length;
        this.height = height;
        this.hasFocus = false;
        this.primaryCharacteristics = primaryCharacteristics;

        widthStep = length / 6;
        heightStep = height / 3;
        fontStep = (heightStep - FontManager.getInstance().getFontMap().get(FONT).getHeight("Testouilles")) / 2;

        posXCharacsStrengthLabel = posX;
        posYCharacsStrengthLabel = posY + fontStep;

        posXCharacsEnduranceLabel = posX;
        posYCharacsEnduranceLabel = posY + heightStep + fontStep;

        posXCharacsIntelligenceLabel = posX + widthStep * 3;
        posYCharacsIntelligenceLabel = posY + fontStep;

        posXCharacsWillLabel = posX + widthStep * 3;
        posYCharacsWillLabel = posY + heightStep + fontStep;

        posXCharacsAgilityLabel = posX;
        posYCharacsAgilityLabel = posY + heightStep * 2 + fontStep;

        posXCharacsMoveLabel = posX + widthStep * 3;
        posYCharacsMoveLabel = posY + heightStep * 2 + fontStep;

        posXCharacsStrength = posX + widthStep * 2 + fontStep;
        posYCharacsStrength = posY;

        posXCharacsEndurance = posX + widthStep * 2;
        posYCharacsEndurance = posY + heightStep + fontStep;

        posXCharacsIntelligence = posX + widthStep * 5;
        posYCharacsIntelligence = posY + fontStep;

        posXCharacsWill = posX + widthStep * 5;
        posYCharacsWill = posY + heightStep + fontStep;

        posXCharacsAgility = posX + widthStep * 2;
        posYCharacsAgility = posY + heightStep * 2 + fontStep;

        posXCharacsMove = posX + widthStep * 5;
        posYCharacsMove = posY + heightStep * 2 + fontStep;
    }

    @Override
    public void draw() {
        FontManager.getInstance().drawString(FONT, posXCharacsStrengthLabel, posYCharacsStrengthLabel, "Strength", Color.white);
        FontManager.getInstance().drawString(FONT, posXCharacsStrength, posYCharacsStrength, String.valueOf(primaryCharacteristics.getStrength()), Color.white);
        FontManager.getInstance().drawString(FONT, posXCharacsEnduranceLabel, posYCharacsEnduranceLabel, "Endurance", Color.white);
        FontManager.getInstance().drawString(FONT, posXCharacsEndurance, posYCharacsEndurance, String.valueOf(primaryCharacteristics.getEndurance()), Color.white);
        FontManager.getInstance().drawString(FONT, posXCharacsIntelligenceLabel, posYCharacsIntelligenceLabel, "Intelligence", Color.white);
        FontManager.getInstance().drawString(FONT, posXCharacsIntelligence, posYCharacsIntelligence, String.valueOf(primaryCharacteristics.getIntelligence()), Color.white);
        FontManager.getInstance().drawString(FONT, posXCharacsWillLabel, posYCharacsWillLabel, "Will", Color.white);
        FontManager.getInstance().drawString(FONT, posXCharacsWill, posYCharacsWill, String.valueOf(primaryCharacteristics.getWill()), Color.white);
        FontManager.getInstance().drawString(FONT, posXCharacsAgilityLabel, posYCharacsAgilityLabel, "Agility", Color.white);
        FontManager.getInstance().drawString(FONT, posXCharacsAgility, posYCharacsAgility, String.valueOf(primaryCharacteristics.getAgility()), Color.white);
        FontManager.getInstance().drawString(FONT, posXCharacsMoveLabel, posYCharacsMoveLabel, "Movement", Color.white);
        FontManager.getInstance().drawString(FONT, posXCharacsMove, posYCharacsMove, String.valueOf(primaryCharacteristics.getMovement()), Color.white);
    }
}
