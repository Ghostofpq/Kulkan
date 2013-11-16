package com.ghostofpq.kulkan.client.graphics;

public abstract class HUDElement {
    protected boolean hasFocus;
    protected int posX;
    protected int posY;
    protected int width;
    protected int height;
    private long lastTimeWasClicked = System.currentTimeMillis();
    private long deltaMillis = 100;

    public abstract void draw();

    public boolean isClicked(int mouseX, int mouseY) {
        boolean result = false;
        if (!isInstantRepetition()) {
            boolean okMouseX = (mouseX >= posX && mouseX <= posX + width);
            boolean okMouseY = (mouseY >= posY && mouseY <= posY + height);
            if (okMouseX && okMouseY) {
                result = true;
            }
        }
        return result;
    }

    public boolean isInstantRepetition() {
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

    public void setHasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
    }
}
