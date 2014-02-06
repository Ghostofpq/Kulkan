package com.ghostofpq.kulkan.client.graphics.HUD;

public class PasswordField extends TextField {
    private String password = "";

    public PasswordField(int posX, int posY, int length, int height, int maxLength) {
        super(posX, posY, length, height, maxLength);
    }

    @Override
    public void writeChar(char c) {
        if (Character.isLetterOrDigit(c) || Character.isSpaceChar(c)) {
            if (password.length() < maxLength) {
                password += c;
                label += "*";
                updateTextPosition();
            }
        }
    }

    @Override
    public void deleteLastChar() {
        if (!password.isEmpty()) {
            password = password.substring(0, password.length() - 1);
            label = label.substring(0, label.length() - 1);
            updateTextPosition();
        }
    }

    @Override
    public void clear() {
        password = "";
        label = "";
        updateTextPosition();
    }

    @Override
    public String getLabel() {
        return password;
    }
}
