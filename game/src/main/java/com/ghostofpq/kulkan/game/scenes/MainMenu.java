package com.ghostofpq.kulkan.game.scenes;

import com.ghostofpq.kulkan.game.graphics.MenuSelect;
import com.ghostofpq.kulkan.game.main.Game;
import com.ghostofpq.kulkan.game.utils.GraphicsManager;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class MainMenu implements Scene {
    private static volatile MainMenu instance = null;
    private MenuSelect menu;

    private MainMenu() {
    }

    public static MainMenu getInstance() {
        if (instance == null) {
            synchronized (MainMenu.class) {
                if (instance == null) {
                    instance = new MainMenu();
                }
            }
        }
        instance.init();
        return instance;
    }

    @Override
    public void init() {
        List<String> options = new ArrayList<String>();
        options.add("New player");
        options.add("Load");
        options.add("Quit");
        menu = new MenuSelect(options, 300, 200, 0, 50, 600, 800);
    }

    @Override
    public void update(long deltaTime) {
        menu.update();
        if (menu.isFinished()) {
            if (menu.getIndex() == 0) {
                Game.getInstance().setCurrentScene(NewPlayer.getInstance());
            } else if (menu.getIndex() == 1) {
                Game.getInstance().setCurrentScene(LoadMenu.getInstance());
            } else if (menu.getIndex() == 2) {
                Game.getInstance().quit();
            }
        }
    }

    @Override
    public void render() {
        GraphicsManager.getInstance().make2D();
        menu.render();
    }

    @Override
    public void manageInput() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (Keyboard.getEventKey() == Keyboard.KEY_UP) {
                    menu.indexDown();
                }
                if (Keyboard.getEventKey() == Keyboard.KEY_LEFT) {
                    menu.indexDown();
                }
                if (Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
                    menu.indexUp();
                }
                if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
                    menu.indexUp();
                }
                if (Keyboard.getEventKey() == Keyboard.KEY_RETURN) {
                    menu.split();
                }
            }
        }
    }
}
