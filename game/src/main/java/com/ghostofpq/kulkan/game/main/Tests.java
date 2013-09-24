package com.ghostofpq.kulkan.game.main;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.SlickException;

public class Tests {
    public AngelCodeFont font;

    public Tests() {
        try {
            Display.setDisplayMode(new DisplayMode(800, 600));
            Display.create();

            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glOrtho(0, 800, 600, 0, 1, -1);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
        try {
            font = new AngelCodeFont("resources/font/Arial/arial_16_white.fnt", "resources/font/Arial/arial_16_white.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }


    public void start() {
        while (!Display.isCloseRequested()) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            Display.sync(60);

            renderText();

            Display.update();
        }

        Display.destroy();

    }

    private void renderText() {
        font.drawString(10, 200, "this should render");
    }

    public static void main(String[] argv) {
        new Tests().start();
    }
}
