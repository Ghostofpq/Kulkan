package com.ghostofpq.kulkan.client.graphics;

import com.ghostofpq.kulkan.client.utils.FontManager;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import org.newdawn.slick.Color;

public class CharacterRender {
    private final String FONT = "optimus_princeps_16";
    private int frameWidth;
    private int frameLength;
    private int frameHeight;
    private int barsPaddingX;
    private int barsPaddingY;
    private int barsHeight;
    private int barsLength;
    private int nameFrameLength;
    private int nameFrameHeight;
    private int barsFrameLength;
    private int barsFrameHeight;
    private int characsFrameLength;
    private int characsFrameHeight;
    private int jobFrameLength;
    private int jobFrameHeight;
    // Origin and Name Frame Position
    private int posX;
    private int posY;
    // Name Position
    private int posXName;
    private int posYName;
    // Clan Position
    private int posXClan;
    private int posYClan;
    // Level Position
    private int posXLevel;
    private int posYLevel;
    // Bars Frame Position
    private int posXBarsFrame;
    private int posYBarsFrame;
    // HP Bar Position
    private int posXBar1;
    private int posYBar1;
    // MP Bar Position
    private int posXBar2;
    private int posYBar2;
    // XP Bar Position
    private int posXBar3;
    private int posYBar3;
    // HP Label Position
    private int posXLabel1;
    private int posYLabel1;
    // MP Label Position
    private int posXLabel2;
    private int posYLabel2;
    // XP Label Position
    private int posXLabel3;
    private int posYLabel3;
    // Characs Frame Position
    private int posXCharacsFrame;
    private int posYCharacsFrame;
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
    // Job Frame Position
    private int posXJobFrame;
    private int posYJobFrame;
    // Job Position
    private int posXJob;
    private int posYJob;
    // Job Points Position
    private int posXJobPoints;
    private int posYJobPoints;
    private GameCharacter character;
    private BarRender experienceBar;
    private BarRender lifeBar;
    private BarRender manaBar;
    private FontManager fontManager = FontManager.getInstance();

    public CharacterRender(float posX, float posY, int frameLength, int frameHeight, int frameWidth, GameCharacter character) {
        this.posX = (int) posX;
        this.posY = (int) posY;
        this.character = character;

        this.frameLength = frameLength;
        this.frameHeight = frameHeight;
        this.frameWidth = frameWidth;


        calculatePositions();
    }

