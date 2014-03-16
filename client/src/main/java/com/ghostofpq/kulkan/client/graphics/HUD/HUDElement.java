package com.ghostofpq.kulkan.client.graphics.HUD;

import com.ghostofpq.kulkan.client.ClientContext;
import org.lwjgl.input.Mouse;

public abstract class HUDElement {
    protected boolean hasFocus;
    protected int posX;
    protected int posY;
    protected int width;
    protected int height;
    private long lastTimeWasClicked = System.currentTimeMillis();
    private long deltaMillis = 100;

    public abstract void draw();

    public boolean isHovered() {
        return isHovered(Mouse.getX(), ClientContext.currentResolution.getHeight() - Mouse.getY());
    }

    protected boolean isHovered(int mouseX, int mouseY) {
        boolean result = false;
        boolean okMouseX = (mouseX >= posX && mouseX <= posX + width);
        boolean okMouseY = (mouseY >= posY && mouseY <= posY + height);
        if (okMouseX && okMouseY) {
            result = true;
        }
        return result;
    }

    public boolean isClicked() {
        return isClicked(Mouse.getX(), ClientContext.currentResolution.getHeight() - Mouse.getY());
    }

    private boolean isClicked(int mouseX, int mouseY) {
        boolean result = false;
        if (!isInstantRepetition()) {
            result = isHovered();
        }
        return result;
    }

    private boolean isInstantRepetition() {
        boolean isInstantRepetition;
        if (System.currentTimeMillis() - lastTimeWasClicked < deltaMillis) {
            isInstantRepetition = true;
        } else {
            lastTimeWasClicked = System.currentTimeMillis();
            isInstantRepetition = false;
        }
        return isInstantRepetition;
    }

    /*
    GETTERS & SETTERS
     */

    public boolean hasFocus() {
        return hasFocus;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setHasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
    }
}
