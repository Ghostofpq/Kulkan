package com.ghostofpq.kulkan.client.graphics;


import com.ghostofpq.kulkan.client.utils.FontManager;
import org.newdawn.slick.Color;

import java.util.ArrayList;
import java.util.List;

public class MenuSelectAction {
    private final String FONT = "optimus_princeps_16";
    private int posX;
    private int posY;
    private int frameWidth;
    private int frameLength;
    private int frameHeight;
    private List<MenuSelectActions> options;
    private List<Boolean> optionsState;
    private int index;

    public MenuSelectAction(int posX, int posY, int frameLength, int frameHeight, int frameWidth) {
        this.posX = posX;
        this.posY = posY;

        this.frameLength = frameLength;
        this.frameHeight = frameHeight;
        this.frameWidth = frameWidth;
        this.options = new ArrayList<MenuSelectActions>();
        this.optionsState = new ArrayList<Boolean>();

        this.options.add(0, MenuSelectActions.MOVE);
        this.options.add(1, MenuSelectActions.ATTACK);
        this.options.add(2, MenuSelectActions.CAPACITY);
        this.options.add(3, MenuSelectActions.END_TURN);

        this.optionsState.add(0, true);
        this.optionsState.add(1, true);
        this.optionsState.add(2, true);
        this.optionsState.add(3, true);
    }

    public void reinitMenu() {
        index = 0;
        this.optionsState.set(0, true);
        this.optionsState.set(1, true);
        this.optionsState.set(2, true);
        this.optionsState.set(3, true);
    }

    public void render(Color color) {
        if (!options.isEmpty()) {
            Toolbox.drawFrame(posX, posY, frameLength, frameHeight, frameWidth, color);
            int optionHeight = frameHeight / (options.size() + 2);

            int optionY = posY;
            optionY += optionHeight;
            for (int i = 0; i < options.size(); i++) {
                int optionX = posX + ((frameLength - FontManager.getInstance().getFontMap().get(FONT).getWidth(options.get(i).toString())) / 2);
                if (options.get(index).equals(options.get(i))) {
                    FontManager.getInstance().drawString(FONT, optionX, optionY, options.get(i).toString(), Color.yellow);
                } else {
                    if (optionsState.get(i)) {
                        FontManager.getInstance().drawString(FONT, optionX, optionY, options.get(i).toString(), Color.white);
                    } else {
                        FontManager.getInstance().drawString(FONT, optionX, optionY, options.get(i).toString(), Color.gray);
                    }
                }
                optionY += optionHeight;
            }
        }
    }

    public void setHasMoved() {
        optionsState.set(0, false);
        incrementOptionsIndex();
    }

    public void setHasActed() {
        optionsState.set(1, false);
        optionsState.set(2, false);
        incrementOptionsIndex();
    }

    public void incrementOptionsIndex() {
        if (index >= options.size() - 1) {
            index = 0;
        } else {
            index++;
        }

        if (!optionsState.get(index)) {
            incrementOptionsIndex();
        }
    }

    public void decrementOptionsIndex() {
        if (index <= 0) {
            index = options.size() - 1;
        } else {
            index--;
        }

        if (!optionsState.get(index)) {
            decrementOptionsIndex();
        }
    }

    public MenuSelectActions getSelectedOption() {
        return options.get(index);
    }

    public enum MenuSelectActions {
        MOVE("Move"),
        ATTACK("Attack"),
        CAPACITY("Capacity"),
        END_TURN("End Turn");
        private final String propertyName;

        MenuSelectActions(String propertyName) {
            this.propertyName = propertyName;
        }

        @Override
        public String toString() {
            return propertyName;
        }
    }
}