    private void calculatePositions() {
        //Name Frame
        nameFrameHeight = (1 * frameHeight / 5);
        nameFrameLength = frameLength;

        posXName = posX + ((frameLength / 2) - fontManager.getFontMap().get(FONT).getWidth(character.getName())) / 2;
        posYName = posY + (nameFrameHeight - fontManager.getFontMap().get(FONT).getHeight(character.getName())) / 2;
        posXClan = posX + (frameLength / 2) + (((3 * frameLength) / 10) - fontManager.getFontMap().get(FONT).getWidth(character.getClan().getName())) / 2;
        posYClan = posY + (nameFrameHeight - fontManager.getFontMap().get(FONT).getHeight(character.getClan().getName())) / 2;
        posXLevel = posX + (frameLength / 2) + ((3 * frameLength) / 10) + (((2 * frameLength) / 10) - fontManager.getFontMap().get(FONT).getWidth("LvL : " + character.getLevel())) / 2;
        posYLevel = posY + (nameFrameHeight - fontManager.getFontMap().get(FONT).getHeight("LvL : " + character.getLevel())) / 2;
        //Bars Frame
        barsFrameLength = (2 * frameLength / 3) + frameWidth / 2;
        barsFrameHeight = (3 * frameHeight / 5) + frameWidth;

        barsPaddingX = (3 * barsFrameLength) / 40;
        barsPaddingY = (barsFrameHeight / 15);

        barsLength = (3 * (barsFrameLength / 4)) - (2 * barsPaddingX);
        barsHeight = (barsFrameHeight / 3) - (2 * barsPaddingY);

        posXBarsFrame = posX;
        posYBarsFrame = posY + nameFrameHeight - frameWidth;

        posXBar1 = posXBarsFrame + (barsFrameLength / 4) + barsPaddingX;
        posYBar1 = posYBarsFrame + barsPaddingY;
        posXBar2 = posXBar1;
        posYBar2 = posYBar1 + (barsFrameHeight / 3);
        posXBar3 = posXBar1;
        posYBar3 = posYBar2 + (barsFrameHeight / 3);

        posXLabel1 = posXBarsFrame + (((barsFrameLength / 4) - fontManager.getFontMap().get(FONT).getWidth("HP")) / 2);
        posYLabel1 = posYBarsFrame + (((barsFrameHeight / 3) - fontManager.getFontMap().get(FONT).getHeight("HP")) / 2);
        posXLabel2 = posXBarsFrame + (((barsFrameLength / 4) - fontManager.getFontMap().get(FONT).getWidth("MP")) / 2);
        posYLabel2 = posYBarsFrame + (barsFrameHeight / 3) + (((barsFrameHeight / 3) - fontManager.getFontMap().get(FONT).getHeight("HP")) / 2);
        posXLabel3 = posXBarsFrame + (((barsFrameLength / 4) - fontManager.getFontMap().get(FONT).getWidth("XP")) / 2);
        posYLabel3 = posYBarsFrame + ((2 * barsFrameHeight) / 3) + (((barsFrameHeight / 3) - fontManager.getFontMap().get(FONT).getHeight("HP")) / 2);

        lifeBar = new BarRender(character.getCurrentHealthPoint(), character.getMaxHealthPoint(), posXBar1, posYBar1, barsLength, barsHeight, Color.green, Color.red);
        manaBar = new BarRender(character.getCurrentManaPoint(), character.getMaxManaPoint(), posXBar2, posYBar2, barsLength, barsHeight, Color.blue, Color.darkGray);
        experienceBar = new BarRender(character.getExperience(), character.getNextLevel(), posXBar3, posYBar3, barsLength, barsHeight, Color.yellow, Color.gray);

        //Characs Frame
        characsFrameLength = (1 * frameLength / 3) + frameWidth / 2;
        characsFrameHeight = barsFrameHeight + frameWidth;

        posXCharacsFrame = posX + barsFrameLength - frameWidth;
        posYCharacsFrame = posY + nameFrameHeight - frameWidth;
        // LABELS
        posXCharacsStrengthLabel = posXCharacsFrame + ((characsFrameLength / 6 - fontManager.getFontMap().get(FONT).getWidth("S")) / 2);
        posYCharacsStrengthLabel = posYCharacsFrame + ((characsFrameHeight / 3 - fontManager.getFontMap().get(FONT).getHeight("S")) / 2);

        posXCharacsEnduranceLabel = posXCharacsFrame + (characsFrameLength / 2) + ((characsFrameLength / 6 - fontManager.getFontMap().get(FONT).getWidth("E")) / 2);
        posYCharacsEnduranceLabel = posYCharacsFrame + ((characsFrameHeight / 3 - fontManager.getFontMap().get(FONT).getHeight("E")) / 2);

        posXCharacsIntelligenceLabel = posXCharacsFrame + ((characsFrameLength / 6 - fontManager.getFontMap().get(FONT).getWidth("I")) / 2);
        posYCharacsIntelligenceLabel = posYCharacsFrame + (characsFrameHeight / 3) + (((characsFrameHeight) / 3 - fontManager.getFontMap().get(FONT).getHeight("I")) / 2);

        posXCharacsWillLabel = posXCharacsFrame + (characsFrameLength / 2) + ((characsFrameLength / 6 - fontManager.getFontMap().get(FONT).getWidth("W")) / 2);
        posYCharacsWillLabel = posYCharacsFrame + (characsFrameHeight / 3) + (((characsFrameHeight) / 3 - fontManager.getFontMap().get(FONT).getHeight("W")) / 2);

        posXCharacsAgilityLabel = posXCharacsFrame + ((characsFrameLength / 6 - fontManager.getFontMap().get(FONT).getWidth("A")) / 2);
        posYCharacsAgilityLabel = posYCharacsFrame + ((2 * characsFrameHeight) / 3) + (((characsFrameHeight) / 3 - fontManager.getFontMap().get(FONT).getWidth("A")) / 2);

        posXCharacsMoveLabel = posXCharacsFrame + (characsFrameLength / 2) + ((characsFrameLength / 6 - fontManager.getFontMap().get(FONT).getWidth("M")) / 2);
        posYCharacsMoveLabel = posYCharacsFrame + ((2 * characsFrameHeight) / 3) + (((characsFrameHeight) / 3 - fontManager.getFontMap().get(FONT).getHeight("M")) / 2);
        // VALUES
        posXCharacsStrength = posXCharacsFrame + (characsFrameLength / 6)
                + ((characsFrameLength / 3 - fontManager.getFontMap().get(FONT).getWidth(String.valueOf(character.getStrength()))) / 2);
        posYCharacsStrength = posYCharacsFrame
                + ((characsFrameHeight / 3 - fontManager.getFontMap().get(FONT).getHeight(String.valueOf(character.getStrength()))) / 2);

        posXCharacsEndurance = posXCharacsFrame + (characsFrameLength / 2) + (characsFrameLength / 6)
                + ((characsFrameLength / 3 - fontManager.getFontMap().get(FONT).getWidth(String.valueOf(character.getEndurance()))) / 2);
        posYCharacsEndurance = posYCharacsFrame
                + ((characsFrameHeight / 3 - fontManager.getFontMap().get(FONT).getHeight(String.valueOf(character.getEndurance()))) / 2);

        posXCharacsIntelligence = posXCharacsFrame + (characsFrameLength / 6)
                + ((characsFrameLength / 3 - fontManager.getFontMap().get(FONT).getWidth(String.valueOf(character.getIntelligence()))) / 2);
        posYCharacsIntelligence = posYCharacsFrame + (characsFrameHeight / 3)
                + (((characsFrameHeight) / 3 - fontManager.getFontMap().get(FONT).getHeight(String.valueOf(character.getIntelligence()))) / 2);

        posXCharacsWill = posXCharacsFrame + (characsFrameLength / 2) + (characsFrameLength / 6)
                + ((characsFrameLength / 3 - fontManager.getFontMap().get(FONT).getWidth(String.valueOf(character.getWill()))) / 2);
        posYCharacsWill = posYCharacsFrame + (characsFrameHeight / 3)
                + (((characsFrameHeight) / 3 - fontManager.getFontMap().get(FONT).getHeight(String.valueOf(character.getWill()))) / 2);

        posXCharacsAgility = posXCharacsFrame + (characsFrameLength / 6)
                + ((characsFrameLength / 3 - fontManager.getFontMap().get(FONT).getWidth(String.valueOf(character.getAgility()))) / 2);
        posYCharacsAgility = posYCharacsFrame + ((2 * characsFrameHeight) / 3)
                + (((characsFrameHeight) / 3 - fontManager.getFontMap().get(FONT).getHeight(String.valueOf(character.getAgility()))) / 2);

        posXCharacsMove = posXCharacsFrame + (characsFrameLength / 2) + (characsFrameLength / 6)
                + ((characsFrameLength / 3 - fontManager.getFontMap().get(FONT).getWidth(String.valueOf(character.getMovement()))) / 2);
        posYCharacsMove = posYCharacsFrame + ((2 * characsFrameHeight) / 3)
                + (((characsFrameHeight) / 3 - fontManager.getFontMap().get(FONT).getHeight(String.valueOf(character.getMovement()))) / 2);

        //Job Frame

        posXJobFrame = posX;
        posYJobFrame = posYBarsFrame + barsFrameHeight - frameWidth;

        jobFrameLength = frameLength;
        jobFrameHeight = (1 * frameHeight / 5) + frameWidth;

        posXJob = posXJobFrame + ((frameLength / 2) - fontManager.getFontMap().get(FONT).getWidth(character.getJob(character.getCurrentJob()).getName())) / 2;
        posYJob = posYJobFrame + (jobFrameHeight - fontManager.getFontMap().get(FONT).getHeight(character.getJob(character.getCurrentJob()).getName())) / 2;

        posXJobPoints = posXJobFrame + (frameLength / 2) + ((3 * frameLength) / 10) + (((2 * frameLength) / 10) - fontManager.getFontMap().get(FONT).getWidth("JP : " + character.getJobPoints())) / 2;
        posYJobPoints = posYJobFrame + (nameFrameHeight - fontManager.getFontMap().get(FONT).getHeight("JP : " + character.getJobPoints())) / 2;
    }

