package com.ghostofpq.kulkan.client.scenes;


import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.ClientContext;
import com.ghostofpq.kulkan.client.ClientMessenger;
import com.ghostofpq.kulkan.client.graphics.*;
import com.ghostofpq.kulkan.client.graphics.HUD.*;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.client.utils.TextureKey;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.inventory.item.Item;
import com.ghostofpq.kulkan.entities.inventory.item.ItemType;
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

    private Mode mode = Mode.JOB;

    private JobManager jobManager;
    private EquipmentManager equipmentManager;

    private TextArea capacityDescription;
    private KeyValueRender capacityPrice;
    private Capacity selectedCapacity;

    private EquipItemPanel equipItemPanel;
    private Item selectedItem;
    private ItemType selectedItemType;

    private List<HUDElement> hudElements;
    private List<HUDElement> hudElementJobMode;
    private List<HUDElement> hudElementStuffMode;
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

    @Override
    public void init() {
        // MENU
        HUDTexturedElement menu;
        Button manageJobButton;
        Button manageEquipmentButton;
        Button quitButton;
        Button deleteGameCharButton;
        Button putInStock;
        // OVERLAYS
        HUDTexturedElement nameHolder;
        TextZone name;
        TextZone level;
        HUDTexturedElement xpBackground;
        HUDTexturedElement xpBar;
        TextZone xp;

        CharacteristicsPanel characteristicsPanel;
        TextZone jobType;
        KeyValueRender jobPoints;

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

        int deleteGameCharButtonPosY = clientContext.getCurrentResolution().getHeight() - 4 * (buttonHeight + padding);
        deleteGameCharButton = new Button(menuPosX, deleteGameCharButtonPosY, menuWidth, buttonHeight, "Delete Char", null, null) {
            @Override
            public void onClick() {
                popupDelete();
            }
        };


        int putInStockPosY = deleteGameCharButtonPosY + buttonHeight + padding;
        putInStock = new Button(menuPosX, putInStockPosY, menuWidth, buttonHeight, "Stock", null, null) {
            @Override
            public void onClick() {
                popupStock();
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

        equipmentManager = new EquipmentManager(jobManagerPosX, jobManagerPosY, jobManagerWidth, jobManagerHeight, gameCharacter);
        List<Item> itemList = clientContext.getPlayer().getInventory().getAll();
        equipItemPanel = new EquipItemPanel(jobManagerPosX, jobManagerPosY + jobManagerHeight, jobManagerWidth, clientContext.getCurrentResolution().getHeight() / 4, itemList);

        int capacityDescriptionPosX = menuPosX + menuWidth;
        int capacityDescriptionPosY = jobManagerPosY + jobManagerHeight;
        int capacityDescriptionHeight = clientContext.getCurrentResolution().getHeight() / 4;
        capacityDescription = new TextArea(capacityDescriptionPosX, capacityDescriptionPosY, jobManagerWidth, capacityDescriptionHeight, "arial_16");


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
        int characteristicsPanelPosX = xpBackgroundPosX + xpBackgroundHeight;
        int characteristicsPanelWidth = clientContext.getCurrentResolution().getWidth() / 3;
        int characteristicsPanelPosY = (xpBackgroundPosY + (xpBackgroundWidth / 2)) - (characteristicsPanelWidth / 2);
        int characteristicsPanelHeight = clientContext.getCurrentResolution().getHeight() - (characteristicsPanelPosY + clientContext.getCurrentResolution().getHeight() / 32);
        characteristicsPanel = new CharacteristicsPanel(characteristicsPanelPosX, characteristicsPanelPosY, characteristicsPanelWidth, characteristicsPanelHeight, gameCharacter.getAggregatedCharacteristics());

        frame = new Frame(0, 0, clientContext.getCurrentResolution().getWidth(), clientContext.getCurrentResolution().getHeight(), clientContext.getCurrentResolution().getWidth() / 64, clientContext.getCurrentResolution().getWidth() / 64, TextureKey.COMMON_EXT_FRAME);

        hudElements = new ArrayList<HUDElement>();
        hudElements.add(menu);
        hudElements.add(manageJobButton);
        hudElements.add(manageEquipmentButton);
        hudElements.add(quitButton);
        hudElements.add(deleteGameCharButton);
        hudElements.add(putInStock);

        hudElements.add(nameHolder);
        hudElements.add(name);
        hudElements.add(level);
        hudElements.add(xpBackground);
        hudElements.add(xpBar);
        hudElements.add(xp);
        hudElements.add(characteristicsPanel);

        hudElementJobMode = new ArrayList<HUDElement>();
        hudElementJobMode.add(jobManager);
        hudElementJobMode.add(jobType);
        hudElementJobMode.add(jobPoints);

        hudElementStuffMode = new ArrayList<HUDElement>();
        hudElementStuffMode.add(equipmentManager);
        hudElementStuffMode.add(equipItemPanel);
    }

    private void popupDelete() {
        String text = new StringBuilder().append("Are you sure you want to delete ").append(clientContext.getSelectedGameCharacter().getName()).append(" ?").toString();
        List<String> options = new ArrayList<String>();
        options.add("DELETE");
        options.add("CANCEL");
        popUp = new PopUp(options, text);
    }

    private void popupStock() {
        String text = new StringBuilder().append("Are you sure you want to put ").append(clientContext.getSelectedGameCharacter().getName()).append(" in the stock ?").toString();
        List<String> options = new ArrayList<String>();
        options.add("STOCK");
        options.add("CANCEL");
        popUp = new PopUp(options, text);
    }

    private void actionManageJob() {
        mode = Mode.JOB;
    }

    private void actionManageEquipment() {
        mode = Mode.STUFF;
    }

    private void actionEquipItem() {
        MessageEquipItemOnGameCharacter messageEquipItemOnGameCharacter = new MessageEquipItemOnGameCharacter(clientContext.getTokenKey(), clientContext.getUsername(), clientContext.getSelectedGameCharacter().getId(), selectedItem.getItemID());
        clientMessenger.sendMessageToUserService(messageEquipItemOnGameCharacter);
    }

    private void actionUnequipItem() {
        MessageUnequipItemOnGameCharacter messageUnequipItemOnGameCharacter = new MessageUnequipItemOnGameCharacter(clientContext.getTokenKey(), clientContext.getUsername(), clientContext.getSelectedGameCharacter().getId(), selectedItemType);
        clientMessenger.sendMessageToUserService(messageUnequipItemOnGameCharacter);
    }

    public void actionUnlockSelectedCapacity() {
        if (null != selectedCapacity) {
            MessageUnlockCapacity messageUnlockCapacity = new MessageUnlockCapacity(clientContext.getTokenKey(), clientContext.getUsername(), clientContext.getSelectedGameCharacter().getId(), clientContext.getSelectedGameCharacter().getCurrentJob(), selectedCapacity.getName());
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
            MessageDeleteGameCharacterFromTeam messageDeleteGameCharacterFromTeam = new MessageDeleteGameCharacterFromTeam(clientContext.getTokenKey(), player.getPseudo(), clientContext.getSelectedGameCharacter().getId());
            clientMessenger.sendMessageToUserService(messageDeleteGameCharacterFromTeam);
        } else {
            MessageDeleteGameCharacterFromStock messageDeleteGameCharacterFromStock = new MessageDeleteGameCharacterFromStock(clientContext.getTokenKey(), player.getPseudo(), clientContext.getSelectedGameCharacter().getId());
            clientMessenger.sendMessageToUserService(messageDeleteGameCharacterFromStock);
        }
    }

    private void actionStock() {
        Player player = clientContext.getPlayer();
        MessagePutGameCharacterFromTeamToStock putGameCharacterFromTeamToStock = new MessagePutGameCharacterFromTeamToStock(clientContext.getTokenKey(), player.getPseudo(), clientContext.getSelectedGameCharacter().getId());
        clientMessenger.sendMessageToUserService(putGameCharacterFromTeamToStock);
    }

    @Override
    public void update(long deltaTime) {
    }

    @Override
    public void render() {
        GraphicsManager.getInstance().make2D();
        background.draw();
        for (HUDElement element : hudElements) {
            element.draw();
        }
        if (mode == Mode.JOB) {
            for (HUDElement element : hudElementJobMode) {
                element.draw();
            }
            if (null != selectedCapacity) {
                capacityDescription.draw();
                capacityPrice.draw();
            }
        } else if (mode == Mode.STUFF) {
            for (HUDElement element : hudElementStuffMode) {
                element.draw();
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
                    for (HUDElement element : hudElements) {
                        if (element.isClicked()) {
                            if (element instanceof Button) {
                                ((Button) element).onClick();
                            }
                        }
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
                    } else if (mode == Mode.STUFF) {
                        if (equipmentManager.isClicked()) {
                            selectedItemType = equipmentManager.getSelectedItemType();
                            List<Item> itemList = new ArrayList<Item>();
                            if (null != selectedItemType) {
                                switch (selectedItemType) {
                                    case HELMET:
                                        itemList = clientContext.getPlayer().getInventory().getItemsByType(ItemType.HELMET);
                                        break;
                                    case ARMOR:
                                        itemList = clientContext.getPlayer().getInventory().getItemsByType(ItemType.ARMOR);
                                        break;
                                    case NECKLACE:
                                        itemList = clientContext.getPlayer().getInventory().getItemsByType(ItemType.NECKLACE);
                                        break;
                                    case RING:
                                        itemList = clientContext.getPlayer().getInventory().getItemsByType(ItemType.RING);
                                        break;
                                    case WEAPON:
                                        itemList = clientContext.getPlayer().getInventory().getItemsByType(ItemType.WEAPON);
                                        break;
                                    case HELD_ITEM:
                                        itemList = clientContext.getPlayer().getInventory().getItemsByType(ItemType.HELD_ITEM);
                                        break;
                                }
                                equipItemPanel.setItemList(itemList);
                            } else {
                                itemList = clientContext.getPlayer().getInventory().getAll();
                                equipItemPanel.setItemList(itemList);
                            }
                        }
                        if (equipItemPanel.isClicked()) {
                            selectedItem = equipItemPanel.getClickedItem();
                            if (selectedItem != null) {
                                String text = new StringBuilder().append("Equip ").append(selectedItem.getName()).append("?").toString();
                                List<String> options = new ArrayList<String>();
                                options.add("EQUIP");
                                options.add("CANCEL");
                                popUp = new PopUp(options, text);
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
                        } else if (onClick.equals("UNEQUIP")) {
                            actionUnequipItem();
                            popUp = null;
                        } else if (onClick.equals("EQUIP")) {
                            actionEquipItem();
                            popUp = null;
                        } else if (onClick.equals("DELETE")) {
                            actionDelete();
                            popUp = null;
                        } else if (onClick.equals("STOCK")) {
                            actionStock();
                            popUp = null;
                        }
                    }
                }
                if (frame.isClicked()) {
                    if (x == -1 && y == -1) {
                        x = Mouse.getX();
                        y = (Display.getHeight() - Mouse.getY());
                        frameClicked = true;
                    }
                }
            } else if (Mouse.isButtonDown(1)) {
                if (equipmentManager.isClicked()) {
                    selectedItemType = equipmentManager.getSelectedItemType();
                    if (selectedItemType != null) {
                        Item equippedItem = clientContext.getSelectedGameCharacter().getEquipment().get(selectedItemType);
                        if (equippedItem != null) {
                            String text = new StringBuilder().append("Unequip ").append(equippedItem.getName()).append("?").toString();
                            List<String> options = new ArrayList<String>();
                            options.add("UNEQUIP");
                            options.add("CANCEL");
                            popUp = new PopUp(options, text);
                        }
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
        if ((mode == Mode.JOB) && (popUp == null)) {
            selectedCapacity = jobManager.hoveredCapacity();
            if (null != selectedCapacity) {
                capacityDescription.clear();
                capacityDescription.addLine(selectedCapacity.getDescription());
                if (selectedCapacity.isLocked()) {
                    capacityPrice.setValue(String.valueOf(selectedCapacity.getPrice()));
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
                    MessagePlayerUpdate response = (MessagePlayerUpdate) message;
                    clientContext.setPlayer(response.getPlayer());
                    if (null == clientContext.getSelectedGameCharacter()) {
                        client.setCurrentScene(lobbyScene);
                    } else if (clientContext.getPlayer().getStock().contains(clientContext.getSelectedGameCharacter())) {
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
