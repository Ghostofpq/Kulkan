package com.ghostofpq.kulkan.client.graphics.HUD;

import com.ghostofpq.kulkan.client.utils.FontManager;
import org.newdawn.slick.Color;

public abstract class HUDLabelledElement extends HUDElement {
    private int posXText;
    private int posYText;

    private FontManager fontManager = FontManager.getInstance();

    protected TextAlignment alignment;
    protected String fontName;
    protected String label;

    public void setAlignment(TextAlignment alignment) {
        this.alignment = alignment;
        updateTextPosition();
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
        updateTextPosition();
    }

    public void drawLabel() {
        fontManager.drawString(fontName, posXText, posYText, label, Color.white);
    }

    protected void updateTextPosition() {
        switch (alignment) {
            case CENTER:
                posXText = posX + (width - fontManager.getFontMap().get(fontName).getWidth(label)) / 2;
                posYText = posY + (height - fontManager.getFontMap().get(fontName).getHeight(label)) / 2;
                break;
            case LEFT:
                posXText = posX;
                posYText = posY + (height - fontManager.getFontMap().get(fontName).getHeight(label)) / 2;
                break;
            case RIGHT:
                posXText = posX + width - fontManager.getFontMap().get(fontName).getWidth(label);
                posYText = posY + (height - fontManager.getFontMap().get(fontName).getHeight(label)) / 2;
                break;
        }
    }
}
