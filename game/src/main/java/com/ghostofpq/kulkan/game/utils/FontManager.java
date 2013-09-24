package com.ghostofpq.kulkan.game.utils;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;

import java.util.HashMap;

public class FontManager {

    private static FontManager instance = new FontManager();
    private HashMap<String, AngelCodeFont> fontMap;

    private FontManager() {
        fontMap = new HashMap<String, AngelCodeFont>();

        try {
            fontMap.put("arial_16", new AngelCodeFont("./resources/font/Arial/arial_16_white.fnt", "./resources/font/Arial/arial_16_white.png"));
            fontMap.put("arial_12", new AngelCodeFont("./resources/font/Arial/arial_12_white.fnt", "./resources/font/Arial/arial_12_white.png"));
            fontMap.put("optimus_princeps_16", new AngelCodeFont("./resources/font/optimus_princeps/optimus_princeps_16.fnt", "./resources/font/optimus_princeps/optimus_princeps_16.png"));
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public static FontManager getInstance() {
        if (instance == null) {
            synchronized (FontManager.class) {
                if (instance == null) {
                    instance = new FontManager();
                }
            }
        }
        return instance;
    }

    public void drawString(String font, int x, int y, String str, Color color) {
        GL11.glEnable(GL11.GL_BLEND);
        fontMap.get(font).drawString(x, y, str, color);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void drawString(String font, int size, int x, int y, String str, Color color) {
        GL11.glEnable(GL11.GL_BLEND);
        fontMap.get(font + "_" + size).drawString(x, y, str, color);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Getters and Setters
     */

    public HashMap<String, AngelCodeFont> getFontMap() {
        return fontMap;
    }
}
