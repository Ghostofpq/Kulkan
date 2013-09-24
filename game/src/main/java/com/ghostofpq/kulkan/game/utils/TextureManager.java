package com.ghostofpq.kulkan.game.utils;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.util.HashMap;

public class TextureManager {

    private static TextureManager instance = new TextureManager();
    private HashMap<TextureKey, Texture> textureMap;

    private TextureManager() {
        textureMap = new HashMap<TextureKey, Texture>();

        try {
            Texture grass = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("./resources/textures/Grass.png"));
            Texture earth = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("./resources/textures/Earth.png"));

            Texture highlightB = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("./resources/textures/HighlightB.png"));
            Texture highlightG = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("./resources/textures/HighlightG.png"));
            Texture highlightR = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("./resources/textures/HighlightR.png"));
            Texture charac = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("./resources/textures/perso1.png"));
            textureMap.put(TextureKey.GRASS, grass);
            textureMap.put(TextureKey.EARTH, earth);

            textureMap.put(TextureKey.HIGHLIGHT_BLUE, highlightB);
            textureMap.put(TextureKey.HIGHLIGHT_GREEN, highlightG);
            textureMap.put(TextureKey.HIGHLIGHT_RED, highlightR);

            textureMap.put(TextureKey.CHAR, charac);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TextureManager getInstance() {
        if (instance == null) {
            synchronized (TextureManager.class) {
                if (instance == null) {
                    instance = new TextureManager();
                }
            }
        }
        return instance;
    }

    public Texture getTexture(TextureKey texture) {
        return textureMap.get(texture);
    }

    /**
     * Getters and Setters
     */

    public HashMap<TextureKey, Texture> getTextureMap() {
        return textureMap;
    }
}
