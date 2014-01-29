package com.ghostofpq.kulkan.client.graphics.HUD;

import com.ghostofpq.kulkan.client.utils.FontManager;
import com.ghostofpq.kulkan.client.utils.TextureKey;
import com.ghostofpq.kulkan.client.utils.TextureManager;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

@Slf4j
public class TextField extends HUDElement {
    private final String FONT = "optimus_princeps_16";
    protected String content;
    private int maxLength;
    private FontManager fontManager = FontManager.getInstance();

    public TextField(int posX, int posY, int length, int height, int maxLength) {
        this.posX = posX;
        this.posY = posY;
        this.width = length;
        this.height = height;
        this.maxLength = maxLength;
        this.hasFocus = false;
        content = "";
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


        int posXText = posX + (width - fontManager.getFontMap().get(FONT).getWidth(getContentToPrint())) / 2;
        int posYText = posY + (height - fontManager.getFontMap().get(FONT).getHeight(getContentToPrint())) / 2;

        fontManager.drawString(FONT, posXText, posYText, getContentToPrint(), Color.white);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public String getContent() {
        return content;
    }

    public String getContentToPrint() {
        return content;
    }

    public void writeChar(char c) {
        if (Character.isLetterOrDigit(c) || Character.isSpaceChar(c)) {
            if (content.length() < maxLength) {
                content += c;
            }
        }
    }

    public void deleteLastChar() {
        if (!content.isEmpty()) {
            content = content.substring(0, content.length() - 1);
        }
    }

    public void clear() {
        content = "";
    }
}