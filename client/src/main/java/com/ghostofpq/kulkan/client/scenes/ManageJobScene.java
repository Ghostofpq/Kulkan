package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
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
import com.rabbitmq.client.Channel;
import org.lwjgl.input.Mouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ManageJobScene implements Scene {
    private static final Logger LOG = LoggerFactory.getLogger(ManageJobScene.class);
    private static volatile ManageJobScene instance = null;
    private final String USER_SERVICE_QUEUE_NAME = "users";
    private GameCharacter gameCharacter;
    private Channel channelOut;
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
    private int widthSeparator = Client.getInstance().getWidth() / 20;
    private int heightSeparator = Client.getInstance().getHeight() / 20;

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
        int widthStep = (Client.getInstance().getWidth() - 3 * widthSeparator) / 5;
        int heightStep = (Client.getInstance().getHeight() - 3 * heightSeparator) / 8;
        this.gameCharacter = gameCharacter;
        jobManager = new JobManager(widthSeparator, heightStep + 2 * heightSeparator, (3 * widthStep), 7 * heightStep, gameCharacter.getJob(gameCharacter.getCurrentJob()));
    }

    @Override
    public void initConnections() throws IOException {
        channelOut = Client.getInstance().getConnection().createChannel();
        channelOut.queueDeclare(USER_SERVICE_QUEUE_NAME, false, false, false, null);
    }

    @Override
    public void init() {
        selectedCapacity = null;
        int widthStep = (Client.getInstance().getWidth() - 5 * widthSeparator) / 5;
        int heightStep = (Client.getInstance().getHeight() - 6 * heightSeparator) / 8;
        jobType = new KeyValueRender(widthSeparator, heightSeparator, widthStep, heightStep, "JOB", String.valueOf(gameCharacter.getCurrentJob()), 3);
        jobPoints = new KeyValueRender(widthSeparator * 2 + widthStep, heightSeparator, widthStep, heightStep, "JP", String.valueOf(gameCharacter.getJob(gameCharacter.getCurrentJob()).getJobPoints()), 7);
        cumulatedJobPoints = new KeyValueRender(widthSeparator * 3 + 2 * widthStep, heightSeparator, widthStep, heightStep, "TOTAL", String.valueOf(gameCharacter.getJob(gameCharacter.getCurrentJob()).getCumulativeJobPoints()), 7);
        selectedCapacityName = new KeyValueRender(widthSeparator * 4 + 3 * widthStep, heightSeparator, widthStep * 2, heightStep, "Capacity", "0", 5);

        capacityDescription = new TextArea(widthSeparator * 4 + 3 * widthStep, heightSeparator * 2 + heightStep, 15, 10);


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
                        Client.getInstance().setCurrentScene(GameCharacterManageScene.getInstance());
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
            if (selectedCapacity.canBeUnlock(gameCharacter.getJob(gameCharacter.getCurrentJob()).getJobPoints())) {
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
                    if (null != selectedCapacity && selectedCapacity.canBeUnlock(gameCharacter.getJob(gameCharacter.getCurrentJob()).getJobPoints())) {
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
            MessageUnlockCapacity messageUnlockCapacity = new MessageUnlockCapacity(Client.getInstance().getTokenKey(), gameCharacter.getId(), JobType.WARRIOR, selectedCapacity.getName());
            try {
                channelOut.basicPublish("", USER_SERVICE_QUEUE_NAME, null, messageUnlockCapacity.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
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
                    LOG.debug("PLAYER_UPDATE");
                    MessagePlayerUpdate response = (MessagePlayerUpdate) message;
                    LOG.debug("CREATE OK");
                    Client.getInstance().setPlayer(response.getPlayer());
                    GameCharacter updatedGameCharacter = response.getPlayer().getGameCharWithId(gameCharacter.getId());
                    setGameCharacter(updatedGameCharacter);
                    GameCharacterManageScene.getInstance().setGameCharacter(updatedGameCharacter);
                    Client.getInstance().setCurrentScene(instance);
                    break;
            }
        }
    }
}
