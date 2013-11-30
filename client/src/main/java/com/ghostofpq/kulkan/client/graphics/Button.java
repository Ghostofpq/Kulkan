package com.ghostofpq.kulkan.client.graphics;

import com.ghostofpq.kulkan.client.utils.FontManager;
import com.ghostofpq.kulkan.client.utils.TextureKey;
import com.ghostofpq.kulkan.client.utils.TextureManager;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public abstract class Button extends HUDElement {
    private final String FONT = "optimus_princeps_16";
    protected String label;
    private TextureKey customTexture;
    private TextureKey customTextureFocus;

    public Button(int posX, int posY, int width, int height, String label) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.label = label;
        this.hasFocus = false;
    }

    public void setCustomTexture(TextureKey customTexture) {
        this.customTexture = customTexture;
    }

    public void setCustomTextureFocus(TextureKey customTextureFocus) {
        this.customTextureFocus = customTextureFocus;
    }

    public void draw() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        Texture texture;
        if (customTexture == null || customTextureFocus == null) {
            if (hasFocus()) {
                texture = TextureManager.getInstance().getTexture(TextureKey.TEXT_FIELD_FOCUS);
            } else {
                texture = TextureManager.getInstance().getTexture(TextureKey.TEXT_FIELD_NO_FOCUS);
            }
        } else {
            if (hasFocus()) {
                texture = TextureManager.getInstance().getTexture(customTextureFocus);
            } else {
                texture = TextureManager.getInstance().getTexture(customTexture);
            }
        }
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


        int posXText = posX + (width - FontManager.getInstance().getFontMap().get(FONT).getWidth(label)) / 2;
        int posYText = posY + (height - FontManager.getInstance().getFontMap().get(FONT).getHeight(label)) / 2;

        FontManager.getInstance().drawString(FONT, posXText, posYText, label, Color.white);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public abstract void onClick();
}
