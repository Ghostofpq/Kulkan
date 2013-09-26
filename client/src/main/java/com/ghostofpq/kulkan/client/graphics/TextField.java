package com.ghostofpq.kulkan.client.graphics;

import com.ghostofpq.kulkan.client.utils.FontManager;
import com.ghostofpq.kulkan.client.utils.TextureKey;
import com.ghostofpq.kulkan.client.utils.TextureManager;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

@Slf4j
public class TextField extends HUDElement {
    private final String FONT = "optimus_princeps_16";
    protected String content;
    private int maxLength;

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
        if (hasFocus()) {
            TextureManager.getInstance().getTexture(TextureKey.TEXT_FIELD_FOCUS).bind();
        } else {
            TextureManager.getInstance().getTexture(TextureKey.TEXT_FIELD_NO_FOCUS).bind();
        }
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex2f(posX, posY);
        GL11.glTexCoord2d(1, 0);
        GL11.glVertex2f(posX + width, posY);
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex2f(posX + width, posY + height);
        GL11.glTexCoord2d(0, 1);
        GL11.glVertex2f(posX, posY + height);
        GL11.glEnd();


        int posXText = posX + (width - FontManager.getInstance().getFontMap().get(FONT).getWidth(getContentToPrint())) / 2;
        int posYText = posY + (height - FontManager.getInstance().getFontMap().get(FONT).getHeight(getContentToPrint())) / 2;

        FontManager.getInstance().drawString(FONT, posXText, posYText, getContentToPrint(), Color.white);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public String getContent() {
        return content;
    }

    public String getContentToPrint() {
        return content;
    }

    public void writeChar(char c) {
        if (content.length() < maxLength) {
            content += c;
        }
    }

    public void deleteLastChar() {
        content = content.substring(0, content.length() - 1);
    }
}
