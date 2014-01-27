package com.ghostofpq.kulkan.client;

import com.ghostofpq.kulkan.client.scenes.LobbyScene;
import com.ghostofpq.kulkan.client.scenes.LoginScene;
import com.ghostofpq.kulkan.client.scenes.Scene;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.rabbitmq.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;

@Slf4j
public class Client {
    private static volatile Client instance = null;
    private final String CLIENT_QUEUE_NAME_BASE = "/client/";
    private int height;
    private int width;
    private Scene currentScene;
    private Player player;
    private String tokenKey;
    private long lastTimeTick;
    private boolean requestClose;
    @Autowired
    private ClientContext clientContext;
    @Autowired
    private ClientMessenger clientMessenger;
    @Autowired
    private LoginScene loginScene;
    @Autowired
    private LobbyScene lobbyScene;

    private Client() {

    }

    public static Client getInstance() {
        return instance;
    }

    public static void main(String[] argv) {
        System.setProperty("org.lwjgl.librarypath", new File("natives").getAbsolutePath());
        ApplicationContext context = new ClassPathXmlApplicationContext("client-context.xml");
        Client g = ((Client) context.getBean("client"));
        g.init();
        try {
            g.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        if (instance == null) {
            instance = this;
        }

        clientContext.init();
        setHeight(clientContext.getHeight());
        setWidth(clientContext.getWidth());

        this.requestClose = false;
        this.lastTimeTick = Sys.getTime();
        try {
            Display.setDisplayMode(new DisplayMode(clientContext.getWidth(), clientContext.getHeight()));
            Display.setSwapInterval(1);
            Display.sync(60);
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }
        GraphicsManager.getInstance().ready3D();

        try {
            clientMessenger.initConnection();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        setCurrentScene(loginScene);
    }

    public Message receiveMessage() {
        return clientMessenger.receiveMessage();
    }

    public void run() throws InterruptedException {
        while (!requestClose) {
            update(deltaTimeInMillis());
            currentScene.manageInput();
            currentScene.receiveMessage();
            render();
            lastTimeTick = Sys.getTime();
            Thread.sleep(1);
        }
        try {
            clientMessenger.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Display.destroy();
    }

    private long deltaTimeInMillis() {
        return Sys.getTime() - lastTimeTick;
    }

    public void update(long deltaTime) {
        if (!requestClose) {
            requestClose = Display.isCloseRequested();
        }
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

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(Scene currentScene) {
        if (null != this.currentScene) {
            try {
                this.currentScene.closeConnections();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.currentScene = currentScene;
        this.currentScene.init();
        try {
            this.currentScene.initConnections();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Player getPlayer() {
        return clientContext.getPlayer();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Connection getConnection() {
        return clientMessenger.getConnection();
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
        try {
            clientMessenger.openChannelsAfterAuthentication(tokenKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LobbyScene getLobbyScene() {
        return lobbyScene;
    }
}
