package com.ghostofpq.kulkan.client.utils;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import java.util.HashMap;
import java.util.Map;

public class SpritesheetManager {

    private static SpritesheetManager instance = new SpritesheetManager();
    private Map<String, SpriteSheet> spriteSheetMap;

    private SpritesheetManager() {
        spriteSheetMap = new HashMap<String, SpriteSheet>();
        try {
            SpriteSheet sheet = new SpriteSheet("./resources/spritesheets/ramza.png", 32, 42);
            SpriteSheet sheet2 = new SpriteSheet("./resources/spritesheets/characters.png", 32, 42, new Color(0, 128, 128));
            spriteSheetMap.put("Ramza", sheet);
            spriteSheetMap.put("Arthur", sheet2);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public static SpritesheetManager getInstance() {
        if (instance == null) {
            synchronized (SpritesheetManager.class) {
                if (instance == null) {
                    instance = new SpritesheetManager();
                }
            }
        }
        return instance;
    }

    public SpriteSheet getSpriteSheet(String key) {
        return spriteSheetMap.get(key);
    }

    /**
     * Getters and Setters
     */

    public Map<String, SpriteSheet> getSpriteSheetMap() {
        return spriteSheetMap;
    }
}

