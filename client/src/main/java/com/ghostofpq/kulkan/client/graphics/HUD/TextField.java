package com.ghostofpq.kulkan.client.graphics.HUD;

import com.ghostofpq.kulkan.client.utils.FontManager;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

@Slf4j
public class TextField extends HUDElement {
    protected String content;
    private int maxLength;
    private FontManager fontManager = FontManager.getInstance();
    private String fontName;
    private int posXText;
    private int posYText;
    private Alignement alignement;

    public enum Alignement {
        CENTER,
        LEFT,
        RIGHT
    }

    public TextField(int posX, int posY, int length, int height, int maxLength) {
        this.posX = posX;
        this.posY = posY;
        this.width = length;
        this.height = height;
        this.maxLength = maxLength;
        this.hasFocus = false;
        fontName = "optimus_princeps_16";
        content = "";
        alignement = Alignement.LEFT;
        updateTextPosition();
    }

    public TextField(int posX, int posY, int length, int height, int maxLength, String fontName) {
        this.posX = posX;
        this.posY = posY;
        this.width = length;
        this.height = height;
        this.maxLength = maxLength;
        this.hasFocus = false;
        this.fontName = fontName;
        content = "";
        alignement = Alignement.LEFT;
        updateTextPosition();
    }


    private void updateTextPosition() {
        switch (alignement) {
            case CENTER:
                posXText = posX + (width - fontManager.getFontMap().get(fontName).getWidth(getContentToPrint())) / 2;
                posYText = posY + (height - fontManager.getFontMap().get(fontName).getHeight(getContentToPrint())) / 2;
                break;
            case LEFT:
                posXText = posX;
                posYText = posY + (height - fontManager.getFontMap().get(fontName).getHeight(getContentToPrint())) / 2;
                break;
            case RIGHT:
                posXText = posX + width - fontManager.getFontMap().get(fontName).getWidth(getContentToPrint());
                posYText = posY + (height - fontManager.getFontMap().get(fontName).getHeight(getContentToPrint())) / 2;
                break;
        }
    }

    @Override
    public void draw() {
        fontManager.drawString(fontName, posXText, posYText, getContentToPrint(), Color.white);
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
                updateTextPosition();
            }
        }
    }

    public void deleteLastChar() {
        if (!content.isEmpty()) {
            content = content.substring(0, content.length() - 1);
            updateTextPosition();
        }
    }

    public void clear() {
        content = "";
        updateTextPosition();
    }

    public void setAlignement(Alignement alignement) {
        this.alignement = alignement;
    }
}
