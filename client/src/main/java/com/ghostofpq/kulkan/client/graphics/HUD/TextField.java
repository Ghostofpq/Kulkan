package com.ghostofpq.kulkan.client.graphics.HUD;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TextField extends HUDLabelledElement {
    protected int maxLength;

    public TextField(int posX, int posY, int length, int height, int maxLength) {
        this.posX = posX;
        this.posY = posY;
        this.width = length;
        this.height = height;
        this.maxLength = maxLength;
        this.hasFocus = false;
        this.fontName = "optimus_princeps_16";
        this.label = "";
        this.alignment = TextAlignment.LEFT;
        updateTextPosition();
    }

    public TextField(int posX, int posY, int length, int height, int maxLength, String fontName) {
        this.posX = posX;
        this.posY = posY;
        this.width = length;
        this.height = height;
        this.maxLength = maxLength;
        this.hasFocus = false;
        this.fontName = fontName;
        this.label = "";
        this.alignment = TextAlignment.LEFT;
        updateTextPosition();
    }

    @Override
    public void draw() {
        drawLabel();
    }

    public void writeChar(char c) {
        if (Character.isLetterOrDigit(c) || Character.isSpaceChar(c)) {
            if (label.length() < maxLength) {
                label += c;
                updateTextPosition();
            }
        }
    }

    public void deleteLastChar() {
        if (!label.isEmpty()) {
            label = label.substring(0, label.length() - 1);
            updateTextPosition();
        }
    }

    public void clear() {
        label = "";
        updateTextPosition();
    }
}
