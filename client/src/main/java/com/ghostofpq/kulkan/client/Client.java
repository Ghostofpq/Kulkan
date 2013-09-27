package com.ghostofpq.kulkan.client;

import com.ghostofpq.kulkan.client.scenes.LoginScene;
import com.ghostofpq.kulkan.client.scenes.Scene;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.entities.character.Player;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.IOException;

@Slf4j
public class Client {
    private static volatile Client instance = null;
    private final String HOST = "localhost";
    private final String QUEUE_NAME = "hello";
    private Scene currentScene;
    private Player player;
    private long lastTimeTick;
    private int height;
    private int width;
    private boolean requestClose;
    private QueueingConsumer consumer;
    private Connection connection;
    private Channel channel;

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

        System.setProperty("org.lwjgl.librarypath", new File("client/target/natives/").getAbsolutePath());
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

        try {
            initConnection();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void initConnection() throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        log.debug(" [*] Waiting for messages. To exit press CTRL+C");
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }

    public void sendMessage(String message) throws InterruptedException, IOException {
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        log.debug(" [x] Sent '{}'", message);
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
        try {
            channel.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
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

    public void sendRequest() {
        //Todo
    }
}
