package com.ghostofpq.kulkan.client.graphics;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.utils.TextureKey;
import com.ghostofpq.kulkan.client.utils.TextureManager;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

public class Background {

    private TextureKey textureKey;

    public Background(TextureKey textureKey) {
        this.textureKey = textureKey;
    }

    public void draw() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        Texture texture = TextureManager.getInstance().getTexture(textureKey);
        texture.bind();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex2f(0, 0);
        GL11.glTexCoord2d(texture.getWidth(), 0);
        GL11.glVertex2f(Client.getInstance().getWidth(), 0);
        GL11.glTexCoord2d(texture.getWidth(), texture.getHeight());
        GL11.glVertex2f(Client.getInstance().getWidth(), Client.getInstance().getHeight());
        GL11.glTexCoord2d(0, texture.getHeight());
        GL11.glVertex2f(0, Client.getInstance().getHeight());
        GL11.glEnd();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }
}
