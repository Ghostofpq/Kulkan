package com.ghostofpq.kulkan.game.graphics;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class TextField {
    private float posX;
    private float posY;
    private String text;
    private boolean placed;
    private UnicodeFont font;

    public TextField(float posX, float posY, String text) {

        try {
            font = new UnicodeFont("resources/font/old_london/OldLondon.ttf", 24, false, false);
            font.addAsciiGlyphs();
            font.getEffects().add(new ColorEffect());
            font.loadGlyphs();

        } catch (SlickException e) {
            e.printStackTrace();
        }

        this.posX = posX;
        this.posY = posY;

        this.text = text;
        this.font = font;

        this.placed = false;
    }

    public void render() {
        this.font.drawString(posX, posY, text);
    }

    public void render(Color color) {
        this.font.drawString(posX, posY, text, color);
    }

    /**
     * Getters and Setters
     */

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isPlaced() {
        return placed;
    }

    public UnicodeFont getFont() {
        return font;
    }
}
