package com.ghostofpq.kulkan.client.graphics;

import com.ghostofpq.kulkan.client.utils.FontManager;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;

public class BarRender {
    private Color colorBack;
    private Color color;
    private float posXBegin;
    private float posYUp;
    private float posXEnd;
    private float posYDown;
    private float posXSeparation;
    private float posYSeparation;
    private float posX4;
    private float posY4;
    private String text;
    private FontManager fontManager = FontManager.getInstance();

    public BarRender(float value, float maxValue, float posX, float posY, float width, float height, Color color, Color colorBack) {
        this.posXBegin = posX;
        this.posYUp = posY;
        this.posXEnd = posX + width;
        this.posYDown = posY + height;
        this.posXSeparation = posX + (width * (value / maxValue));

        StringBuilder builder = new StringBuilder();
        builder.append((int) value);
        builder.append(" / ");
        builder.append((int) maxValue);
        text = builder.toString();

        AngelCodeFont font = fontManager.getFontMap().get("optimus_princeps_16");

        this.posX4 = posX + ((width - font.getWidth(text)) / 2);
        this.posY4 = posY + ((height - font.getHeight(text)) / 2);
        this.color = color;
        this.colorBack = colorBack;
    }

    public void render() {
        GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), 1f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(posXBegin, posYUp);
        GL11.glVertex2f(posXSeparation, posYUp);
        GL11.glVertex2f(posXSeparation, posYDown);
        GL11.glVertex2f(posXBegin, posYDown);
        GL11.glEnd();

        GL11.glColor4f(colorBack.getRed(), colorBack.getGreen(), colorBack.getBlue(), 1f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(posXSeparation, posYUp);
        GL11.glVertex2f(posXEnd, posYUp);
        GL11.glVertex2f(posXEnd, posYDown);
        GL11.glVertex2f(posXSeparation, posYDown);
        GL11.glEnd();

        GL11.glColor4f(0, 0, 0, 1);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2f(posXSeparation, posYUp);
        GL11.glVertex2f(posXSeparation, posYDown);
        GL11.glEnd();

        fontManager.drawString("optimus_princeps_16", (int) posX4, (int) posY4, text, Color.black);
    }
}
