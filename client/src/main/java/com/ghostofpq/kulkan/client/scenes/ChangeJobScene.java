package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.HUD.Button;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.job.JobType;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.user.MessageChangeJob;
import com.ghostofpq.kulkan.entities.messages.user.MessagePlayerUpdate;
import com.rabbitmq.client.Channel;
import org.lwjgl.input.Mouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ChangeJobScene implements Scene {
    private static final Logger LOG = LoggerFactory.getLogger(ChangeJobScene.class);
    private static volatile ChangeJobScene instance = null;
    private final String USER_SERVICE_QUEUE_NAME = "server/users";
    private GameCharacter gameCharacter;
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

    public void setGameCharacter(GameCharacter gameCharacter) {
        this.gameCharacter = gameCharacter;
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
                LOG.debug("Warrior");
                MessageChangeJob messageChangeJob = new MessageChangeJob(Client.getInstance().getTokenKey(), gameCharacter.getId(), JobType.WARRIOR);
                try {
                    channelOut.basicPublish("", USER_SERVICE_QUEUE_NAME, null, messageChangeJob.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        mageButton = new

                Button(widthSeparator * 2 + widthStep, heightSeparator, widthStep, heightStep, "Mage") {
                    @Override
                    public void onClick() {
                        LOG.debug("Mage");
                        MessageChangeJob messageChangeJob = new MessageChangeJob(Client.getInstance().getTokenKey(), gameCharacter.getId(), JobType.MAGE);
                        try {
                            channelOut.basicPublish("", USER_SERVICE_QUEUE_NAME, null, messageChangeJob.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

        quitButton = new

                Button(widthSeparator * 4 + widthStep * 3, heightSeparator * 4 + heightStep * 3, widthStep, heightStep, "Back") {
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
                if (warriorButton.isClicked()) {
                    warriorButton.onClick();
                }
                if (mageButton.isClicked()) {
                    mageButton.onClick();
                }
                if (quitButton.isClicked()) {
                    quitButton.onClick();
                }
            }
        }
    }

    @Override
    public void closeConnections() throws IOException {
        channelOut.close();
        LOG.debug("channelOut closed");
    }

    @Override
    public void receiveMessage() {
        Message message = Client.getInstance().receiveMessage();
        if (null != message) {
            switch (message.getType()) {
                case PLAYER_UPDATE:
                    LOG.debug("PLAYER_UPDATE");
                    MessagePlayerUpdate response = (MessagePlayerUpdate) message;
                    Client.getInstance().setPlayer(response.getPlayer());
                    Client.getInstance().setCurrentScene(TeamManagementScene.getInstance());
                    break;
            }
        }
    }
}
