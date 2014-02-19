package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.ClientContext;
import com.ghostofpq.kulkan.client.ClientMessenger;
import com.ghostofpq.kulkan.client.graphics.HUD.Button;
import com.ghostofpq.kulkan.client.graphics.HUD.TextArea;
import com.ghostofpq.kulkan.client.graphics.JobManager;
import com.ghostofpq.kulkan.client.graphics.KeyValueRender;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.job.JobType;
import com.ghostofpq.kulkan.entities.job.capacity.Capacity;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.user.MessagePlayerUpdate;
import com.ghostofpq.kulkan.entities.messages.user.MessageUnlockCapacity;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.input.Mouse;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ManageGameCharacterJobScene implements Scene {
    private JobManager jobManager;
    private KeyValueRender jobType;
    private KeyValueRender jobPoints;
    private KeyValueRender cumulatedJobPoints;
    private KeyValueRender selectedCapacityName;
    private KeyValueRender capacityPrice;
    private Button unlockCapacity;
    private Button quitButton;
    private Capacity selectedCapacity;
    private TextArea capacityDescription;


    @Autowired
    private ClientContext clientContext;
    @Autowired
    private Client client;
    @Autowired
    private ManageGameCharacterScene manageGameCharacterScene;
    @Autowired
    private ClientMessenger clientMessenger;

    private int widthSeparator;
    private int heightSeparator;

    public ManageGameCharacterJobScene() {
    }


    @Override
    public void init() {
        GameCharacter gameCharacter = clientContext.getSelectedGameCharacter();
        int widthStep = (client.getWidth() - 3 * widthSeparator) / 5;
        int heightStep = (client.getHeight() - 3 * heightSeparator) / 8;
        jobManager = new JobManager(widthSeparator, heightStep + 2 * heightSeparator, (3 * widthStep), 7 * heightStep, gameCharacter.getJob(gameCharacter.getCurrentJob()));
        widthSeparator = client.getWidth() / 20;
        heightSeparator = client.getHeight() / 20;
        selectedCapacity = null;
        widthStep = (client.getWidth() - 5 * widthSeparator) / 5;
        heightStep = (client.getHeight() - 6 * heightSeparator) / 8;
        jobType = new KeyValueRender(widthSeparator, heightSeparator, widthStep, heightStep, "JOB", String.valueOf(gameCharacter.getCurrentJob()), 3);
        jobPoints = new KeyValueRender(widthSeparator * 2 + widthStep, heightSeparator, widthStep, heightStep, "JP", String.valueOf(gameCharacter.getJob(gameCharacter.getCurrentJob()).getJobPoints()), 7);
        cumulatedJobPoints = new KeyValueRender(widthSeparator * 3 + 2 * widthStep, heightSeparator, widthStep, heightStep, "TOTAL", String.valueOf(gameCharacter.getJob(gameCharacter.getCurrentJob()).getCumulativeJobPoints()), 7);
        selectedCapacityName = new KeyValueRender(widthSeparator * 4 + 3 * widthStep, heightSeparator, widthStep * 2, heightStep, "Capacity", "0", 5);

        capacityDescription = new TextArea(widthSeparator * 4 + 3 * widthStep, heightSeparator * 2 + heightStep, widthStep * 2, heightStep, "optimus_princeps_16");


        capacityPrice = new KeyValueRender(widthSeparator * 4 + 3 * widthStep, heightSeparator * 3 + 5 * heightStep, widthStep * 2, heightStep, "Price", "0", 5);

        unlockCapacity = new Button(widthSeparator * 4 + 3 * widthStep, heightSeparator * 4 + 6 * heightStep, widthStep * 2, heightStep, "Unlock Capacity") {
            @Override
            public void onClick() {
                unlockSelectedCapacity();
            }
        };

        quitButton = new
                Button(widthSeparator * 4 + 3 * widthStep, heightSeparator * 5 + 7 * heightStep, widthStep * 2, heightStep, "Back") {
                    @Override
                    public void onClick() {
                        client.setCurrentScene(manageGameCharacterScene);
                    }
                };
    }

    @Override
    public void update(long deltaTime) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void render() {
        jobManager.draw();
        jobType.draw();
        jobPoints.draw();
        cumulatedJobPoints.draw();
        quitButton.draw();

        if (null != selectedCapacity) {
            capacityPrice.draw();
            selectedCapacityName.draw();
            capacityDescription.draw();
            if (selectedCapacity.canBeUnlock(clientContext.getSelectedGameCharacter().getActiveJob().getJobPoints())) {
                unlockCapacity.draw();
            }
        }
    }

    @Override
    public void manageInput() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                if (quitButton.isClicked()) {
                    quitButton.onClick();
                } else if (unlockCapacity.isClicked()) {
                    if (null != selectedCapacity && selectedCapacity.canBeUnlock(clientContext.getSelectedGameCharacter().getActiveJob().getJobPoints())) {
                        unlockCapacity.onClick();
                    }
                } else if (jobManager.isClicked()) {
                    Capacity capacity = jobManager.clickedCapacity();
                    if (null != capacity) {
                        selectedCapacity = capacity;
                        selectedCapacityName.setValue(capacity.getName());
                        capacityDescription.clear();
                        capacityDescription.addLine(capacity.getDescription());
                        capacityPrice.setValue(String.valueOf(capacity.getPrice()));
                    }
                }
            }
        }
    }

    public void unlockSelectedCapacity() {
        if (null != selectedCapacity) {
            MessageUnlockCapacity messageUnlockCapacity = new MessageUnlockCapacity(clientContext.getTokenKey(), clientContext.getUsername(), clientContext.getSelectedCharacterId(), JobType.WARRIOR, selectedCapacity.getName());
            clientMessenger.sendMessageToUserService(messageUnlockCapacity);
        }
    }

    @Override
    public void receiveMessage() {
        Message message = clientMessenger.receiveMessage();
        if (null != message) {
            switch (message.getType()) {
                case PLAYER_UPDATE:
                    log.debug("PLAYER_UPDATE");
                    MessagePlayerUpdate response = (MessagePlayerUpdate) message;
                    log.debug("CREATE OK");
                    clientContext.setPlayer(response.getPlayer());
                    init();
                    break;
            }
        }
    }
}