    public void render(Color color) {

        Toolbox.drawFrame(posX, posY, nameFrameLength, nameFrameHeight, frameWidth, color);
        fontManager.drawString(FONT, posXName, posYName, character.getName(), Color.white);
        fontManager.drawString(FONT, posXClan, posYClan, character.getClan().getName(), Color.white);
        fontManager.drawString(FONT, posXLevel, posYLevel, "LvL : " + character.getLevel(), Color.white);

        Toolbox.drawFrame(posXBarsFrame, posYBarsFrame, barsFrameLength, barsFrameHeight, frameWidth, color);
        fontManager.drawString(FONT, posXLabel1, posYLabel1, "HP", Color.white);
        fontManager.drawString(FONT, posXLabel2, posYLabel2, "MP", Color.white);
        fontManager.drawString(FONT, posXLabel3, posYLabel3, "XP", Color.white);

        Toolbox.drawFrame(posXCharacsFrame, posYCharacsFrame, characsFrameLength, characsFrameHeight, frameWidth, color);
        fontManager.drawString(FONT, posXCharacsStrengthLabel, posYCharacsStrengthLabel, "S", Color.white);
        fontManager.drawString(FONT, posXCharacsEnduranceLabel, posYCharacsEnduranceLabel, "E", Color.white);
        fontManager.drawString(FONT, posXCharacsIntelligenceLabel, posYCharacsIntelligenceLabel, "I", Color.white);
        fontManager.drawString(FONT, posXCharacsWillLabel, posYCharacsWillLabel, "W", Color.white);
        fontManager.drawString(FONT, posXCharacsAgilityLabel, posYCharacsAgilityLabel, "A", Color.white);
        fontManager.drawString(FONT, posXCharacsMoveLabel, posYCharacsMoveLabel, "M", Color.white);

        fontManager.drawString(FONT, posXCharacsStrength, posYCharacsStrength, String.valueOf(character.getStrength()), Color.white);
        fontManager.drawString(FONT, posXCharacsEndurance, posYCharacsEndurance, String.valueOf(character.getEndurance()), Color.white);
        fontManager.drawString(FONT, posXCharacsIntelligence, posYCharacsIntelligence, String.valueOf(character.getIntelligence()), Color.white);
        fontManager.drawString(FONT, posXCharacsWill, posYCharacsWill, String.valueOf(character.getWill()), Color.white);
        fontManager.drawString(FONT, posXCharacsAgility, posYCharacsAgility, String.valueOf(character.getIntelligence()), Color.white);
        fontManager.drawString(FONT, posXCharacsMove, posYCharacsMove, String.valueOf(character.getMovement()), Color.white);

        Toolbox.drawFrame(posXJobFrame, posYJobFrame, jobFrameLength, jobFrameHeight, frameWidth, color);
        fontManager.drawString(FONT, posXJob, posYJob, character.getJob(character.getCurrentJob()).getName(), Color.white);
        fontManager.drawString(FONT, posXJobPoints, posYJobPoints, "JP : " + character.getJobPoints(), Color.white);

        experienceBar.render();
        lifeBar.render();
        manaBar.render();
    }
}
