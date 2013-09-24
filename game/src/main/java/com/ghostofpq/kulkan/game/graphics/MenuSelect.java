package com.ghostofpq.kulkan.game.graphics;

import com.ghostofpq.kulkan.game.utils.FontManager;
import org.newdawn.slick.Color;

import java.util.ArrayList;
import java.util.List;

public class MenuSelect {

    private List<MenuElementSelect> options;
    private int index;
    private MenuState state;
    private float screenHeight;
    private float screenWidth;


    public MenuSelect(List<String> optionString, float startX, float startY, float offsetX, float offsetY, float screenHeight, float screenWidth) {
        float x = startX;
        float y = startY;
        this.options = new ArrayList<MenuElementSelect>();
        for (int i = 0; i < optionString.size(); i++) {
            options.add(new MenuElementSelect(x, y, optionString.get(i), FontManager.getInstance().getFontMap().get("optimus_princeps_16")));
            x += offsetX;
            y += offsetY;
        }
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        state = MenuState.PLACING;
        index = 0;
    }

    public void updatePlacing() {
        boolean allIsPlaced = true;

        for (int i = 0; i < options.size(); i++) {
            if (!options.get(i).isPlaced()) {
                options.get(i).addOffset(-10f);
                allIsPlaced = false;
                break;
            }
        }

        if (allIsPlaced) {
            state = MenuState.STATIC;
        }

    }

    public void updateSplitting() {
        boolean allIsOutOfScreen = true;
        for (int i = 0; i < options.size(); i++) {
            if (i != index) {
                options.get(i).addOffset(10f);
                if (!options.get(i).isOutOfScreenLeft()) {
                    allIsOutOfScreen = false;
                }
            }
        }
        options.get(index).addOffset(-20f);
        if (!options.get(index).isOutOfScreenRight(screenWidth)) {
            allIsOutOfScreen = false;
        }

        if (allIsOutOfScreen) {
            state = MenuState.FINISHED;
        }
    }

    public void indexUp() {
        if (state.equals(MenuState.STATIC)) {
            if (index + 1 == options.size()) {
                index = 0;
            } else {
                index++;
            }
        }
    }

    public void indexDown() {
        if (state.equals(MenuState.STATIC)) {
            if (index == 0) {
                index = options.size() - 1;
            } else {
                index--;
            }
        }
    }

    public void update() {
        switch (state) {
            case PLACING:
                updatePlacing();
                break;
            case STATIC:
                break;
            case SPLITTING:
                updateSplitting();
                break;
            case FINISHED:
                break;
        }
    }

    public void render() {
        for (int i = 0; i < options.size(); i++) {
            options.get(i).render(Color.white);
        }
        options.get(index).render(Color.yellow);
    }

    public void split() {
        if (state.equals(MenuState.STATIC)) {
            state = MenuState.SPLITTING;
        }
    }

    public boolean isFinished() {
        return (state.equals(MenuState.FINISHED));
    }

    /**
     * Getters and Setters
     */

    public List<MenuElementSelect> getOptions() {
        return options;
    }

    public void setOptions(List<MenuElementSelect> options) {
        this.options = options;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public MenuState getState() {
        return state;
    }

    public void setState(MenuState state) {
        this.state = state;
    }

    public float getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(float screenHeight) {
        this.screenHeight = screenHeight;
    }

    public float getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(float screenWidth) {
        this.screenWidth = screenWidth;
    }

    public enum MenuState {
        PLACING, STATIC, SPLITTING, FINISHED
    }
}
