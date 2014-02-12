package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.ClientContext;
import com.ghostofpq.kulkan.client.ClientMessenger;
import com.ghostofpq.kulkan.client.graphics.HUD.Button;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.job.JobType;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.user.MessageChangeJob;
import com.ghostofpq.kulkan.entities.messages.user.MessagePlayerUpdate;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.lwjgl.input.Mouse;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ManageGameCharacterChangeJobScene implements Scene {

    private ObjectId gameCharId;
    private Button warriorButton;
    private Button mageButton;
    private Button quitButton;
    @Autowired
    private Client client;
    @Autowired
    private ClientContext clientContext;
    @Autowired
    private ClientMessenger clientMessenger;
    @Autowired
    private ManageGameCharacterScene manageGameCharacterScene;

    public ManageGameCharacterChangeJobScene() {
    }

    public void setGameCharacter(GameCharacter gameCharacter) {
        this.gameCharId = gameCharacter.getId();
    }

    @Override
    public void init() {
        int widthSeparator = client.getWidth() / 20;
        int heightSeparator = client.getHeight() / 20;

        int widthStep = (client.getWidth() - 5 * widthSeparator) / 4;
        int heightStep = (client.getHeight() - 5 * heightSeparator) / 4;

        warriorButton = new Button(widthSeparator, heightSeparator, widthStep, heightStep, "Warrior") {
            @Override
            public void onClick() {
                log.debug("Warrior");
                MessageChangeJob messageChangeJob = new MessageChangeJob(client.getTokenKey(), gameCharId, JobType.WARRIOR);
                clientMessenger.sendMessageToUserService(messageChangeJob);
            }
        };

        mageButton = new Button(widthSeparator * 2 + widthStep, heightSeparator, widthStep, heightStep, "Mage") {
            @Override
            public void onClick() {
                ManageGameCharacterChangeJobScene.log.debug("Mage");
                MessageChangeJob messageChangeJob = new MessageChangeJob(client.getTokenKey(), gameCharId, JobType.MAGE);
                clientMessenger.sendMessageToUserService(messageChangeJob);
            }
        };

        quitButton = new Button(widthSeparator * 4 + widthStep * 3, heightSeparator * 4 + heightStep * 3, widthStep, heightStep, "Back") {
            @Override
            public void onClick() {
                client.setCurrentScene(manageGameCharacterScene);
            }
        };

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
    public void receiveMessage() {
        Message message = clientMessenger.receiveMessage();
        if (null != message) {
            switch (message.getType()) {
                case PLAYER_UPDATE:
                    ManageGameCharacterChangeJobScene.log.debug("PLAYER_UPDATE");
                    MessagePlayerUpdate response = (MessagePlayerUpdate) message;
                    client.setPlayer(response.getPlayer());
                    GameCharacter gameCharacter = response.getPlayer().getGameCharWithId(gameCharId);
                    clientContext.setSelectedGameCharacter(gameCharacter);
                    client.setCurrentScene(manageGameCharacterScene);
                    break;
            }
        }
    }
}
