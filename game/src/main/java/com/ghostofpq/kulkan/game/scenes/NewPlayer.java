package com.ghostofpq.kulkan.game.scenes;

import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.game.graphics.MenuSelect;
import com.ghostofpq.kulkan.game.graphics.TextField;
import com.ghostofpq.kulkan.game.main.Game;
import com.ghostofpq.kulkan.game.utils.SaveManager;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class NewPlayer implements Scene {
    private static volatile NewPlayer instance = null;
    private MenuSelect menu;
    private String playerName;
    private TextField playerNameField;

    private NewPlayer() {
    }

    public static NewPlayer getInstance() {
        if (instance == null) {
            synchronized (MainMenu.class) {
                if (instance == null) {
                    instance = new NewPlayer();
                }
            }
        }
        instance.init();
        return instance;
    }

    @Override
    public void init() {
        playerName = new String();
        playerName = "Marcel";
        playerNameField = new TextField(300, 200, playerName);
        List<String> options = new ArrayList<String>();
        options.add("Okay");
        options.add("Back");
        menu = new MenuSelect(options, 300, 250, 0, 50, 600, 800);
    }

    @Override
    public void update(long deltaTime) {
        menu.update();
        if (menu.isFinished()) {
            if (menu.getIndex() == 0) {
                if (playerName.length() >= 3) {
                    Player player = new Player(playerName);
                    SaveManager.getInstance().savePlayer(player);
                    Game.getInstance().setPlayer(player);
                    Game.getInstance().setCurrentScene(PlayerMenu.getInstance());
                }
            } else if (menu.getIndex() == 1) {
                Game.getInstance().setCurrentScene(MainMenu.getInstance());
            }
        }

    }

    @Override
    public void render() {
        playerNameField.render();
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
                if (Keyboard.getEventKey() == Keyboard.KEY_BACK) {
                    if (!playerName.isEmpty()) {
                        playerName = playerName.substring(0, playerName.length() - 1);
                        playerNameField.setText(playerName);
                    }

                }


                if (Character.isLetter(Keyboard.getEventCharacter())) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(playerName);
                    builder.append(Keyboard.getEventCharacter());
                    playerName = builder.toString();
                    playerNameField.setText(playerName);
                }

            }


        }
    }
}
