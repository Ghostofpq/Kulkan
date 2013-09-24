package com.ghostofpq.kulkan.game.main;

import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.game.scenes.BattleScene;
import com.ghostofpq.kulkan.game.scenes.Scene;
import com.ghostofpq.kulkan.game.utils.GraphicsManager;
import com.ghostofpq.kulkan.game.utils.SaveManager;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Game {
    private static volatile Game instance = null;
    boolean requestClose;
    private int height;
    private int width;
    private Scene currentScene;
    private Player player;
    private long lastTimeTick;

    private Game() {
        this.height = 600;
        this.width = 800;
        this.requestClose = false;
        this.lastTimeTick = Sys.getTime();
        player = new Player("Jack");
        init();

    }

    public static Game getInstance() {
        if (instance == null) {
            synchronized (Game.class) {
                if (instance == null) {
                    instance = new Game();
                }
            }
        }
        return instance;
    }

    public static void main(String[] argv) {
        //System.setProperty("org.lwjgl.librarypath", new File("natives").getAbsolutePath());
        Game g = Game.getInstance();
        g.setCurrentScene(BattleScene.getInstance());

        SaveManager sm = SaveManager.getInstance();
        Player p1 = sm.loadPlayer("Bob");
        Player p2 = sm.loadPlayer("Jack");
        List<Player> players = new ArrayList<Player>();
        players.add(p1);
        players.add(p2);

        BattleScene.getInstance().setPlayer(players);
        g.run();
    }

    public void init() {
        try {
            Display.setDisplayMode(new DisplayMode(this.width, this.height));
            Display.setSwapInterval(1);
            Display.sync(60);
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }
        GraphicsManager.getInstance().ready3D();
    }

    public void run() {
        while (!requestClose) {
            manageInput();
            update(deltaTimeInMillis());
            render();
            lastTimeTick = Sys.getTime();
            while (deltaTimeInMillis() <= 4) {
                // waiting for at least 4 millis
            }
        }
        Display.destroy();
    }

    private long deltaTimeInMillis() {
        return Sys.getTime() - lastTimeTick;
    }

    public void manageInput() {
        currentScene.manageInput();
    }

    public void update(long deltaTime) {
        requestClose = Display.isCloseRequested();
        currentScene.update(deltaTime);
    }

    public void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        currentScene.render();

        Display.update();
        Display.sync(60);
    }

    public void quit() {
        requestClose = true;
    }

    /**
     * Getters and Setters
     */

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(Scene currentScene) {
        this.currentScene = currentScene;
        this.currentScene.init();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
