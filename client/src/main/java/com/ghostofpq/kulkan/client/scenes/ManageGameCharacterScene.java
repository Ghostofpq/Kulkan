package com.ghostofpq.kulkan.client.scenes;


import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.ClientContext;
import com.ghostofpq.kulkan.client.ClientMessenger;
import com.ghostofpq.kulkan.client.graphics.Background;
import com.ghostofpq.kulkan.client.graphics.HUD.*;
import com.ghostofpq.kulkan.client.graphics.JobManager;
import com.ghostofpq.kulkan.client.graphics.KeyValueRender;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.client.utils.TextureKey;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.job.capacity.Capacity;
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

    private int widthSeparator = 50;
    private int widthStep;
    private int heightSeparator = 50;
    private int heightStep;

    // MENU
    private HUDTexturedElement menu;
    private Button manageJobButton;
    private Button manageEquipmentButton;
    private Button quitButton;
    private Button deleteGameCharButton;
    private Button putInStock;

    // OVERLAYS
    private HUDTexturedElement nameHolder;
    private TextZone name;
    private TextZone level;
    private HUDTexturedElement xpBackground;
    private HUDTexturedElement xpBar;
    private TextZone xp;

    private Mode mode;

    private JobManager jobManager;
    private TextZone jobType;
    private KeyValueRender jobPoints;
    private KeyValueRender capacityPrice;
    private Capacity selectedCapacity;
    private TextArea capacityDescription;

    // CHARACTERISTICS
    private HUDTexturedElement characteristicsPanel;
    private KeyValueRender maxHealthPointRender;
    private KeyValueRender maxManaPointRender;
    private KeyValueRender attackDamageRender;
    private KeyValueRender magicalDamageRender;
    private KeyValueRender armorRender;
    private KeyValueRender magicResistRender;
    private KeyValueRender armorPenetrationRender;
    private KeyValueRender magicPenetrationRender;
    private KeyValueRender movementRender;
    private KeyValueRender speedRender;
    private KeyValueRender healthRegenerationRender;
    private KeyValueRender manaRegenerationRender;
    private KeyValueRender escapeRender;
    private KeyValueRender criticalStrikeRender;
    private KeyValueRender precisionRender;
    private KeyValueRender resilienceRender;

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
        mode = Mode.JOB;

        GameCharacter gameCharacter = clientContext.getSelectedGameCharacter();
        background = new Background(TextureKey.BACKGROUND_BASIC);

        // MENU
        int menuWidth = clientContext.getCurrentResolution().getWidth() / 8;
        int menuPosX = (clientContext.getCurrentResolution().getWidth() - menuWidth) / 2;
        int menuPosY = 0;
        int menuHeight = clientContext.getCurrentResolution().getHeight();
        menu = new HUDTexturedElement(menuPosX, menuPosY, menuWidth, menuHeight, TextureKey.MANAGE_CHAR_MENU, TextureKey.MANAGE_CHAR_MENU);

        int buttonHeight = 40;
        int padding = 10;

        int manageJobButtonPosY = clientContext.getCurrentResolution().getWidth() / 64 + padding;
        manageJobButton = new Button(menuPosX, manageJobButtonPosY, menuWidth, buttonHeight, "Manage Job", null, null) {
            @Override
            public void onClick() {
                actionManageJob();
            }
        };

        int manageEquipmentButtonPosY = manageJobButtonPosY + buttonHeight + padding;
        manageEquipmentButton = new Button(menuPosX, manageEquipmentButtonPosY, menuWidth, buttonHeight, "Manage Stuff", null, null) {
            @Override
            public void onClick() {
                actionManageEquipment();
            }
        };

        int deleteGameCharButtonPosY = clientContext.getCurrentResolution().getHeight() / 2;
        deleteGameCharButton = new Button(menuPosX, deleteGameCharButtonPosY, menuWidth, buttonHeight, "Delete Char", null, null) {
            @Override
            public void onClick() {
                actionDelete();
            }
        };


        int putInStockPosY = deleteGameCharButtonPosY + buttonHeight + padding;
        putInStock = new Button(menuPosX, putInStockPosY, menuWidth, buttonHeight, "Stock", null, null) {
            @Override
            public void onClick() {
                actionStock();
            }
        };

        int quitButtonPosY = putInStockPosY + buttonHeight + padding;
        quitButton = new Button(menuPosX, quitButtonPosY, menuWidth, buttonHeight, "Back", null, null) {
            @Override
            public void onClick() {
                actionBack();
            }
        };

        int jobTypePosX = menuPosX + menuWidth;
        int jobTypePosY = clientContext.getCurrentResolution().getWidth() / 64;
        int jobTypeWidth = (clientContext.getCurrentResolution().getWidth() - jobTypePosX - clientContext.getCurrentResolution().getWidth() / 64) / 2;
        int jobTypeHeight = clientContext.getCurrentResolution().getHeight() / 8;
        jobType = new TextZone(jobTypePosX, jobTypePosY, jobTypeWidth, jobTypeHeight, String.valueOf(gameCharacter.getCurrentJob()));


        int jobPointsWidth = (clientContext.getCurrentResolution().getWidth() - jobTypePosX - clientContext.getCurrentResolution().getWidth() / 64) / 4;
        int jobPointsPosX = jobTypePosX + jobTypeWidth + jobPointsWidth;
        jobPoints = new KeyValueRender(jobPointsPosX, jobTypePosY, jobPointsWidth, jobTypeHeight, "JP", String.valueOf(gameCharacter.getJobPoints()), 7);

        int jobManagerPosX = menuPosX + menuWidth;
        int jobManagerPosY = clientContext.getCurrentResolution().getWidth() / 64 + jobTypeHeight;
        int jobManagerWidth = clientContext.getCurrentResolution().getWidth() - jobManagerPosX - clientContext.getCurrentResolution().getWidth() / 64;
        int jobManagerHeight = clientContext.getCurrentResolution().getHeight() / 2;
        jobManager = new JobManager(jobManagerPosX, jobManagerPosY, jobManagerWidth, jobManagerHeight, gameCharacter.getActiveJob());


        int capacityDescriptionPosX = menuPosX + menuWidth;
        int capacityDescriptionPosY = jobManagerPosY + jobManagerHeight;
        int capacityDescriptionWidth = jobManagerWidth;
        int capacityDescriptionHeight = clientContext.getCurrentResolution().getHeight() / 4;
        capacityDescription = new TextArea(capacityDescriptionPosX, capacityDescriptionPosY, capacityDescriptionWidth, capacityDescriptionHeight, "arial_16");


        int capacityPricePosX = jobTypePosX + jobTypeWidth + jobPointsWidth;
        int capacityPricePosY = capacityDescriptionPosY + capacityDescriptionHeight;
        int capacityPriceWidth = (clientContext.getCurrentResolution().getWidth() - jobTypePosX - clientContext.getCurrentResolution().getWidth() / 64) / 4;
        int capacityPriceHeight = clientContext.getCurrentResolution().getHeight() / 8;
        capacityPrice = new KeyValueRender(capacityPricePosX, capacityPricePosY, capacityPriceWidth, capacityPriceHeight, "Price", "0", 5);


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

        // XP BAR
        int xpBarPosX = xpBackgroundPosX + (int) (37f * xpBackgroundWidth / 685f);
        int xBarpPosY = xpBackgroundPosY + (int) (7f * xpBackgroundHeight / 30f);
        float xpRatio = ((float) gameCharacter.getExperience() / (float) gameCharacter.getNextLevel());
        int xpBarWidth = (int) (xpRatio * (611f * xpBackgroundWidth / 685f));
        int xpBarHeight = (int) (11f * xpBackgroundHeight / 30f);
        xpBar = new HUDTexturedElement(xpBarPosX, xBarpPosY, xpBarWidth, xpBarHeight, TextureKey.COMMON_BLOOD, TextureKey.COMMON_BLOOD);

        int xpPosX = xpBackgroundPosX + (int) (37f * xpBackgroundWidth / 685f);
        int xpPosY = xpBackgroundPosY + (int) (7f * xpBackgroundHeight / 30f);
        int xpWidth = (int) (611f * xpBackgroundWidth / 685f);
        int xpHeight = (int) (11f * xpBackgroundHeight / 30f);
        String xpValue = new StringBuilder().append(gameCharacter.getExperience()).append("/").append(gameCharacter.getNextLevel()).toString();
        xp = new TextZone(xpPosX, xpPosY, xpWidth, xpHeight, xpValue);
        xp.setFontName("arial_12");
        xp.setAlignment(TextAlignment.CENTER);

        // CHARACTERISTICS PANEL
        int characteristicsPanelWidth = clientContext.getCurrentResolution().getWidth() / 3;
        int characteristicsPanelPosX = xpBackgroundPosX + xpBackgroundHeight;
        int characteristicsPanelPosY = (xpBackgroundPosY + (xpBackgroundWidth / 2)) - (characteristicsPanelWidth / 2);
        int characteristicsPanelHeight = clientContext.getCurrentResolution().getHeight() - characteristicsPanelPosY - clientContext.getCurrentResolution().getHeight() / 64;
        characteristicsPanel = new HUDTexturedElement(characteristicsPanelPosX, characteristicsPanelPosY, characteristicsPanelWidth, characteristicsPanelHeight, TextureKey.MANAGE_CHAR_MENU, TextureKey.MANAGE_CHAR_MENU);


        int valueHeight = characteristicsPanelHeight / 10;
        int valuePadding = (characteristicsPanelHeight - (8 * valueHeight)) / 13;
        int valueWidth = (characteristicsPanelWidth / 2) - 4 * valuePadding;

        int hpRenderPosX = characteristicsPanelPosX + 2 * valuePadding;
        int hpRenderPosY = characteristicsPanelPosY + valuePadding;
        maxHealthPointRender = new KeyValueRender(hpRenderPosX, hpRenderPosY, valueWidth, valueHeight, "HP", String.valueOf(gameCharacter.getMaxHealthPoint()), 5);
        maxHealthPointRender.getValue().setAlignment(TextAlignment.RIGHT);

        int mpRenderPosX = characteristicsPanelPosX + (characteristicsPanelWidth / 2) + 2 * valuePadding;
        int mpRenderPosY = hpRenderPosY;
        maxManaPointRender = new KeyValueRender(mpRenderPosX, mpRenderPosY, valueWidth, valueHeight, "MP", String.valueOf(gameCharacter.getMaxManaPoint()), 5);
        maxManaPointRender.getValue().setAlignment(TextAlignment.RIGHT);

        int attackDamageRenderPosX = characteristicsPanelPosX + 2 * valuePadding;
        int attackDamageRenderPosY = hpRenderPosY + valueHeight + 2 * valuePadding;
        attackDamageRender = new KeyValueRender(attackDamageRenderPosX, attackDamageRenderPosY, valueWidth, valueHeight, "Attack", String.valueOf(gameCharacter.getAttackDamage()), 5);
        attackDamageRender.getValue().setAlignment(TextAlignment.RIGHT);

        int armorRenderPosX = characteristicsPanelPosX + (characteristicsPanelWidth / 2) + 2 * valuePadding;
        int armorRenderPosY = attackDamageRenderPosY;
        armorRender = new KeyValueRender(armorRenderPosX, armorRenderPosY, valueWidth, valueHeight, "Armor", String.valueOf(gameCharacter.getArmor()), 5);
        armorRender.getValue().setAlignment(TextAlignment.RIGHT);

        int magicalDamageRenderPosX = characteristicsPanelPosX + 2 * valuePadding;
        int magicalDamageRenderPosY = attackDamageRenderPosY + valueHeight + valuePadding;
        magicalDamageRender = new KeyValueRender(magicalDamageRenderPosX, magicalDamageRenderPosY, valueWidth, valueHeight, "Mag. Attack", String.valueOf(gameCharacter.getMagicalDamage()), 5);
        magicalDamageRender.getValue().setAlignment(TextAlignment.RIGHT);

        int magicResistRenderPosX = characteristicsPanelPosX + (characteristicsPanelWidth / 2) + 2 * valuePadding;
        int magicResistRenderPosY = magicalDamageRenderPosY;
        magicResistRender = new KeyValueRender(magicResistRenderPosX, magicResistRenderPosY, valueWidth, valueHeight, "Mag. Armor", String.valueOf(gameCharacter.getMagicResist()), 5);
        magicResistRender.getValue().setAlignment(TextAlignment.RIGHT);

        int armorPenetrationRenderPosX = characteristicsPanelPosX + 2 * valuePadding;
        int armorPenetrationRenderPosY = magicalDamageRenderPosY + valueHeight + valuePadding;
        armorPenetrationRender = new KeyValueRender(armorPenetrationRenderPosX, armorPenetrationRenderPosY, valueWidth, valueHeight, "Arm. Pen.", String.valueOf(gameCharacter.getAttackDamage()), 5);
        armorPenetrationRender.getValue().setAlignment(TextAlignment.RIGHT);

        int magicPenetrationRenderPosX = characteristicsPanelPosX + (characteristicsPanelWidth / 2) + 2 * valuePadding;
        int magicPenetrationRenderPosY = armorPenetrationRenderPosY;
        magicPenetrationRender = new KeyValueRender(magicPenetrationRenderPosX, magicPenetrationRenderPosY, valueWidth, valueHeight, "Mag. Pen.", String.valueOf(gameCharacter.getMagicPenetration()), 5);
        magicPenetrationRender.getValue().setAlignment(TextAlignment.RIGHT);

        int movementRenderPosX = characteristicsPanelPosX + 2 * valuePadding;
        int movementRenderPosY = armorPenetrationRenderPosY + valueHeight + 2 * valuePadding;
        movementRender = new KeyValueRender(movementRenderPosX, movementRenderPosY, valueWidth, valueHeight, "Movement", String.valueOf(gameCharacter.getMovement()), 5);
        movementRender.getValue().setAlignment(TextAlignment.RIGHT);

        int speedRenderPosX = characteristicsPanelPosX + (characteristicsPanelWidth / 2) + 2 * valuePadding;
        int speedRenderPosY = movementRenderPosY;
        speedRender = new KeyValueRender(speedRenderPosX, speedRenderPosY, valueWidth, valueHeight, "Speed", String.valueOf(gameCharacter.getSpeed()), 5);
        speedRender.getValue().setAlignment(TextAlignment.RIGHT);


        int healthRegenerationRenderPosX = characteristicsPanelPosX + 2 * valuePadding;
        int healthRegenerationRenderPosY = movementRenderPosY + valueHeight + 2 * valuePadding;
        healthRegenerationRender = new KeyValueRender(healthRegenerationRenderPosX, healthRegenerationRenderPosY, valueWidth, valueHeight, "HP Regen.", String.valueOf(gameCharacter.getHealthRegeneration()), 5);
        healthRegenerationRender.getValue().setAlignment(TextAlignment.RIGHT);

        int manaRegenerationRenderPosX = characteristicsPanelPosX + (characteristicsPanelWidth / 2) + 2 * valuePadding;
        int manaRegenerationRenderPosY = healthRegenerationRenderPosY;
        manaRegenerationRender = new KeyValueRender(manaRegenerationRenderPosX, manaRegenerationRenderPosY, valueWidth, valueHeight, "MP Regen.", String.valueOf(gameCharacter.getManaRegeneration()), 5);
        manaRegenerationRender.getValue().setAlignment(TextAlignment.RIGHT);

        int escapeRenderPosX = characteristicsPanelPosX + 2 * valuePadding;
        int escapeRenderPosY = healthRegenerationRenderPosY + valueHeight + 2 * valuePadding;
        escapeRender = new KeyValueRender(escapeRenderPosX, escapeRenderPosY, valueWidth, valueHeight, "Escape", String.valueOf(gameCharacter.getEscape()), 5);
        escapeRender.getValue().setAlignment(TextAlignment.RIGHT);

        int criticalStrikeRenderPosX = characteristicsPanelPosX + (characteristicsPanelWidth / 2) + 2 * valuePadding;
        int criticalStrikeRenderPosY = escapeRenderPosY;
        criticalStrikeRender = new KeyValueRender(criticalStrikeRenderPosX, criticalStrikeRenderPosY, valueWidth, valueHeight, "Crit. Chance", String.valueOf(gameCharacter.getCriticalStrike()), 5);
        criticalStrikeRender.getValue().setAlignment(TextAlignment.RIGHT);

        int precisionRenderPosX = characteristicsPanelPosX + 2 * valuePadding;
        int precisionRenderPosY = escapeRenderPosY + valueHeight + valuePadding;
        precisionRender = new KeyValueRender(precisionRenderPosX, precisionRenderPosY, valueWidth, valueHeight, "Precision", String.valueOf(gameCharacter.getPrecision()), 5);
        precisionRender.getValue().setAlignment(TextAlignment.RIGHT);

        int resilienceRenderPosX = characteristicsPanelPosX + (characteristicsPanelWidth / 2) + 2 * valuePadding;
        int resilienceRenderPosY = precisionRenderPosY;
        resilienceRender = new KeyValueRender(resilienceRenderPosX, resilienceRenderPosY, valueWidth, valueHeight, "Resilience", String.valueOf(gameCharacter.getResilience()), 5);
        resilienceRender.getValue().setAlignment(TextAlignment.RIGHT);

        maxHealthPointRender.setFont("arial_16");
        maxManaPointRender.setFont("arial_16");
        attackDamageRender.setFont("arial_16");
        armorRender.setFont("arial_16");
        magicalDamageRender.setFont("arial_16");
        magicResistRender.setFont("arial_16");
        armorPenetrationRender.setFont("arial_16");
        magicPenetrationRender.setFont("arial_16");
        movementRender.setFont("arial_16");
        speedRender.setFont("arial_16");
        healthRegenerationRender.setFont("arial_16");
        manaRegenerationRender.setFont("arial_16");
        escapeRender.setFont("arial_16");
        criticalStrikeRender.setFont("arial_16");
        precisionRender.setFont("arial_16");
        resilienceRender.setFont("arial_16");


        frame = new Frame(0, 0, clientContext.getCurrentResolution().getWidth(), clientContext.getCurrentResolution().getHeight(), clientContext.getCurrentResolution().getWidth() / 64, clientContext.getCurrentResolution().getWidth() / 64, TextureKey.COMMON_EXT_FRAME);

    }

    private void actionManageJob() {
        mode = Mode.JOB;
    }

    private void actionManageEquipment() {
        mode = Mode.STUFF;
    }


    public void actionUnlockSelectedCapacity() {
        if (null != selectedCapacity) {
            MessageUnlockCapacity messageUnlockCapacity = new MessageUnlockCapacity(clientContext.getTokenKey(), clientContext.getUsername(), clientContext.getSelectedCharacterId(), clientContext.getSelectedGameCharacter().getCurrentJob(), selectedCapacity.getName());
            clientMessenger.sendMessageToUserService(messageUnlockCapacity);
        }
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
        xpBar.draw();
        xp.draw();
        characteristicsPanel.draw();
        maxHealthPointRender.draw();
        maxManaPointRender.draw();
        attackDamageRender.draw();
        armorRender.draw();
        magicalDamageRender.draw();
        magicResistRender.draw();
        armorPenetrationRender.draw();
        magicPenetrationRender.draw();
        movementRender.draw();
        speedRender.draw();
        healthRegenerationRender.draw();
        manaRegenerationRender.draw();
        escapeRender.draw();
        criticalStrikeRender.draw();
        precisionRender.draw();
        resilienceRender.draw();
        manageJobButton.draw();
        manageEquipmentButton.draw();
        deleteGameCharButton.draw();
        quitButton.draw();
        putInStock.draw();

        if (mode == Mode.JOB) {
            jobManager.draw();
            jobType.draw();
            jobPoints.draw();
            if (null != selectedCapacity) {
                capacityDescription.draw();
                capacityPrice.draw();
            }
        }
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
                    if (mode == Mode.JOB) {
                        if (jobManager.isClicked()) {
                            Capacity capacity = jobManager.hoveredCapacity();
                            if (null != capacity) {
                                selectedCapacity = capacity;
                                if (!selectedCapacity.isLocked()) {
                                    String text = new StringBuilder().append("You already know this capacity.").toString();
                                    List<String> options = new ArrayList<String>();
                                    options.add("OK");
                                    popUp = new PopUp(options, text);
                                } else if (selectedCapacity.canBeUnlock(clientContext.getSelectedGameCharacter().getJobPoints())) {
                                    String text = new StringBuilder().append("Unlock ").append(selectedCapacity.getName()).append(" for ").append(selectedCapacity.getPrice()).append(" job points?").toString();
                                    List<String> options = new ArrayList<String>();
                                    options.add("UNLOCK");
                                    options.add("CANCEL");
                                    popUp = new PopUp(options, text);
                                } else {
                                    String text = new StringBuilder().append("You don't have enough Job points.").toString();
                                    List<String> options = new ArrayList<String>();
                                    options.add("OK");
                                    popUp = new PopUp(options, text);
                                }
                            }
                        }
                    }
                } else if (popUp.isClicked()) {
                    String onClick = popUp.onClick();
                    if (null != onClick) {
                        if (onClick.equals("OK")) {

                            popUp = null;
                        } else if (onClick.equals("UNLOCK")) {
                            actionUnlockSelectedCapacity();
                            popUp = null;
                        } else if (onClick.equals("CANCEL")) {

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
        if (mode == Mode.JOB) {
            selectedCapacity = jobManager.hoveredCapacity();
            if (null != selectedCapacity) {
                capacityDescription.clear();
                capacityDescription.addLine(selectedCapacity.getDescription());
                capacityPrice.setValue(String.valueOf(selectedCapacity.getPrice()));
            }
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

    private enum Mode {
        JOB, STUFF
    }
}
