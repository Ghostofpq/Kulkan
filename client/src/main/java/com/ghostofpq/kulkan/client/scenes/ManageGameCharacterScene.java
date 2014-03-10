package com.ghostofpq.kulkan.client.scenes;


import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.ClientContext;
import com.ghostofpq.kulkan.client.ClientMessenger;
import com.ghostofpq.kulkan.client.graphics.Background;
import com.ghostofpq.kulkan.client.graphics.HUD.*;
import com.ghostofpq.kulkan.client.graphics.KeyValueRender;
import com.ghostofpq.kulkan.client.graphics.SecondaryCharacteristicsRender;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.client.utils.TextureKey;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.user.*;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ManageGameCharacterScene implements Scene {
    private Button manageJobButton;
    private Button manageEquipmentButton;
    private Button quitButton;
    private Button deleteGameCharButton;
    private Button putInStock;
    private int widthSeparator = 50;
    private int widthStep;
    private int heightSeparator = 50;
    private int heightStep;
    private SecondaryCharacteristicsRender secondaryCharacteristicsRender;
    private KeyValueRender hpRender;
    private KeyValueRender mpRender;
    private KeyValueRender xpRender;
    private KeyValueRender lvlRender;
    private KeyValueRender currentJobRender;
    private KeyValueRender jobPoints;


    // OVERLAYS
    private HUDTexturedElement nameHolder;
    private TextZone name;
    private TextZone level;
    private HUDTexturedElement xpBackground;
    private HUDTexturedElement xp;
    private HUDTexturedElement menu;

    // BACKGROUND
    private Background background;
    // POPUP
    private PopUp popUp;
    // FRAME
    private Frame frame;
    private int x;
    private int y;
    private boolean frameClicked;

    @Autowired
    private ClientContext clientContext;
    @Autowired
    private Client client;
    @Autowired
    private LobbyScene lobbyScene;
    @Autowired
    private ClientMessenger clientMessenger;
    @Autowired
    private ManageGameCharacterEquipmentScene manageGameCharacterEquipmentScene;
    @Autowired
    private ManageGameCharacterJobScene manageGameCharacterJobScene;

    @Override
    public void init() {
        GameCharacter gameCharacter = clientContext.getSelectedGameCharacter();
        background = new Background(TextureKey.BACKGROUND_BASIC);

        // MENU
        int menuWidth = clientContext.getCurrentResolution().getWidth() / 8;
        int menuPosX = (clientContext.getCurrentResolution().getWidth() - menuWidth) / 2;
        int menuPosY = 0;
        int menuHeight = clientContext.getCurrentResolution().getHeight();
        menu = new HUDTexturedElement(menuPosX, menuPosY, menuWidth, menuHeight, TextureKey.MANAGE_CHAR_MENU, TextureKey.MANAGE_CHAR_MENU);

        // NAME HOLDER
        int nameHolderPosX = clientContext.getCurrentResolution().getWidth() / 64;
        int nameHolderPosY = (clientContext.getCurrentResolution().getHeight() * 3) / 10;
        int nameHolderWidth = menuPosX - nameHolderPosX;
        int nameHolderHeight = clientContext.getCurrentResolution().getWidth() / 12;
        nameHolder = new HUDTexturedElement(nameHolderPosX, nameHolderPosY, nameHolderWidth, nameHolderHeight, TextureKey.MANAGE_CHAR_NAME_HOLDER, TextureKey.MANAGE_CHAR_NAME_HOLDER);

        // NAME
        int nameWidth = (int) (145f * nameHolderWidth / 685f);
        int nameHeight = (int) (40 * nameHolderHeight / 120f);
        int namePosX = nameHolderPosX + (int) (288f * nameHolderWidth / 685f);
        int namePosY = nameHolderPosY + (int) (65f * nameHolderHeight / 120f);

        name = new TextZone(namePosX, namePosY, nameWidth, nameHeight, gameCharacter.getName());
        // name.setFontName("arial_12");
        name.setAlignment(TextAlignment.CENTER);

        // LEVEL
        int levelWidth = (int) (80f * nameHolderWidth / 685f);
        int levelHeight = (int) (30f * nameHolderHeight / 120f);
        int levelPosX = nameHolderPosX + (int) (320f * nameHolderWidth / 685f);
        int levelPosY = nameHolderPosY + (int) (10f * nameHolderHeight / 120f);

        level = new TextZone(levelPosX, levelPosY, levelWidth, levelHeight, String.valueOf(gameCharacter.getLevel()));
        //level.setFontName("arial_12");
        level.setAlignment(TextAlignment.CENTER);

        // XP BACKGROUND
        int xpBackgroundPosX = clientContext.getCurrentResolution().getWidth() / 64;
        int xpBackgroundPosY = nameHolderPosY + nameHolderHeight;
        int xpBackgroundWidth = menuPosX - xpBackgroundPosX;
        int xpBackgroundHeight = clientContext.getCurrentResolution().getWidth() / 30;
        xpBackground = new HUDTexturedElement(xpBackgroundPosX, xpBackgroundPosY, xpBackgroundWidth, xpBackgroundHeight, TextureKey.MANAGE_CHAR_XP_BACKGROUND, TextureKey.MANAGE_CHAR_XP_BACKGROUND);

        // XP
        int xpPosX = xpBackgroundPosX + (int) (37f * xpBackgroundWidth / 685f);
        int xpPosY = xpBackgroundPosY + (int) (7f * xpBackgroundHeight / 30f);
        float xpRatio = ((float) gameCharacter.getExperience() / (float) gameCharacter.getNextLevel());
        int xpWidth = (int) (xpRatio * (611f * xpBackgroundWidth / 685f));
        int xpHeight = (int) (11f * xpBackgroundHeight / 30f);
        xp = new HUDTexturedElement(xpPosX, xpPosY, xpWidth, xpHeight, TextureKey.COMMON_BLOOD, TextureKey.COMMON_BLOOD);


        frame = new Frame(0, 0, clientContext.getCurrentResolution().getWidth(), clientContext.getCurrentResolution().getHeight(), clientContext.getCurrentResolution().getWidth() / 64, clientContext.getCurrentResolution().getWidth() / 64, TextureKey.COMMON_EXT_FRAME);


        widthSeparator = client.getWidth() / 20;
        heightSeparator = client.getHeight() / 20;
        widthStep = (client.getWidth() - 3 * widthSeparator) / 4;
        heightStep = (client.getHeight() - 4 * heightSeparator) / 8;

        secondaryCharacteristicsRender = new SecondaryCharacteristicsRender(2 * widthSeparator + widthStep * 2, heightSeparator, widthStep * 2, heightStep * 5, gameCharacter.getAggregatedCharacteristics());

        hpRender = new KeyValueRender(widthSeparator, heightSeparator + heightStep * 3, widthStep, heightStep, "HP", String.valueOf(gameCharacter.getMaxHealthPoint()), 5);
        mpRender = new KeyValueRender(widthSeparator + widthStep, heightSeparator + heightStep * 3, widthStep, heightStep, "MP", String.valueOf(gameCharacter.getMaxManaPoint()), 5);
        xpRender = new KeyValueRender(widthSeparator, heightSeparator + heightStep * 4, widthStep, heightStep, "XP", String.valueOf(gameCharacter.getExperience()), 5);
        lvlRender = new KeyValueRender(widthSeparator + widthStep, heightSeparator + heightStep * 4, widthStep, heightStep, "LVL", String.valueOf(gameCharacter.getLevel()), 5);
        currentJobRender = new KeyValueRender(widthSeparator, heightSeparator + heightStep * 5, widthStep, heightStep, "Job", String.valueOf(gameCharacter.getJob(gameCharacter.getCurrentJob()).getName()), 5);
        jobPoints = new KeyValueRender(widthSeparator + widthStep, heightSeparator + heightStep * 5, widthStep, heightStep, "JP", String.valueOf(gameCharacter.getJobPoints()), 5);
        manageJobButton = new Button(widthSeparator, heightSeparator + heightStep * 6, widthStep, heightStep, "Manage Job") {
            @Override
            public void onClick() {
                client.setCurrentScene(manageGameCharacterJobScene);
                log.debug("manageJobButton");
            }
        };

        manageEquipmentButton = new Button(widthSeparator + 2 * widthStep, heightSeparator + heightStep * 6, widthStep, heightStep, "Manage Stuff") {
            @Override
            public void onClick() {
                client.setCurrentScene(manageGameCharacterEquipmentScene);
            }
        };

        deleteGameCharButton = new Button(widthSeparator, heightSeparator + heightStep * 8, widthStep, heightStep, "Delete Char") {
            @Override
            public void onClick() {
                actionDelete();
            }
        };

        quitButton = new Button(widthSeparator + widthStep, heightSeparator + heightStep * 8, widthStep, heightStep, "Back") {
            @Override
            public void onClick() {
                actionBack();
            }
        };

        putInStock = new Button(widthSeparator, heightSeparator + heightStep * 7, widthStep, heightStep, "Stock") {
            @Override
            public void onClick() {
                actionStock();
            }
        };
    }

    private void actionBack() {
        client.setCurrentScene(lobbyScene);
    }

    private void actionDelete() {
        Player player = clientContext.getPlayer();
        GameCharacter gameCharacter = clientContext.getSelectedGameCharacter();
        if (player.getTeam().contains(gameCharacter)) {
            MessageDeleteGameCharacterFromTeam messageDeleteGameCharacterFromTeam = new MessageDeleteGameCharacterFromTeam(clientContext.getTokenKey(), player.getPseudo(), gameCharacter.getId());
            clientMessenger.sendMessageToUserService(messageDeleteGameCharacterFromTeam);
        } else {
            MessageDeleteGameCharacterFromStock messageDeleteGameCharacterFromStock = new MessageDeleteGameCharacterFromStock(clientContext.getTokenKey(), player.getPseudo(), gameCharacter.getId());
            clientMessenger.sendMessageToUserService(messageDeleteGameCharacterFromStock);
        }
    }

    private void actionStock() {
        Player player = clientContext.getPlayer();
        GameCharacter gameCharacter = clientContext.getSelectedGameCharacter();
        MessagePutGameCharacterFromTeamToStock putGameCharacterFromTeamToStock = new MessagePutGameCharacterFromTeamToStock(clientContext.getTokenKey(), player.getPseudo(), gameCharacter.getId());
        clientMessenger.sendMessageToUserService(putGameCharacterFromTeamToStock);
    }

    @Override
    public void update(long deltaTime) {
    }

    @Override
    public void render() {
        GraphicsManager.getInstance().make2D();
        background.draw();
        menu.draw();
        nameHolder.draw();
        name.draw();
        level.draw();
        xpBackground.draw();
        xp.draw();
        secondaryCharacteristicsRender.draw();
        hpRender.draw();
        mpRender.draw();
        xpRender.draw();
        lvlRender.draw();
        manageJobButton.draw();
        manageEquipmentButton.draw();
        deleteGameCharButton.draw();
        quitButton.draw();
        currentJobRender.draw();
        jobPoints.draw();
        putInStock.draw();

        if (null != popUp) {
            popUp.draw();
        }
        frame.draw();
    }

    @Override
    public void manageInput() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                if (null == popUp) {

                    if (quitButton.isClicked()) {
                        quitButton.onClick();
                    }
                    if (deleteGameCharButton.isClicked()) {
                        deleteGameCharButton.onClick();
                    }
                    if (manageEquipmentButton.isClicked()) {
                        manageEquipmentButton.onClick();
                    }
                    if (manageJobButton.isClicked()) {
                        manageJobButton.onClick();
                    }
                    if (putInStock.isClicked()) {
                        putInStock.onClick();
                    }
                } else if (popUp.isClicked()) {
                    String onClick = popUp.onClick();
                    if (null != onClick) {
                        if (onClick.equals("OK")) {
                            popUp = null;
                        }
                    }
                } else if (frame.isClicked()) {
                    if (x == -1 && y == -1) {
                        x = Mouse.getX();
                        y = (Display.getHeight() - Mouse.getY());
                        frameClicked = true;
                    }
                }
            } else if (!Mouse.isButtonDown(0)) {
                frameClicked = false;
                x = -1;
                y = -1;
            }
        }
        if (frameClicked && !clientContext.isFullscreen()) {
            Display.setLocation(Display.getX() + (Mouse.getX()) - x, (Display.getY() + (Display.getHeight() - Mouse.getY())) - y);
        }
    }

    @Override
    public void receiveMessage() {
        Message message = clientMessenger.receiveMessage();
        if (null != message) {
            switch (message.getType()) {
                case PLAYER_UPDATE:
                    MessagePlayerUpdate response = (MessagePlayerUpdate) message;
                    clientContext.setPlayer(response.getPlayer());
                    if (null == clientContext.getSelectedGameCharacter()) {
                        client.setCurrentScene(lobbyScene);
                    } else {
                        init();
                    }
                    break;
                case ERROR:
                    List<String> options = new ArrayList<String>();
                    options.add("OK");
                    MessageError messageError = (MessageError) message;
                    popUp = new PopUp(options, messageError.getError());
                    break;
            }
        }
    }
}
