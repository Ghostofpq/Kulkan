package com.ghostofpq.kulkan.client;

import com.ghostofpq.kulkan.client.scenes.LoginScene;
import com.ghostofpq.kulkan.client.scenes.Scene;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.messages.Message;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;

@Slf4j
public class Client {
    //SPRING
    private String hostIp;
    private int height;
    private int width;

    private static volatile Client instance = null;
    private final String CLIENT_QUEUE_NAME_BASE = "/client/";
    private final String GAME_SERVER_QUEUE_NAME_BASE = "/server/game";
    private String clientQueueName;
    private Channel channelGameOut;
    private Channel channelIn;
    private Scene currentScene;
    private Player player;
    private String tokenKey;
    private int lobbyNumber = 0;
    private int gameNumber;
    private long lastTimeTick;
    private boolean requestClose;
    private QueueingConsumer consumer;
    private Connection connection;


    private Client() {

    }

    public static Client getInstance() {
        return instance;
    }

    public static void main(String[] argv) {
        System.setProperty("org.lwjgl.librarypath", new File("natives").getAbsolutePath());
        ApplicationContext context = new ClassPathXmlApplicationContext("Client.xml");

        Client g = ((Client) context.getBean("Client"));
        g.init();
        g.setCurrentScene(LoginScene.getInstance());

        g.run();
    }

    public void init() {
        setHeight(600);
        setWidth(800);
        if (instance == null) {
            instance = this;
        }
        this.requestClose = false;
        this.lastTimeTick = Sys.getTime();
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
        factory.setHost(hostIp);
        connection = factory.newConnection();
    }

    public Message receiveMessage() {
        Message result = null;
        if (null != consumer) {
            try {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery(0);
                if (null != delivery) {
                    result = Message.loadFromBytes(delivery.getBody());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            log.debug("Authenticate yourself");
        }
        return result;
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
        try {
            if (null != channelIn) {
                channelIn.close();
                log.debug("channelIn closed");
            }
            currentScene.closeConnections();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
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

    public Connection getConnection() {
        return connection;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
        try {
            clientQueueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(tokenKey).toString();
            channelIn = connection.createChannel();
            channelIn.queueDeclare(clientQueueName, false, false, false, null);
            consumer = new QueueingConsumer(channelIn);
            channelIn.basicConsume(clientQueueName, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
