package com.ghostofpq.kulkan.client;

import com.ghostofpq.kulkan.entities.character.Player;
import org.lwjgl.opengl.DisplayMode;

import java.util.ArrayList;
import java.util.List;

public class ClientContext {
    // WINDOW
    public static int height;
    public static int width;
    public static DisplayRatio displayRatio;
    private List<DisplayMode> displayModes43 = new ArrayList<DisplayMode>();
    private List<DisplayMode> displayModes169 = new ArrayList<DisplayMode>();
    // USER INFO
    private Player player;
    private String tokenKey;

    public static DisplayRatio getDisplayRatio() {
        return displayRatio;
    }

    public static void setDisplayRatio(DisplayRatio displayRatio) {
        ClientContext.displayRatio = displayRatio;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public List<DisplayMode> getDisplayModes43() {
        return displayModes43;
    }

    public void setDisplayModes43(List<DisplayMode> displayModes43) {
        this.displayModes43 = displayModes43;
    }

    public List<DisplayMode> getDisplayModes169() {
        return displayModes169;
    }

    public void setDisplayModes169(List<DisplayMode> displayModes169) {
        this.displayModes169 = displayModes169;
    }

    public enum DisplayRatio {
        DISPLAY_RATIO_4_3, DISPLAY_RATIO_16_9;
    }
}
