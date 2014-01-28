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
    private Scene currentScene;
    private Player player;
    private String tokenKey;
    private long lastTimeTick;
    private boolean requestClose;
    private boolean requestUpdateDisplayMode;
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

        this.requestClose = false;
        this.requestUpdateDisplayMode = false;
        this.lastTimeTick = Sys.getTime();

        try {
            Display.setDisplayMode(clientContext.getCurrentDisplayMode());
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

    private void updateDisplayMode() {
        requestUpdateDisplayMode = false;
        try {
            Display.destroy();
            Display.setDisplayMode(clientContext.getCurrentDisplayMode());
            Display.setSwapInterval(1);
            Display.sync(60);
            Display.create();
            Display.makeCurrent();
            initGL();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }
        //GraphicsManager.getInstance().ready3D();
    }

    public void run() throws InterruptedException {
        while (!requestClose && !requestUpdateDisplayMode) {
            update(deltaTimeInMillis());
            currentScene.manageInput();
            currentScene.receiveMessage();
            render();
            lastTimeTick = Sys.getTime();
            Thread.sleep(1);
        }
        if (requestUpdateDisplayMode) {
            updateDisplayMode();
            setCurrentScene(currentScene);
            run();
        } else {
            try {
                clientMessenger.closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Display.destroy();
        }
    }

    private void initGL() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLoadIdentity();
        GL11.glViewport(0, 0, clientContext.width, clientContext.height);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glOrtho(0.0D, clientContext.width, 0.0D, clientContext.height, 1.0D, -1.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
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

    public void updateDisplay() {
        requestUpdateDisplayMode = true;
    }

    /**
     * Getters and Setters
     */

    public int getHeight() {
        return ClientContext.height;
    }

    public int getWidth() {
        return ClientContext.width;
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
