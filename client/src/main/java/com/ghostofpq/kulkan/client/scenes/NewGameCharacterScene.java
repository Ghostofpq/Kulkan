package com.ghostofpq.kulkan.client.scenes;


import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.Button;
import com.ghostofpq.kulkan.client.graphics.HUDElement;
import com.ghostofpq.kulkan.client.graphics.TextField;
import com.ghostofpq.kulkan.client.utils.InputManager;
import com.ghostofpq.kulkan.client.utils.InputMap;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class NewGameCharacterScene implements Scene {
    private static volatile NewGameCharacterScene instance = null;
    private List<HUDElement> raceSwitch;
    private TextField pseudo;
    private Button male;
    private Button female;
    private int WIDTH_SEPARATOR = 10;
    private int widthStep;
    private int HEIGHT_SEPARATOR = 10;
    private int heightStep;

    private NewGameCharacterScene() {
    }

    public static NewGameCharacterScene getInstance() {
        if (instance == null) {
            synchronized (NewGameCharacterScene.class) {
                if (instance == null) {
                    instance = new NewGameCharacterScene();
                }
            }
        }
        return instance;
    }

    @Override
    public void init() {
        widthStep = (Client.getInstance().getWidth() - 3 * WIDTH_SEPARATOR) / 5;
        heightStep = (Client.getInstance().getWidth() - 4 * HEIGHT_SEPARATOR) / 5;

        int malePosX = WIDTH_SEPARATOR;
        int malePosY = HEIGHT_SEPARATOR;

        int femalePosX = malePosX + widthStep / 2;
        int femalePosY = HEIGHT_SEPARATOR;

        raceSwitch = new ArrayList<HUDElement>();

        male = new Button(malePosX, malePosY, widthStep / 2, heightStep, "MALE") {
            @Override
            public void onClick() {
                maleHasFocus();
            }
        };
        female = new
                Button(femalePosX, femalePosY, widthStep / 2, heightStep, "FEMALE") {
                    @Override
                    public void onClick() {
                        femaleHasFocus();
                    }
                };
        maleHasFocus();

    }

    public void maleHasFocus() {
        male.setHasFocus(true);
        female.setHasFocus(false);
    }

    public void femaleHasFocus() {
        male.setHasFocus(false);
        female.setHasFocus(true);
    }

    @Override
    public void update(long deltaTime) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void render() {
        male.draw();
        female.draw();
    }

    @Override
    public void manageInput() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                if (male.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    male.onClick();
                }
                if (female.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    female.onClick();
                }
            }
        }
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (InputManager.getInstance().getInput(Keyboard.getEventKey()) != null) {
                    if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.CANCEL)) {
                        pseudo.deleteLastChar();
                    } else {
                        pseudo.writeChar(Keyboard.getEventCharacter());
                    }
                } else {
                    pseudo.writeChar(Keyboard.getEventCharacter());
                }
            }
        }
    }

    @Override
    public void closeConnections() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void receiveMessage() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
