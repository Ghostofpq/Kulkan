package com.ghostofpq.kulkan.game.graphics;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public class Toolbox {

    public static void drawIcon(float X, float Y, float width, float height, Texture icon) {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1f, 0.5f, 1f, 1f);
        icon.bind();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex2f(X, Y);
        GL11.glTexCoord2d(1, 0);
        GL11.glVertex2f(X + width, Y);
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex2f(X + width, Y + height);
        GL11.glTexCoord2d(0, 1);
        GL11.glVertex2f(X, Y + height);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public static void drawFrame(float X, float Y, float length, float height, float width, Color color) {

        GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), 1f);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(X, Y);
        GL11.glVertex2f(X + length, Y);
        GL11.glVertex2f(X + length, Y + height);
        GL11.glVertex2f(X, Y + height);
        GL11.glEnd();

        GL11.glColor4f(0f, 0f, 0f, 1f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(X + width, Y + width);
        GL11.glVertex2f(X + length - width, Y + width);
        GL11.glVertex2f(X + length - width, Y + height - width);
        GL11.glVertex2f(X + width, Y + height - width);
        GL11.glEnd();
    }
}
