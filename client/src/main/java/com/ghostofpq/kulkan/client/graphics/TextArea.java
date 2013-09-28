package com.ghostofpq.kulkan.client.graphics;


import com.ghostofpq.kulkan.client.utils.FontManager;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import java.util.ArrayList;
import java.util.List;

public class TextArea extends HUDElement {
    private final String FONT = "optimus_princeps_16";
    public List<String> textInputs;
    private int maxLength;
    private int numberOfLineToShow;

    public TextArea(int posX, int posY, int maxLength, int numberOfLineToShow) {
        this.posX = posX;
        this.posY = posY;
        this.maxLength = maxLength;
        this.hasFocus = false;
        textInputs = new ArrayList<String>();
    }

    @Override
    public void draw() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1f, 1f, 1f, 1f);

        for (int i = 0; i < numberOfLineToShow; i++) {
            if (null != textInputs.get(textInputs.size() - i)) {
                int posXText = posX;
                int posYText = posY + (numberOfLineToShow - i) * FontManager.getInstance().getFontMap().get(FONT).getHeight("AAA");
                FontManager.getInstance().drawString(FONT, posXText, posYText, textInputs.get(textInputs.size() - i), Color.white);
            } else {
                break;
            }
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public void addLine(String input) {
        while (!input.isEmpty()) {
            int nextIndex = Math.min(input.length(), maxLength);
            String buffer = input.substring(0, nextIndex);
            input = input.substring(nextIndex);
            textInputs.add(buffer);
        }
    }
}
