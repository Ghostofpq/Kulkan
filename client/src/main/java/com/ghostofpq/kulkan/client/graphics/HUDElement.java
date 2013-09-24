package com.ghostofpq.kulkan.client.graphics;

public abstract class HUDElement {
    protected boolean hasFocus;
    protected int posX;
    protected int posY;
    protected int width;
    protected int height;

    public abstract void draw();

    public abstract boolean isClicked(int mouseX, int mouseY);

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
