package com.ghostofpq.kulkan.client.graphics.HUD;


import com.ghostofpq.kulkan.client.utils.FontManager;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TextArea extends HUDElement {
    public List<String> textInputs;
    private String fontName = "optimus_princeps_16";
    private int maxLength;
    private int numberOfLineToShow;
    private FontManager fontManager = FontManager.getInstance();

    public TextArea(int posX, int posY, int width, int height) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.maxLength = width / (fontManager.getFontMap().get(fontName).getWidth("a"));
        this.hasFocus = false;
        this.numberOfLineToShow = height / (fontManager.getFontMap().get(fontName).getHeight("A"));
        log.debug("New Text Area : {},{} {}x{} maxLength {} numberOfLineToShow {}", this.posX, this.posY, width, height, maxLength, numberOfLineToShow);
        textInputs = new ArrayList<String>();
    }

    public TextArea(int posX, int posY, int width, int height, String fontName) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.maxLength = width / (fontManager.getFontMap().get(fontName).getWidth("a"));
        this.hasFocus = false;
        this.numberOfLineToShow = height / (fontManager.getFontMap().get(fontName).getHeight("A"));
        this.fontName = fontName;
        log.debug("New Text Area : {},{} {}x{} maxLength {} numberOfLineToShow {}", this.posX, this.posY, width, height, maxLength, numberOfLineToShow);
        textInputs = new ArrayList<String>();
    }

    @Override
    public void draw() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        if (!textInputs.isEmpty()) {
            for (int i = 1; i <= numberOfLineToShow; i++) {
                if (textInputs.size() - i >= 0) {
                    int posXText = posX;
                    int posYText = posY + (numberOfLineToShow - i) * fontManager.getFontMap().get(fontName).getHeight("AAA");
                    fontManager.drawString(fontName, posXText, posYText, textInputs.get(textInputs.size() - i), Color.white);
                } else {
                    break;
                }
            }
        }
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public void addLine(String input) {
        while (!input.isEmpty()) {
            if (stringFits(input)) {
                textInputs.add(input);
                input = "";
            } else {
                int index = 0;

                StringBuilder buffer = new StringBuilder();
                buffer.append(input.charAt(index));
                while (stringFits(buffer.toString()) && index < input.length()) {
                    index++;
                    buffer.append(input.charAt(index));
                }
                int mark = index - 1;
                if (!stringFits(buffer.toString())) {
                    while (input.charAt(index) != (' ') && index > 0) {
                        index--;
                        buffer.deleteCharAt(buffer.length() - 1);
                    }
                    if (index == 0) {
                        buffer.append(input.substring(0, mark));
                    }
                }

                input = input.substring(buffer.toString().length(), input.length());
                textInputs.add(buffer.toString());
            }
        }
    }

    private boolean stringFits(String string) {
        return ((fontManager.getFontMap().get(fontName).getWidth(string)) < width);
    }


    public void clear() {
        textInputs = new ArrayList<String>();
    }
}
