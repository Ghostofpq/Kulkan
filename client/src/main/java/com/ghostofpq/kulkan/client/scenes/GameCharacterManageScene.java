package com.ghostofpq.kulkan.client.scenes;


import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.HUD.Button;
import com.ghostofpq.kulkan.client.graphics.KeyValueRender;
import com.ghostofpq.kulkan.client.graphics.PrimaryCharacteristicsRender;
import com.ghostofpq.kulkan.client.graphics.SecondaryCharacteristicsRender;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.user.*;
import com.rabbitmq.client.Channel;
import org.lwjgl.input.Mouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GameCharacterManageScene implements Scene {
    private static final Logger LOG = LoggerFactory.getLogger(GameCharacterManageScene.class);
    private static volatile GameCharacterManageScene instance = null;
    private final String USER_SERVICE_QUEUE_NAME = "users";
    private Channel channelOut;
    private GameCharacter gameCharacter;
    private Button manageJobButton;
    private Button manageEquipementButton;
    private Button changeJobButton;
    private Button quitButton;
    private Button deleteGameCharButton;
    private Button putInTeam;
    private Button putInStock;
    private int widthSeparator = 50;
    private int widthStep;
    private int heightSeparator = 50;
    private int heightStep;
    private PrimaryCharacteristicsRender primaryCharacteristicsRender;
    private SecondaryCharacteristicsRender secondaryCharacteristicsRender;
    private KeyValueRender hpRender;
    private KeyValueRender mpRender;
    private KeyValueRender xpRender;
    private KeyValueRender lvlRender;
    private KeyValueRender currentJobRender;
    private KeyValueRender jobPoints;

    private GameCharacterManageScene() {
    }

    public static GameCharacterManageScene getInstance() {
        if (instance == null) {
            synchronized (GameCharacterManageScene.class) {
                if (instance == null) {
                    instance = new GameCharacterManageScene();
                }
            }
        }
        return instance;
    }

    @Override
    public void init() {
        widthSeparator = Client.getInstance().getWidth() / 20;
        heightSeparator = Client.getInstance().getHeight() / 20;
        widthStep = (Client.getInstance().getWidth() - 3 * widthSeparator) / 4;
        heightStep = (Client.getInstance().getHeight() - 4 * heightSeparator) / 8;

        primaryCharacteristicsRender = new PrimaryCharacteristicsRender(widthSeparator, heightSeparator, widthStep * 2, heightStep * 3, gameCharacter.getAggregatedCharacteristics());
        secondaryCharacteristicsRender = new SecondaryCharacteristicsRender(2 * widthSeparator + widthStep * 2, heightSeparator, widthStep * 2, heightStep * 5, gameCharacter.getAggregatedSecondaryCharacteristics());

        hpRender = new KeyValueRender(widthSeparator, heightSeparator + heightStep * 3, widthStep, heightStep, "HP", String.valueOf(gameCharacter.getMaxHealthPoint()), 5);
        mpRender = new KeyValueRender(widthSeparator + widthStep, heightSeparator + heightStep * 3, widthStep, heightStep, "MP", String.valueOf(gameCharacter.getMaxManaPoint()), 5);
        xpRender = new KeyValueRender(widthSeparator, heightSeparator + heightStep * 4, widthStep, heightStep, "XP", String.valueOf(gameCharacter.getExperience()), 5);
        lvlRender = new KeyValueRender(widthSeparator + widthStep, heightSeparator + heightStep * 4, widthStep, heightStep, "LVL", String.valueOf(gameCharacter.getLevel()), 5);
        currentJobRender = new KeyValueRender(widthSeparator, heightSeparator + heightStep * 5, widthStep, heightStep, "Job", String.valueOf(gameCharacter.getJob(gameCharacter.getCurrentJob()).getName()), 5);
        jobPoints = new KeyValueRender(widthSeparator + widthStep, heightSeparator + heightStep * 5, widthStep, heightStep, "JP", String.valueOf(gameCharacter.getJob(gameCharacter.getCurrentJob()).getJobPoints()), 5);
        manageJobButton = new Button(widthSeparator, heightSeparator + heightStep * 6, widthStep, heightStep, "Manage Job") {
            @Override
            public void onClick() {

                Client.getInstance().setCurrentScene(ManageJobScene.getInstance());
                LOG.debug("manageJobButton");
            }
        };

        changeJobButton = new

                Button(widthSeparator + widthStep, heightSeparator + heightStep * 6, widthStep, heightStep, "Change Job") {
                    @Override
                    public void onClick() {
                        Client.getInstance().setCurrentScene(ChangeJobScene.getInstance());
                    }
                };

        manageEquipementButton = new

                Button(widthSeparator + 2 * widthStep, heightSeparator + heightStep * 6, widthStep, heightStep, "Manage Stuff") {
                    @Override
                    public void onClick() {
                        ManageEquipementScene.getInstance().setGameCharacter(gameCharacter);
                        Client.getInstance().setCurrentScene(ManageEquipementScene.getInstance());
                    }
                };

        deleteGameCharButton = new

                Button(widthSeparator, heightSeparator + heightStep * 8, widthStep, heightStep, "Delete Char") {
                    @Override
                    public void onClick() {
                        LOG.debug("Sending a DeleteGameCharacterRequest");
                        LOG.debug("Name : '{}'", gameCharacter.getName());
                        try {
                            LOG.debug("Sending ");
                            Player player = Client.getInstance().getPlayer();
                            if (player.getTeam().contains(gameCharacter)) {
                                MessageDeleteGameCharacterFromTeam messageDeleteGameCharacterFromTeam = new MessageDeleteGameCharacterFromTeam(Client.getInstance().getTokenKey(), player.getPseudo(), gameCharacter.getId());
                                channelOut.basicPublish("", USER_SERVICE_QUEUE_NAME, null, messageDeleteGameCharacterFromTeam.getBytes());
                            } else {
                                MessageDeleteGameCharacterFromStock messageDeleteGameCharacterFromStock = new MessageDeleteGameCharacterFromStock(Client.getInstance().getTokenKey(), player.getPseudo(), gameCharacter.getId());
                                channelOut.basicPublish("", USER_SERVICE_QUEUE_NAME, null, messageDeleteGameCharacterFromStock.getBytes());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

        quitButton = new

                Button(widthSeparator + widthStep, heightSeparator + heightStep * 8, widthStep, heightStep, "Back") {
                    @Override
                    public void onClick() {
                        Client.getInstance().setCurrentScene(TeamManagementScene.getInstance());
                    }
                };

        putInTeam = new

                Button(widthSeparator + widthStep, heightSeparator + heightStep * 7, widthStep, heightStep, "Team") {
                    @Override
                    public void onClick() {
                        LOG.debug("Sending a PutInTeamRequest");
                        LOG.debug("Name : '{}'", gameCharacter.getName());
                        try {
                            LOG.debug("Sending ");
                            Player player = Client.getInstance().getPlayer();
                            MessagePutGameCharacterFromStockToTeam messagePutGameCharacterFromStockToTeam = new MessagePutGameCharacterFromStockToTeam(Client.getInstance().getTokenKey(), player.getPseudo(), gameCharacter.getId());
                            channelOut.basicPublish("", USER_SERVICE_QUEUE_NAME, null, messagePutGameCharacterFromStockToTeam.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
        putInStock = new

                Button(widthSeparator, heightSeparator + heightStep * 7, widthStep, heightStep, "Stock") {
                    @Override
                    public void onClick() {
                        LOG.debug("Sending a PutInTeamRequest");
                        LOG.debug("Name : '{}'", gameCharacter.getName());
                        try {
                            LOG.debug("Sending ");
                            Player player = Client.getInstance().getPlayer();
                            MessagePutGameCharacterFromTeamToStock putGameCharacterFromTeamToStock = new MessagePutGameCharacterFromTeamToStock(Client.getInstance().getTokenKey(), player.getPseudo(), gameCharacter.getId());
                            channelOut.basicPublish("", USER_SERVICE_QUEUE_NAME, null, putGameCharacterFromTeamToStock.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
        primaryCharacteristicsRender.draw();
        secondaryCharacteristicsRender.draw();
        hpRender.draw();
        mpRender.draw();
        xpRender.draw();
        lvlRender.draw();
        manageJobButton.draw();
        manageEquipementButton.draw();
        deleteGameCharButton.draw();
        quitButton.draw();
        currentJobRender.draw();
        jobPoints.draw();
        changeJobButton.draw();
        if (isInTeam()) {
            putInStock.draw();
        } else {
            putInTeam.draw();
        }

    }

    @Override
    public void manageInput() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                if (quitButton.isClicked()) {
                    quitButton.onClick();
                }
                if (deleteGameCharButton.isClicked()) {
                    deleteGameCharButton.onClick();
                }
                if (manageEquipementButton.isClicked()) {
                    manageEquipementButton.onClick();
                }
                if (manageJobButton.isClicked()) {
                    manageJobButton.onClick();
                }
                if (isInTeam()) {
                    if (putInStock.isClicked()) {
                        putInStock.onClick();
                    }
                } else {
                    if (putInTeam.isClicked()) {
                        putInTeam.onClick();
                    }
                }
                if (changeJobButton.isClicked()) {
                    changeJobButton.onClick();
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
                    LOG.debug("PLAYER_UPDATE");
                    MessagePlayerUpdate response = (MessagePlayerUpdate) message;
                    LOG.debug("CREATE OK");
                    Client.getInstance().setPlayer(response.getPlayer());
                    setGameCharacter(response.getPlayer().getGameCharWithId(gameCharacter.getId()));
                    Client.getInstance().setCurrentScene(instance);
                    break;
            }
        }
    }

    private boolean isInTeam() {
        return Client.getInstance().getPlayer().getTeam().contains(gameCharacter);
    }

    public void setGameCharacter(GameCharacter gameCharacter) {
        this.gameCharacter = gameCharacter;
        ManageJobScene.getInstance().setGameCharacter(gameCharacter);
        ChangeJobScene.getInstance().setGameCharacter(gameCharacter);
    }
}
