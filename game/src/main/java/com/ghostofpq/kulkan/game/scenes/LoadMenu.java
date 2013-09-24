package com.ghostofpq.kulkan.game.scenes;

import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.game.graphics.MenuSelect;
import com.ghostofpq.kulkan.game.main.Game;
import com.ghostofpq.kulkan.game.utils.SaveManager;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoadMenu implements Scene {
    private static LoadMenu instance = new LoadMenu();
    private MenuSelect menuLoad;

    private LoadMenu() {

    }

    public static LoadMenu getInstance() {
        if (instance == null) {
            synchronized (MainMenu.class) {
                if (instance == null) {
                    instance = new LoadMenu();
                }
            }
        }
        instance.init();
        return instance;
    }

    @Override
    public void init() {
        List<String> savedPlayers = null;
        try {
            savedPlayers = SaveManager.getInstance().checkFolderForPlayers();
        } catch (IOException e) {
            e.printStackTrace();
            Game.getInstance().quit();
        }

        List<String> options = new ArrayList<String>();
        for (String player : savedPlayers) {
            options.add(player);
        }
        options.add("Back");
        menuLoad = new MenuSelect(options, 300, 200, 0, 50, 600, 800);
    }

    @Override
    public void update(long deltaTime) {
        menuLoad.update();
        if (menuLoad.isFinished()) {
            if (menuLoad.getIndex() == (menuLoad.getOptions().size() - 1)) {
                Game.getInstance().setCurrentScene(MainMenu.getInstance());
            } else {
                String playerPseudo = menuLoad.getOptions().get(menuLoad.getIndex()).getText();
                Player player = SaveManager.getInstance().loadPlayer(playerPseudo);
                Game.getInstance().setPlayer(player);
                Game.getInstance().setCurrentScene(PlayerMenu.getInstance());
            }
        }
    }

    @Override
    public void render() {
        menuLoad.render();
    }

    @Override
    public void manageInput() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (Keyboard.getEventKey() == Keyboard.KEY_UP) {
                    menuLoad.indexDown();
                }
                if (Keyboard.getEventKey() == Keyboard.KEY_LEFT) {
                    menuLoad.indexDown();
                }
                if (Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
                    menuLoad.indexUp();
                }
                if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
                    menuLoad.indexUp();
                }
                if (Keyboard.getEventKey() == Keyboard.KEY_RETURN) {
                    menuLoad.split();
                }
            }
        }
    }
}
