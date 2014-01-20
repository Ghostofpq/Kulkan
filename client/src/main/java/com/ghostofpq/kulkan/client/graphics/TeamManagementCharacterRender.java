package com.ghostofpq.kulkan.client.graphics;

import com.ghostofpq.kulkan.client.utils.FontManager;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import org.newdawn.slick.Color;
import org.springframework.beans.factory.annotation.Autowired;

public class TeamManagementCharacterRender extends HUDElement {
    private final String FONT = "optimus_princeps_16";
    private GameCharacter character;
    private int frameWidth;
    // Name
    private int namePosX;
    private int namePosY;
    // Lvl
    private int lvlPosX;
    private int lvlPosY;
    // Clan
    private int racePosX;
    private int racePosY;
    // xp
    private int xpPosX;
    private int xpPosY;
    @Autowired
    private FontManager fontManager;

    public TeamManagementCharacterRender(float posX, float posY, int width, int height, int frameWidth, GameCharacter character) {
        this.posX = (int) posX;
        this.posY = (int) posY;
        this.character = character;

        this.width = width;
        this.height = height;
        this.frameWidth = frameWidth;

        calculatePositions();
    }

    private void calculatePositions() {
        namePosX = posX + frameWidth;
        lvlPosX = posX + frameWidth;
        racePosX = posX + frameWidth;
        xpPosX = posX + frameWidth;

        namePosY = posY + 10;
        lvlPosY = posY + 30;
        racePosY = posY + 50;
        xpPosY = posY + 70;
    }

    @Override
    public void draw() {
        Toolbox.drawFrame(posX, posY, width, height, frameWidth, Color.white);
        fontManager.drawString(FONT, namePosX, namePosY, character.getName(), Color.white);
        fontManager.drawString(FONT, lvlPosX, lvlPosY, "LvL : " + character.getLevel(), Color.white);
        fontManager.drawString(FONT, racePosX, racePosY, character.getClan().getName(), Color.white);
        fontManager.drawString(FONT, xpPosX, xpPosY, String.valueOf(character.getExperience()), Color.white);
    }

    public GameCharacter getCharacter() {
        return character;
    }
}
