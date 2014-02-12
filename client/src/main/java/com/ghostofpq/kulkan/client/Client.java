package com.ghostofpq.kulkan.client;

import com.ghostofpq.kulkan.client.scenes.LobbyScene;
import com.ghostofpq.kulkan.client.scenes.LoginScene;
import com.ghostofpq.kulkan.client.scenes.Scene;
import com.ghostofpq.kulkan.client.utils.FontManager;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.client.utils.TextureManager;
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

    public Client() {

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

        clientContext.init();

        this.requestClose = false;
        this.requestUpdateDisplayMode = false;
        this.lastTimeTick = Sys.getTime();

        try {
            Display.setDisplayMode(new DisplayMode(clientContext.getCurrentResolution().getWidth(), clientContext.getCurrentResolution().getHeight()));
            Display.setSwapInterval(1);
            Display.sync(60);
            Display.create();
            clientContext.setFullscreen(Display.isFullscreen());
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        initGL();

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
            Display.setDisplayMode(new DisplayMode(clientContext.getCurrentResolution().getWidth(), clientContext.getCurrentResolution().getHeight()));
            Display.setSwapInterval(1);
            Display.sync(60);

            TextureManager.getInstance().reload();
            FontManager.getInstance().reload();
            initGL();
            GraphicsManager.getInstance().ready3D();
            clientContext.setFullscreen(Display.isFullscreen());
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }
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
        GL11.glViewport(0, 0, clientContext.getCurrentResolution().getWidth(), clientContext.getCurrentResolution().getHeight());
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glOrtho(0.0D, clientContext.getCurrentResolution().getWidth(), 0.0D, clientContext.getCurrentResolution().getHeight(), 1.0D, -1.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GraphicsManager.getInstance().ready3D();
    }

    private long deltaTimeInMillis() {
        long delta = Sys.getTime() - lastTimeTick;
        if (delta >= 50) {
            log.warn("Delta time is long : {}", delta);
        }
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
        return Display.getHeight();
    }

    public int getWidth() {
        return Display.getWidth();
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(Scene currentScene) {
        this.currentScene = currentScene;
        this.currentScene.init();
    }

    public Player getPlayer() {
        return clientContext.getPlayer();
    }

    public void setPlayer(Player player) {
        this.player = player;
        clientContext.setPlayer(player);
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

    public void setDisplayMode(int width, int height, boolean fullscreen) {
        if ((Display.getDisplayMode().getWidth() == width) &&
                (Display.getDisplayMode().getHeight() == height) &&
                (Display.isFullscreen() == fullscreen)) {
            return;
        }
        try {
            DisplayMode targetDisplayMode = null;
            if (fullscreen) {
                DisplayMode[] modes = Display.getAvailableDisplayModes();
                int freq = 0;
                for (int i = 0; i < modes.length; i++) {
                    DisplayMode current = modes[i];
                    if ((current.getWidth() == width) && (current.getHeight() == height)) {
                        if (((targetDisplayMode == null) || (current.getFrequency() >= freq)) && (
                                (targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel()))) {
                            targetDisplayMode = current;
                            freq = targetDisplayMode.getFrequency();
                        }
                        if ((current.getBitsPerPixel() != Display.getDesktopDisplayMode().getBitsPerPixel()) ||
                                (current.getFrequency() != Display.getDesktopDisplayMode().getFrequency())) continue;
                        targetDisplayMode = current;
                        break;
                    }
                }
            } else {
                targetDisplayMode = new DisplayMode(width, height);
            }
            if (targetDisplayMode == null) {
                System.out.println("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
                return;
            }
            Display.setDisplayMode(targetDisplayMode);
            Display.setFullscreen(fullscreen);
        } catch (LWJGLException e) {
            System.out.println("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + e);
        }
    }


    public LobbyScene getLobbyScene() {
        return lobbyScene;
    }
}
