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
            Texture grass = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/textures/Grass.PNG"));
            Texture earth = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/textures/Earth.PNG"));

            Texture highlightB = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/textures/HighlightB.PNG"));
            Texture highlightG = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/textures/HighlightG.PNG"));
            Texture highlightR = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/textures/HighlightR.PNG"));

            Texture textfieldNoFocus = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/HUD/field_not_selected.png"));
            Texture textfieldFocus = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/HUD/field_selected.png"));

            Texture buttonFocus = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/HUD/homepage_800-600-button-rollover.png"));
            Texture buttonNoFocus = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/HUD/homepage_800-600-button-basic.png"));

            Texture loginBackground = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("graphics/HUD/login_background.jpg"));
            Texture backgroundBasic = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("graphics/HUD/background_basic.jpg"));

            Texture frameExt = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/HUD/Common/COMMON_ExtFrame.png"));
            textureMap.put(TextureKey.COMMON_EXT_FRAME, frameExt);
            Texture background = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("graphics/HUD/Common/COMMON_Background.jpg"));
            textureMap.put(TextureKey.COMMON_BACKGROUND_BASIC, background);


            Texture action1 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/HUD/action.png"));
            textureMap.put(TextureKey.ACTION_1, action1);

            Texture action1Back = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/HUD/actionBack.png"));
            textureMap.put(TextureKey.ACTION_1_BACK, action1Back);

            Texture action1Hovered = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/HUD/actionHovered.png"));
            textureMap.put(TextureKey.ACTION_1_HOVERED, action1Hovered);

            Texture action1Used = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/HUD/actionUsed.png"));
            textureMap.put(TextureKey.ACTION_1_USED, action1Used);

            Texture blood = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("graphics/HUD/Common/COMMON_Blood.jpg"));
            textureMap.put(TextureKey.COMMON_BLOOD, blood);


            // LOBBY VIEW
            Texture lobbyBackground = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("graphics/HUD/Lobby/LOBBY_Background_169.jpg"));
            textureMap.put(TextureKey.LOBBY_BACKGROUND_169, lobbyBackground);

            Texture lobbyChatOverlay = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/HUD/Lobby/LOBBY_Chat-overlay.png"));
            textureMap.put(TextureKey.LOBBY_CHAT_OVERLAY, lobbyChatOverlay);

            Texture lobbyNewsOverlay = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/HUD/Lobby/LOBBY_News-overlay.png"));
            textureMap.put(TextureKey.LOBBY_NEWS_OVERLAY, lobbyNewsOverlay);

            Texture charShadow = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/HUD/Common/COMMON_Character-shadow.png"));
            textureMap.put(TextureKey.COMMON_CHAR_SHADOW, charShadow);

            // MANAGE CHAR VIEW
            Texture manageCharMenu = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("graphics/HUD/ManageChar/MANAGE-CHAR_Menu.jpg"));
            textureMap.put(TextureKey.MANAGE_CHAR_MENU, manageCharMenu);

            Texture manageCharNameHolder = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/HUD/ManageChar/MANAGE-CHAR_Name-holder.png"));
            textureMap.put(TextureKey.MANAGE_CHAR_NAME_HOLDER, manageCharNameHolder);

            Texture manageCharXpBackground = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/HUD/ManageChar/MANAGE-CHAR_XP-background.png"));
            textureMap.put(TextureKey.MANAGE_CHAR_XP_BACKGROUND, manageCharXpBackground);

            Texture earthTop00 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/textures/EARTH/TOP/earth_top_00.png"));
            Texture earthTop01 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/textures/EARTH/TOP/earth_top_01.png"));
            Texture earthTop02 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/textures/EARTH/TOP/earth_top_02.png"));
            Texture earthTop03 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/textures/EARTH/TOP/earth_top_03.png"));
            Texture earthTop04 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/textures/EARTH/TOP/earth_top_04.png"));
            Texture earthTop05 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/textures/EARTH/TOP/earth_top_05.png"));
            Texture earthTop06 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/textures/EARTH/TOP/earth_top_06.png"));
            Texture earthTop07 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/textures/EARTH/TOP/earth_top_07.png"));
            Texture earthTop08 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/textures/EARTH/TOP/earth_top_08.png"));
            Texture earthTop09 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/textures/EARTH/TOP/earth_top_09.png"));

            Texture earthSide00 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/textures/EARTH/SIDE/earth_side_00.png"));
            Texture earthSide01 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/textures/EARTH/SIDE/earth_side_01.png"));
            Texture earthSide02 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/textures/EARTH/SIDE/earth_side_02.png"));
            Texture earthSide03 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("graphics/textures/EARTH/SIDE/earth_side_03.png"));


            textureMap.put(TextureKey.GRASS, grass);
            textureMap.put(TextureKey.EARTH, earth);

            textureMap.put(TextureKey.HIGHLIGHT_BLUE, highlightB);
            textureMap.put(TextureKey.HIGHLIGHT_GREEN, highlightG);
            textureMap.put(TextureKey.HIGHLIGHT_RED, highlightR);


            textureMap.put(TextureKey.BUTTON_NO_FOCUS, buttonNoFocus);
            textureMap.put(TextureKey.BUTTON_FOCUS, buttonFocus);

            textureMap.put(TextureKey.TEXT_FIELD_NO_FOCUS, textfieldNoFocus);
            textureMap.put(TextureKey.TEXT_FIELD_FOCUS, textfieldFocus);

            textureMap.put(TextureKey.LOGIN_BACKGROUND, loginBackground);
            textureMap.put(TextureKey.BACKGROUND_BASIC, backgroundBasic);


            textureMap.put(TextureKey.EARTH_TOP_00, earthTop00);
            textureMap.put(TextureKey.EARTH_TOP_01, earthTop01);
            textureMap.put(TextureKey.EARTH_TOP_02, earthTop02);
            textureMap.put(TextureKey.EARTH_TOP_03, earthTop03);
            textureMap.put(TextureKey.EARTH_TOP_04, earthTop04);
            textureMap.put(TextureKey.EARTH_TOP_05, earthTop05);
            textureMap.put(TextureKey.EARTH_TOP_06, earthTop06);
            textureMap.put(TextureKey.EARTH_TOP_07, earthTop07);
            textureMap.put(TextureKey.EARTH_TOP_08, earthTop08);
            textureMap.put(TextureKey.EARTH_TOP_09, earthTop09);

            textureMap.put(TextureKey.EARTH_SIDE_00, earthSide00);
            textureMap.put(TextureKey.EARTH_SIDE_01, earthSide01);
            textureMap.put(TextureKey.EARTH_SIDE_02, earthSide02);
            textureMap.put(TextureKey.EARTH_SIDE_03, earthSide03);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        instance = new TextureManager();
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
