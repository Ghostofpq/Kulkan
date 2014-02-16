package com.ghostofpq.kulkan.client.graphics.HUD;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.utils.TextureKey;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class PopUp extends HUDElement {
    private List<Button> options;
    private TextZone textZone;
    private Frame frame;

    public PopUp(List<String> options, String text) {
        int width = Client.getInstance().getWidth() / 2;
        int height = Client.getInstance().getHeight() / 3;
        int posX = (Client.getInstance().getWidth() - width) / 2;
        int posY = (Client.getInstance().getHeight() - height) / 2;

        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.hasFocus = false;

        int frameWidth = Client.getInstance().getWidth() / 64;
        frame = new Frame(posX, posY, width, height, frameWidth, frameWidth, TextureKey.COMMON_EXT_FRAME);
        textZone = new TextZone(posX + frameWidth, posY + frameWidth, width - (2 * frameWidth), (height - 2 * frameWidth) / 2, text);
        textZone.setAlignment(TextAlignment.CENTER);
        textZone.setFontName("optimus_princeps_16");
        this.options = new ArrayList<Button>();

        int buttonWidth = 100;
        int buttonHeight = 50;
        for (int i = 0; i < options.size(); i++) {
            int buttonPosX = posX + (width / 2 - i * buttonWidth - buttonWidth / 2);
            int buttonPosY = posY + (3 * height / 4) - buttonHeight / 2;
            Button button = new Button(buttonPosX, buttonPosY, buttonWidth, buttonHeight, options.get(i)) {
                @Override
                public void onClick() {
                }
            };
            this.options.add(button);
        }
    }

    @Override
    public void draw() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(0f, 0f, 0f, 0.5f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(0, 0);
        GL11.glVertex2f(Client.getInstance().getWidth(), 0);
        GL11.glVertex2f(Client.getInstance().getWidth(), Client.getInstance().getHeight());
        GL11.glVertex2f(0, Client.getInstance().getHeight());
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glColor4f(0f, 0f, 0f, 1f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(posX, posY);
        GL11.glVertex2f(posX + width, posY);
        GL11.glVertex2f(posX + width, posY + height);
        GL11.glVertex2f(posX, posY + height);
        GL11.glEnd();

        for (Button button : options) {
            button.draw();
        }
        textZone.draw();
        frame.draw();
    }

    public String onClick() {
        for (Button button : options) {
            if (button.isClicked()) {
                return button.getLabel();
            }
        }
        return null;
    }
}
