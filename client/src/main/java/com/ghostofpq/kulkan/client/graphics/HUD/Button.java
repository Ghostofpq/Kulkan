package com.ghostofpq.kulkan.client.graphics.HUD;

import com.ghostofpq.kulkan.client.utils.TextureKey;
import com.ghostofpq.kulkan.client.utils.TextureManager;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public abstract class Button extends HUDLabelledElement {
    private TextureKey customTexture;
    private TextureKey customTextureFocus;

    public Button(int posX, int posY, int width, int height, String label) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.hasFocus = false;
        this.fontName = "optimus_princeps_16";
        this.label = label;
        this.alignment = TextAlignment.CENTER;
        customTexture = TextureKey.TEXT_FIELD_NO_FOCUS;
        customTextureFocus = TextureKey.TEXT_FIELD_FOCUS;
        updateTextPosition();
    }

    public Button(int posX, int posY, int width, int height, String label, TextureKey customTexture, TextureKey customTextureFocus) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.hasFocus = false;
        this.fontName = "optimus_princeps_16";
        this.label = label;
        this.alignment = TextAlignment.CENTER;
        this.customTexture = customTexture;
        this.customTextureFocus = customTextureFocus;
        updateTextPosition();
    }

    public void setCustomTexture(TextureKey customTexture) {
        this.customTexture = customTexture;
    }

    public void setCustomTextureFocus(TextureKey customTextureFocus) {
        this.customTextureFocus = customTextureFocus;
    }

    public void draw() {
        if (customTexture != null || customTextureFocus != null) {
            Texture texture = null;

            if (isHovered() && customTextureFocus != null) {
                texture = TextureManager.getInstance().getTexture(customTextureFocus);
            } else if (customTexture != null) {
                texture = TextureManager.getInstance().getTexture(customTexture);
            }

            if (null != texture) {
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

        if (isHovered()) {
            drawLabel(Color.red);
        } else {
            drawLabel();
        }

    }


    public abstract void onClick();
}
