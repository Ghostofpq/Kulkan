package com.ghostofpq.kulkan.client.graphics;

public abstract class HUDElement {
    protected boolean hasFocus;
    protected int posX;
    protected int posY;
    protected int width;
    protected int height;

    public abstract void draw();

    public boolean isClicked(int mouseX, int mouseY) {
        boolean result = false;
        boolean okMouseX = (mouseX >= posX && mouseX <= posX + width);
        boolean okMouseY = (mouseY >= posY && mouseY <= posY + height);
        if (okMouseX && okMouseY) {
            result = true;
        }
        return result;
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
