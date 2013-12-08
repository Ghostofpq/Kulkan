package com.ghostofpq.kulkan.client.graphics;

import com.ghostofpq.kulkan.client.utils.FontManager;
import org.newdawn.slick.Color;

public class KeyValueRender extends HUDElement {
    private final String FONT = "optimus_princeps_16";
    private String key;
    private String value;
    // Key Position
    private int posXKey;
    private int posYKey;
    // Value Position
    private int posXValue;
    private int posYValue;

    public KeyValueRender(int posX, int posY, int width, int height, String key, String value, int ratio) {
        this.posX = posX;
        this.posY = posY;

        this.width = width;
        this.height = height;
        this.key = key;
        this.value = value;

        int widthStep = width / 10;
        int fontStep = (height - FontManager.getInstance().getFontMap().get(FONT).getHeight("Testouilles")) / 2;

        posXKey = posX;
        posYKey = posY + fontStep;

        posXValue = posX + ratio * widthStep;
        posYValue = posY + fontStep;
    }

    @Override
    public void draw() {
        FontManager.getInstance().drawString(FONT, posXKey, posYKey, key, Color.white);
        FontManager.getInstance().drawString(FONT, posXValue, posYValue, value, Color.white);
    }

    public void setValue(String value) {
        this.value = value;
    }
}
