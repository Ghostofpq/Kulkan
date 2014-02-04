package com.ghostofpq.kulkan.client.graphics.HUD;

import com.ghostofpq.kulkan.client.utils.TextureKey;
import com.ghostofpq.kulkan.client.utils.TextureManager;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

public class Frame extends HUDElement {


    private int frameWidth;
    private int frameHeight;
    private TextureKey textureKey;

    public Frame(int posX, int posY, int width, int height, int frameWidth, int frameHeight, TextureKey textureKey) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.textureKey = textureKey;
    }

    @Override
    public void draw() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        Texture texture = TextureManager.getInstance().getTexture(textureKey);
        texture.bind();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex2f(posX, posY);
        GL11.glTexCoord2d(texture.getWidth(), 0);
        GL11.glVertex2f(posX + width, posY);
        GL11.glTexCoord2d(texture.getWidth(), texture.getHeight());
        GL11.glVertex2f(posX + width, posY + height);
        GL11.glTexCoord2d(0, texture.getHeight());
        GL11.glVertex2f(posX, posY + height);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    @Override
    protected boolean isHovered(int mouseX, int mouseY) {
        boolean okMouseX = (mouseX >= posX && mouseX <= posX + frameWidth) || (mouseX >= posX + width - frameWidth && mouseX <= posX + width);
        boolean okMouseY = (mouseY >= posY && mouseY <= posY + frameHeight) || (mouseY >= posY + height - frameHeight && mouseY <= posY + height);
        return (okMouseX || okMouseY);
    }
}
