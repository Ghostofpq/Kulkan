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
    // Characs AttackDamage Label Position
    private int posXCharacsAttackDamageLabel;
    private int posYCharacsAttackDamageLabel;
    // Characs getArmor Label Position
    private int posXCharacsgetArmorLabel;
    private int posYCharacsgetArmorLabel;
    // Characs MagicalDamage Label Position
    private int posXCharacsMagicalDamageLabel;
    private int posYCharacsMagicalDamageLabel;
    // Characs MagicResist Label Position
    private int posXCharacsMagicResistLabel;
    private int posYCharacsMagicResistLabel;
    // Characs Speed Label Position
    private int posXCharacsSpeedLabel;
    private int posYCharacsSpeedLabel;
    // Characs Move Label Position
    private int posXCharacsMoveLabel;
    private int posYCharacsMoveLabel;
    // Characs AttackDamage Position
    private int posXCharacsAttackDamage;
    private int posYCharacsAttackDamage;
    // Characs getArmor Position
    private int posXCharacsgetArmor;
    private int posYCharacsgetArmor;
    // Characs MagicalDamage Position
    private int posXCharacsMagicalDamage;
    private int posYCharacsMagicalDamage;
    // Characs MagicResist Position
    private int posXCharacsMagicResist;
    private int posYCharacsMagicResist;
    // Characs Speed Position
    private int posXCharacsSpeed;
    private int posYCharacsSpeed;
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
        posXCharacsAttackDamageLabel = posXCharacsFrame + ((characsFrameLength / 6 - fontManager.getFontMap().get(FONT).getWidth("S")) / 2);
        posYCharacsAttackDamageLabel = posYCharacsFrame + ((characsFrameHeight / 3 - fontManager.getFontMap().get(FONT).getHeight("S")) / 2);

        posXCharacsgetArmorLabel = posXCharacsFrame + (characsFrameLength / 2) + ((characsFrameLength / 6 - fontManager.getFontMap().get(FONT).getWidth("E")) / 2);
        posYCharacsgetArmorLabel = posYCharacsFrame + ((characsFrameHeight / 3 - fontManager.getFontMap().get(FONT).getHeight("E")) / 2);

        posXCharacsMagicalDamageLabel = posXCharacsFrame + ((characsFrameLength / 6 - fontManager.getFontMap().get(FONT).getWidth("I")) / 2);
        posYCharacsMagicalDamageLabel = posYCharacsFrame + (characsFrameHeight / 3) + (((characsFrameHeight) / 3 - fontManager.getFontMap().get(FONT).getHeight("I")) / 2);

        posXCharacsMagicResistLabel = posXCharacsFrame + (characsFrameLength / 2) + ((characsFrameLength / 6 - fontManager.getFontMap().get(FONT).getWidth("W")) / 2);
        posYCharacsMagicResistLabel = posYCharacsFrame + (characsFrameHeight / 3) + (((characsFrameHeight) / 3 - fontManager.getFontMap().get(FONT).getHeight("W")) / 2);

        posXCharacsSpeedLabel = posXCharacsFrame + ((characsFrameLength / 6 - fontManager.getFontMap().get(FONT).getWidth("A")) / 2);
        posYCharacsSpeedLabel = posYCharacsFrame + ((2 * characsFrameHeight) / 3) + (((characsFrameHeight) / 3 - fontManager.getFontMap().get(FONT).getWidth("A")) / 2);

        posXCharacsMoveLabel = posXCharacsFrame + (characsFrameLength / 2) + ((characsFrameLength / 6 - fontManager.getFontMap().get(FONT).getWidth("M")) / 2);
        posYCharacsMoveLabel = posYCharacsFrame + ((2 * characsFrameHeight) / 3) + (((characsFrameHeight) / 3 - fontManager.getFontMap().get(FONT).getHeight("M")) / 2);
        // VALUES
        posXCharacsAttackDamage = posXCharacsFrame + (characsFrameLength / 6)
                + ((characsFrameLength / 3 - fontManager.getFontMap().get(FONT).getWidth(String.valueOf(character.getAttackDamage()))) / 2);
        posYCharacsAttackDamage = posYCharacsFrame
                + ((characsFrameHeight / 3 - fontManager.getFontMap().get(FONT).getHeight(String.valueOf(character.getAttackDamage()))) / 2);

        posXCharacsgetArmor = posXCharacsFrame + (characsFrameLength / 2) + (characsFrameLength / 6)
                + ((characsFrameLength / 3 - fontManager.getFontMap().get(FONT).getWidth(String.valueOf(character.getArmor()))) / 2);
        posYCharacsgetArmor = posYCharacsFrame
                + ((characsFrameHeight / 3 - fontManager.getFontMap().get(FONT).getHeight(String.valueOf(character.getArmor()))) / 2);

        posXCharacsMagicalDamage = posXCharacsFrame + (characsFrameLength / 6)
                + ((characsFrameLength / 3 - fontManager.getFontMap().get(FONT).getWidth(String.valueOf(character.getMagicalDamage()))) / 2);
        posYCharacsMagicalDamage = posYCharacsFrame + (characsFrameHeight / 3)
                + (((characsFrameHeight) / 3 - fontManager.getFontMap().get(FONT).getHeight(String.valueOf(character.getMagicalDamage()))) / 2);

        posXCharacsMagicResist = posXCharacsFrame + (characsFrameLength / 2) + (characsFrameLength / 6)
                + ((characsFrameLength / 3 - fontManager.getFontMap().get(FONT).getWidth(String.valueOf(character.getMagicResist()))) / 2);
        posYCharacsMagicResist = posYCharacsFrame + (characsFrameHeight / 3)
                + (((characsFrameHeight) / 3 - fontManager.getFontMap().get(FONT).getHeight(String.valueOf(character.getMagicResist()))) / 2);

        posXCharacsSpeed = posXCharacsFrame + (characsFrameLength / 6)
                + ((characsFrameLength / 3 - fontManager.getFontMap().get(FONT).getWidth(String.valueOf(character.getSpeed()))) / 2);
        posYCharacsSpeed = posYCharacsFrame + ((2 * characsFrameHeight) / 3)
                + (((characsFrameHeight) / 3 - fontManager.getFontMap().get(FONT).getHeight(String.valueOf(character.getSpeed()))) / 2);

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
        fontManager.drawString(FONT, posXCharacsAttackDamageLabel, posYCharacsAttackDamageLabel, "S", Color.white);
        fontManager.drawString(FONT, posXCharacsgetArmorLabel, posYCharacsgetArmorLabel, "E", Color.white);
        fontManager.drawString(FONT, posXCharacsMagicalDamageLabel, posYCharacsMagicalDamageLabel, "I", Color.white);
        fontManager.drawString(FONT, posXCharacsMagicResistLabel, posYCharacsMagicResistLabel, "W", Color.white);
        fontManager.drawString(FONT, posXCharacsSpeedLabel, posYCharacsSpeedLabel, "A", Color.white);
        fontManager.drawString(FONT, posXCharacsMoveLabel, posYCharacsMoveLabel, "M", Color.white);

        fontManager.drawString(FONT, posXCharacsAttackDamage, posYCharacsAttackDamage, String.valueOf(character.getAttackDamage()), Color.white);
        fontManager.drawString(FONT, posXCharacsgetArmor, posYCharacsgetArmor, String.valueOf(character.getArmor()), Color.white);
        fontManager.drawString(FONT, posXCharacsMagicalDamage, posYCharacsMagicalDamage, String.valueOf(character.getMagicalDamage()), Color.white);
        fontManager.drawString(FONT, posXCharacsMagicResist, posYCharacsMagicResist, String.valueOf(character.getMagicResist()), Color.white);
        fontManager.drawString(FONT, posXCharacsSpeed, posYCharacsSpeed, String.valueOf(character.getMagicalDamage()), Color.white);
        fontManager.drawString(FONT, posXCharacsMove, posYCharacsMove, String.valueOf(character.getMovement()), Color.white);

        Toolbox.drawFrame(posXJobFrame, posYJobFrame, jobFrameLength, jobFrameHeight, frameWidth, color);
        fontManager.drawString(FONT, posXJob, posYJob, character.getJob(character.getCurrentJob()).getName(), Color.white);
        fontManager.drawString(FONT, posXJobPoints, posYJobPoints, "JP : " + character.getJobPoints(), Color.white);

        experienceBar.render();
        lifeBar.render();
        manaBar.render();
    }
}
