package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ManageJobScene implements Scene {
    private static volatile ManageJobScene instance = null;
    private final String USER_SERVICE_QUEUE_NAME = "users";
    private GameCharacter gameCharacter;
    private Channel channelOut;

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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void manageInput() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void closeConnections() throws IOException {
        channelOut.close();
    }

    @Override
    public void receiveMessage() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
