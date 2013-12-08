package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.JobManager;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.job.JobType;
import com.ghostofpq.kulkan.entities.job.capacity.Capacity;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.user.MessagePlayerUpdate;
import com.ghostofpq.kulkan.entities.messages.user.MessageUnlockCapacity;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.input.Mouse;

import java.io.IOException;

@Slf4j
public class ManageJobScene implements Scene {
    private static volatile ManageJobScene instance = null;
    private final String USER_SERVICE_QUEUE_NAME = "users";
    private GameCharacter gameCharacter;
    private Channel channelOut;
    private JobManager warriorJobManager;

    private ManageJobScene() {
    }

    public static ManageJobScene getInstance() {
        if (instance == null) {
            synchronized (ManageJobScene.class) {
                if (instance == null) {
                    instance = new ManageJobScene();
                }
            }
        }
        return instance;
    }

    public void setGameCharacter(GameCharacter gameCharacter) {
        this.gameCharacter = gameCharacter;
        warriorJobManager = new JobManager(0, 0, (2 * Client.getInstance().getWidth() / 3), Client.getInstance().getHeight(), gameCharacter.getJob(gameCharacter.getCurrentJob()));
    }

    @Override
    public void initConnections() throws IOException {
        channelOut = Client.getInstance().getConnection().createChannel();
        channelOut.queueDeclare(USER_SERVICE_QUEUE_NAME, false, false, false, null);
    }

    @Override
    public void init() {
    }

    @Override
    public void update(long deltaTime) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void render() {
        warriorJobManager.draw();
    }

    @Override
    public void manageInput() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                if (warriorJobManager.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    Capacity capacity = warriorJobManager.clickedCapacity(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY());
                    if (null != capacity) {
                        MessageUnlockCapacity messageUnlockCapacity = new MessageUnlockCapacity(Client.getInstance().getTokenKey(), gameCharacter.getName(), JobType.WARRIOR, capacity.getName());
                        try {
                            channelOut.basicPublish("", USER_SERVICE_QUEUE_NAME, null, messageUnlockCapacity.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void closeConnections() throws IOException {
        channelOut.close();
    }

    @Override
    public void receiveMessage() {
        Message message = Client.getInstance().receiveMessage();
        if (null != message) {
            switch (message.getType()) {
                case PLAYER_UPDATE:
                    log.debug("PLAYER_UPDATE");
                    MessagePlayerUpdate response = (MessagePlayerUpdate) message;
                    log.debug("CREATE OK");
                    Client.getInstance().setPlayer(response.getPlayer());
                    setGameCharacter(response.getPlayer().getGameCharWithId(gameCharacter.getId()));
                    Client.getInstance().setCurrentScene(instance);
                    break;
            }
        }
    }
}
