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
    private static volatile Client instance = null;
    private final String CLIENT_QUEUE_NAME_BASE = "/client/";
    //SPRING
    private String hostIp;
    private int hostPort;
    private int height;
    private int width;
    private String clientQueueName;
    private Channel channelIn;
    private Scene currentScene;
    private Player player;
    private String tokenKey;
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
        ApplicationContext context = new ClassPathXmlApplicationContext("client.xml");
        Client g = ((Client) context.getBean("client"));

        g.init();
        g.setCurrentScene(LoginScene.getInstance());
        g.run();
    }

    public void init() {
        log.debug("HOST : {}", hostIp);

        setHeight(600);
        setWidth(800);
        if (instance == null) {
            instance = this;
        }
        this.requestClose = false;
        this.lastTimeTick = Sys.getTime();
        try {
            // DisplayMode[] displayModes = Display.getAvailableDisplayModes();
            // for (int i = 0; i < displayModes.length; i++) {
            //     log.debug("[{}]: {}x{} bbp:{} freq:{}  {}",i, displayModes[i].getHeight(), displayModes[i].getWidth()
            //             , displayModes[i].getBitsPerPixel(), displayModes[i].getFrequency(), displayModes[i].isFullscreenCapable());
            // }

            Display.setDisplayMode(new DisplayMode(800, 600));
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
        factory.setPort(hostPort);
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
        }
        return result;
    }

    public void run() {
        while (!requestClose) {
            update(deltaTimeInMillis());
            currentScene.manageInput();
            currentScene.receiveMessage();
            render();
            lastTimeTick = Sys.getTime();
            while (deltaTimeInMillis() <= 4) {
                // waiting for at least 10 millis
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

    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }
}
