package com.ghostofpq.kulkan.client;

import com.ghostofpq.kulkan.client.scenes.LoginScene;
import com.ghostofpq.kulkan.client.scenes.Scene;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.entities.character.Player;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class Client {
    private static volatile Client instance = null;
    boolean requestClose;
    private Scene currentScene;
    private Player player;
    private long lastTimeTick;
    private int height;
    private int width;

    private Client() {
        this.height = 600;
        this.width = 800;
        this.requestClose = false;
        this.lastTimeTick = Sys.getTime();
        init();
    }

    public static Client getInstance() {
        if (instance == null) {
            synchronized (Client.class) {
                if (instance == null) {
                    instance = new Client();
                }
            }
        }
        return instance;
    }

    public static void main(String[] argv) {
        //System.setProperty("org.lwjgl.librarypath", new File("natives").getAbsolutePath());
        Client g = Client.getInstance();
        g.setCurrentScene(LoginScene.getInstance());

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
