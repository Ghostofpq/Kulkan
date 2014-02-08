package com.ghostofpq.kulkan.client.graphics.HUD;


import com.ghostofpq.kulkan.client.utils.TextureKey;
import com.ghostofpq.kulkan.client.utils.TextureManager;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

public class HUDTexturedElement extends HUDElement {
    private TextureKey textureKey;
    private TextureKey textureKeyWhenFocus;

    public HUDTexturedElement(int posX, int posY, int width, int height, TextureKey textureKey, TextureKey textureKeyWhenFocus) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.hasFocus = false;
        this.textureKey = textureKey;
        this.textureKeyWhenFocus = textureKeyWhenFocus;
    }

    @Override
    public void draw() {
        Texture texture;
        if (hasFocus()) {
            texture = TextureManager.getInstance().getTexture(textureKeyWhenFocus);
        } else {
            texture = TextureManager.getInstance().getTexture(textureKey);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1f, 1f, 1f, 1f);
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
}
