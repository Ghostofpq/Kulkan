package com.ghostofpq.kulkan.game.graphics;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;

public class MenuElementSelect {
    private float posX;
    private float posY;
    private float offset;
    private String text;
    private boolean placed;
    private AngelCodeFont font;

    public MenuElementSelect(float posX, float posY, String text, AngelCodeFont font) {
        this.posX = posX;
        this.posY = posY;
        this.offset = posX + font.getWidth(text);

        this.text = text;
        this.font = font;

        this.placed = false;
    }

    public void render() {
        this.font.drawString(posX - offset, posY, text);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public void render(Color color) {
        this.font.drawString(posX - offset, posY, text, color);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public void addOffset(float delta) {
        this.offset += delta;
        if (offset <= 0) {
            this.placed = true;
        }
    }

    public boolean isOutOfScreenLeft() {
        return ((posX - offset + font.getWidth(text)) <= 0);
    }

    public boolean isOutOfScreenRight(float width) {
        return ((posX - offset) >= width);
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

    public float getOffset() {
        return offset;
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

    public AngelCodeFont getFont() {
        return font;
    }
}
