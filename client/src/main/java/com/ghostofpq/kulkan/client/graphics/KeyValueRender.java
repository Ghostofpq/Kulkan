package com.ghostofpq.kulkan.client.graphics;

import com.ghostofpq.kulkan.client.graphics.HUD.HUDElement;
import com.ghostofpq.kulkan.client.graphics.HUD.TextAlignment;
import com.ghostofpq.kulkan.client.graphics.HUD.TextZone;

public class KeyValueRender extends HUDElement {
    private TextZone key;
    private TextZone value;

    public KeyValueRender(int posX, int posY, int width, int height, String key, String value, int ratio) {
        this.posX = posX;
        this.posY = posY;

        this.width = width;
        this.height = height;

        int keyWidth = ratio * width / 10;

        this.key = new TextZone(posX, posY, keyWidth, height, key);
        this.key.setAlignment(TextAlignment.LEFT);

        int valuePosX = posX + keyWidth;
        int valueWidth = width - keyWidth;

        this.value = new TextZone(valuePosX, posY, valueWidth, height, value);
        this.value.setAlignment(TextAlignment.LEFT);
    }

    @Override
    public void draw() {
        this.key.draw();
        this.value.draw();
    }

    public TextZone getKey() {
        return key;
    }

    public TextZone getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value.setLabel(value);
    }

    public void setFont(String font) {
        this.value.setFontName(font);
        this.key.setFontName(font);
    }
}
