package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.HUDElement;
import com.ghostofpq.kulkan.client.graphics.PasswordField;
import com.ghostofpq.kulkan.client.graphics.TextField;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.client.utils.InputManager;
import com.ghostofpq.kulkan.client.utils.InputMap;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LoginScene implements Scene {

    private static volatile LoginScene instance = null;
    private TextField pseudo;
    private PasswordField password;
    private List<HUDElement> hudElementList;
    private int indexOnFocus;

    private LoginScene() {
    }

    public static LoginScene getInstance() {
        if (instance == null) {
            synchronized (LoginScene.class) {
                if (instance == null) {
                    instance = new LoginScene();
                }
            }
        }
        return instance;
    }

    @Override
    public void init() {
        pseudo = new TextField(200, 200, 300, 50, 10);
        password = new PasswordField(200, 300, 300, 50, 10);

        hudElementList = new ArrayList<HUDElement>();
        hudElementList.add(pseudo);
        hudElementList.add(password);

        indexOnFocus = 0;
        setFocusOn(indexOnFocus);
    }

    public void setFocusOn(int i) {
        for (HUDElement hudElement : hudElementList) {
            hudElement.setHasFocus(false);
        }
        hudElementList.get(i).setHasFocus(true);
    }

    @Override
    public void update(long deltaTime) {
    }

    @Override
    public void render() {
        GraphicsManager.getInstance().make2D();
        pseudo.draw();
        password.draw();
    }

    @Override
    public void manageInput() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                if (pseudo.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    setFocusOn(hudElementList.indexOf(pseudo));
                }
                if (password.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    setFocusOn(hudElementList.indexOf(password));
                }
            }
        }
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (InputManager.getInstance().getInput(Keyboard.getEventKey()) != null) {
                    if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.CANCEL)) {
                        if (pseudo.hasFocus()) {
                            pseudo.deleteLastChar();
                        } else if (password.hasFocus()) {
                            password.deleteLastChar();
                        }
                    } else {
                        if (pseudo.hasFocus()) {
                            pseudo.writeChar(Keyboard.getEventCharacter());
                        } else if (password.hasFocus()) {
                            password.writeChar(Keyboard.getEventCharacter());
                        }
                    }
                } else {
                    if (pseudo.hasFocus()) {
                        pseudo.writeChar(Keyboard.getEventCharacter());
                    } else if (password.hasFocus()) {
                        password.writeChar(Keyboard.getEventCharacter());
                    }
                }

            }
        }
    }
}
