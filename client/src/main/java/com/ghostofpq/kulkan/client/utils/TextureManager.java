package com.ghostofpq.kulkan.client.utils;

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
            Texture grass = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("resources/textures/Grass.PNG"));
            Texture earth = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("resources/textures/Earth.PNG"));

            Texture highlightB = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("resources/textures/HighlightB.PNG"));
            Texture highlightG = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("resources/textures/HighlightG.PNG"));
            Texture highlightR = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("resources/textures/HighlightR.PNG"));

            Texture textfieldNoFocus = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("resources/HUD/field_not_selected.png"));
            Texture textfieldFocus = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("resources/HUD/field_selected.png"));

            Texture loginBackground = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("resources/HUD/login_background.jpg"));

            textureMap.put(TextureKey.GRASS, grass);
            textureMap.put(TextureKey.EARTH, earth);

            textureMap.put(TextureKey.HIGHLIGHT_BLUE, highlightB);
            textureMap.put(TextureKey.HIGHLIGHT_GREEN, highlightG);
            textureMap.put(TextureKey.HIGHLIGHT_RED, highlightR);


            textureMap.put(TextureKey.TEXT_FIELD_NO_FOCUS, textfieldNoFocus);
            textureMap.put(TextureKey.TEXT_FIELD_FOCUS, textfieldFocus);

            textureMap.put(TextureKey.LOGIN_BACKGROUND, loginBackground);

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
