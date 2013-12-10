package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.Button;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.user.MessagePlayerUpdate;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.input.Mouse;

import java.io.IOException;

@Slf4j
public class ChangeJobScene implements Scene {
    private static volatile ChangeJobScene instance = null;
    private final String USER_SERVICE_QUEUE_NAME = "users";
    private Channel channelOut;
    private Button warriorButton;
    private Button mageButton;
    private Button quitButton;

    private ChangeJobScene() {
    }

    public static ChangeJobScene getInstance() {
        if (instance == null) {
            synchronized (ChangeJobScene.class) {
                if (instance == null) {
                    instance = new ChangeJobScene();
                }
            }
        }
        return instance;
    }

    @Override
    public void init() {
        int widthSeparator = Client.getInstance().getWidth() / 20;
        int heightSeparator = Client.getInstance().getHeight() / 20;

        int widthStep = (Client.getInstance().getWidth() - 5 * widthSeparator) / 4;
        int heightStep = (Client.getInstance().getHeight() - 5 * heightSeparator) / 4;

        warriorButton = new Button(widthSeparator, heightSeparator, widthStep, heightStep, "Warrior") {
            @Override
            public void onClick() {
                log.debug("Warrior");
            }
        };

        mageButton = new Button(widthSeparator * 2 + widthStep, heightSeparator, widthStep, heightStep, "Mage") {
            @Override
            public void onClick() {
                log.debug("Mage");
            }
        };

        quitButton = new Button(widthSeparator * 4 + widthStep * 3, heightSeparator * 4 + heightStep * 3, widthStep, heightStep, "Back") {
            @Override
            public void onClick() {
                Client.getInstance().setCurrentScene(TeamManagementScene.getInstance());
            }
        };

    }

    @Override
    public void initConnections() throws IOException {
        channelOut = Client.getInstance().getConnection().createChannel();
        channelOut.queueDeclare(USER_SERVICE_QUEUE_NAME, false, false, false, null);
    }

    @Override
    public void update(long deltaTime) {
    }

    @Override
    public void render() {
        warriorButton.draw();
        mageButton.draw();
        quitButton.draw();
    }

    @Override
    public void manageInput() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                if (warriorButton.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    warriorButton.onClick();
                }
                if (mageButton.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    mageButton.onClick();
                }
                if (quitButton.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    quitButton.onClick();
                }
            }
        }
    }

    @Override
    public void closeConnections() throws IOException {
        channelOut.close();
        log.debug("channelOut closed");
    }

    @Override
    public void receiveMessage() {
        Message message = Client.getInstance().receiveMessage();
        if (null != message) {
            switch (message.getType()) {
                case PLAYER_UPDATE:
                    log.debug("PLAYER_UPDATE");
                    MessagePlayerUpdate response = (MessagePlayerUpdate) message;
                    Client.getInstance().setPlayer(response.getPlayer());
                    Client.getInstance().setCurrentScene(TeamManagementScene.getInstance());
                    break;
            }
        }
    }
}
