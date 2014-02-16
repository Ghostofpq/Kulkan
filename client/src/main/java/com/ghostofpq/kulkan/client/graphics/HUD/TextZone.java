package com.ghostofpq.kulkan.client.graphics.HUD;

public class TextZone extends HUDLabelledElement {
    private final String FONT = "optimus_princeps_16";

    public TextZone(int posX, int posY, int length, int height, String text) {
        this.posX = posX;
        this.posY = posY;
        this.width = length;
        this.height = height;
        this.hasFocus = false;
        this.alignment = TextAlignment.CENTER;
        this.fontName = FONT;
        this.label = text;
        updateTextPosition();
    }

    @Override
    public void draw() {
        drawLabel();
    }
}
