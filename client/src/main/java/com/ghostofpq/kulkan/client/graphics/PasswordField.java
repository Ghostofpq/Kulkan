package com.ghostofpq.kulkan.client.graphics;

import com.ghostofpq.kulkan.client.graphics.HUD.TextField;

public class PasswordField extends TextField {

    public PasswordField(int posX, int posY, int length, int height, int maxLength) {
        super(posX, posY, length, height, maxLength);
    }

    @Override
    public String getContentToPrint() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            result.append("*");
        }
        return result.toString();
    }
}
