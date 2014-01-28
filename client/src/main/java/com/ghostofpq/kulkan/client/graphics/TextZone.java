package com.ghostofpq.kulkan.client.graphics;

import com.ghostofpq.kulkan.client.utils.FontManager;
import com.ghostofpq.kulkan.client.utils.TextureKey;
import com.ghostofpq.kulkan.client.utils.TextureManager;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public class TextZone extends HUDElement {
    private final String FONT = "optimus_princeps_16";
    private FontManager fontManager = FontManager.getInstance();
    private String text;
    private int posXText;
    private int posYText;

    public TextZone(int posX, int posY, int length, int height, String text) {
        this.posX = posX;
        this.posY = posY;
        this.width = length;
        this.height = height;
        this.hasFocus = false;
        setText(text);
    }

    @Override
    public void draw() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        Texture texture;
        if (hasFocus()) {
            texture = TextureManager.getInstance().getTexture(TextureKey.TEXT_FIELD_FOCUS);
        } else {
            texture = TextureManager.getInstance().getTexture(TextureKey.TEXT_FIELD_NO_FOCUS);
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


        fontManager.drawString(FONT, posXText, posYText, text, Color.white);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        posXText = posX + (width - fontManager.getFontMap().get(FONT).getWidth(text)) / 2;
        posYText = posY + (height - fontManager.getFontMap().get(FONT).getHeight(text)) / 2;
    }
}
