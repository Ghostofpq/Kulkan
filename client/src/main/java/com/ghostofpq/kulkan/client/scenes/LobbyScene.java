package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.ClientContext;
import com.ghostofpq.kulkan.client.ClientMessenger;
import com.ghostofpq.kulkan.client.graphics.Background;
import com.ghostofpq.kulkan.client.graphics.HUD.*;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.client.utils.InputManager;
import com.ghostofpq.kulkan.client.utils.InputMap;
import com.ghostofpq.kulkan.client.utils.TextureKey;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.game.MessageGameStart;
import com.ghostofpq.kulkan.entities.messages.lobby.*;
import com.ghostofpq.kulkan.entities.messages.user.MessagePlayerUpdate;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LobbyScene implements Scene {
    private TextField inputChat;
    private TextArea chat;
    private TextArea news;
    private Button postButton;
    private Button matchmakingButton;
    private Button shopButton;
    private Button optionButton;
    private Button quitButton;
    private Button acceptButton;
    private Button refuseButton;
    private Button manageTeamButton;
    private boolean matchFound;
    private String matchId;
    private List<HUDElement> hudElementList;
    private int indexOnFocus;
    private Background background;
    private HUDTexturedElement chatOverlay;
    private HUDTexturedElement newsOverlay;
    // FRAME
    private Frame frame;
    private int x;
    private int y;
    private boolean frameClicked;
    @Autowired
    private Client client;
    @Autowired
    private ClientContext clientContext;
    @Autowired
    private ClientMessenger clientMessenger;
    @Autowired
    private OptionScene optionScene;

    public LobbyScene() {
    }

    @Override
    public void init() {
        hudElementList = new ArrayList<HUDElement>();

        // PADDING TO AVOID HAVING TEXT RIGHT NEXT TO THE BORDER
        int padding = 10;

        int chatPosX = clientContext.getCurrentResolution().getWidth() / 32;
        int chatPosY = clientContext.getCurrentResolution().getHeight() * 5 / 8;
        int chatWidth = clientContext.getCurrentResolution().getWidth() * 23 / 64;
        int chatHeight = clientContext.getCurrentResolution().getHeight() * 5 / 18;
        chat = new TextArea(chatPosX + padding, chatPosY + padding, chatWidth - 2 * padding, chatHeight - 2 * padding, "arial_12");

        int inputChatPosX = chatPosX;
        int inputChatPosY = chatPosY + chatHeight;
        int inputChatWidth = clientContext.getCurrentResolution().getWidth() * 31 / 96;
        int inputChatHeight = clientContext.getCurrentResolution().getHeight() / 24;
        inputChat = new TextField(inputChatPosX + padding, inputChatPosY, inputChatWidth - 2 * padding, inputChatHeight, 120, "arial_12");

        int postButtonPosX = chatPosX + inputChatWidth;
        int postButtonPosY = inputChatPosY;
        int postButtonWidth = chatWidth - inputChatWidth;
        int postButtonHeight = inputChatHeight;
        postButton = new Button(postButtonPosX, postButtonPosY, postButtonWidth, postButtonHeight, "P", null, null) {
            @Override
            public void onClick() {
                actionPostMessage();
            }
        };

        int chatOverlayPosX = chatPosX;
        int chatOverlayPosY = chatPosY;
        int chatOverlayWidth = chatWidth;
        int chatOverlayHeight = chatHeight + inputChatHeight;
        chatOverlay = new HUDTexturedElement(chatOverlayPosX, chatOverlayPosY, chatOverlayWidth, chatOverlayHeight, TextureKey.LOBBY_CHAT_OVERLAY, TextureKey.LOBBY_CHAT_OVERLAY);


        int matchmakingButtonPosX = clientContext.getCurrentResolution().getWidth() * 5 / 12;
        int matchmakingButtonPosY = chatPosY;
        int matchmakingButtonWidth = clientContext.getCurrentResolution().getWidth() / 6;
        int matchmakingButtonHeight = clientContext.getCurrentResolution().getHeight() / 18;
        matchmakingButton = new Button(matchmakingButtonPosX, matchmakingButtonPosY, matchmakingButtonWidth, matchmakingButtonHeight, "GAME", null, null) {
            @Override
            public void onClick() {
                enterMatchmaking();
            }
        };

        int shopButtonPosX = matchmakingButtonPosX;
        int shopButtonPosY = matchmakingButtonPosY + matchmakingButtonHeight + clientContext.getCurrentResolution().getHeight() / 36;
        int shopButtonWidth = matchmakingButtonWidth;
        int shopButtonHeight = matchmakingButtonHeight;
        shopButton = new Button(shopButtonPosX, shopButtonPosY, shopButtonWidth, shopButtonHeight, "SHOP", null, null) {
            @Override
            public void onClick() {
                log.debug("SHOP TEAM");
                exitLobby();
                Client.getInstance().setCurrentScene(ShopScene.getInstance());
            }
        };

        int optionButtonPosX = matchmakingButtonPosX;
        int optionButtonPosY = shopButtonPosY + shopButtonHeight;
        int optionButtonWidth = matchmakingButtonWidth;
        int optionButtonHeight = matchmakingButtonHeight;
        optionButton = new Button(optionButtonPosX, optionButtonPosY, optionButtonWidth, optionButtonHeight, "OPTION", null, null) {
            @Override
            public void onClick() {
                log.debug("OPTION");
                actionOption();
            }
        };

        int quitButtonPosX = matchmakingButtonPosX;
        int quitButtonPosY = optionButtonPosY + optionButtonHeight;
        int quitButtonWidth = matchmakingButtonWidth;
        int quitButtonHeight = matchmakingButtonHeight;
        quitButton = new Button(quitButtonPosX, quitButtonPosY, quitButtonWidth, quitButtonHeight, "QUIT", null, null) {
            @Override
            public void onClick() {
                log.debug("QUIT");
                Client.getInstance().quit();
            }
        };

        int newsPosX = clientContext.getCurrentResolution().getWidth() * 39 / 64;
        int newsPosY = chatPosY;
        int newsWidth = chatWidth;
        int newsHeight = chatHeight + inputChatHeight;
        news = new TextArea(newsPosX + padding, newsPosY + padding, newsWidth - 2 * padding, newsHeight - 2 * padding, "arial_12");


        int newsOverlayPosX = newsPosX;
        int newsOverlayPosY = newsPosY;
        int newsOverlayWidth = newsWidth;
        int newsOverlayHeight = newsHeight;
        newsOverlay = new HUDTexturedElement(newsOverlayPosX, newsOverlayPosY, newsOverlayWidth, newsOverlayHeight, TextureKey.LOBBY_NEWS_OVERLAY, TextureKey.LOBBY_NEWS_OVERLAY);


        acceptButton = new

                Button(550, 100, 50, 50, "ACCEPT") {
                    @Override
                    public void onClick() {
                        log.debug("ACCEPT");
                        acceptMatch();
                    }
                };

        refuseButton = new

                Button(550, 150, 50, 50, "REFUSE") {
                    @Override
                    public void onClick() {
                        log.debug("REFUSE");
                        refuseMatch();
                    }
                };

        manageTeamButton = new

                Button(550, 200, 50, 50, "MANAGE TEAM") {
                    @Override
                    public void onClick() {
                        log.debug("MANAGE TEAM");
                        exitLobby();
                        Client.getInstance().setCurrentScene(TeamManagementScene.getInstance());
                    }
                };


        hudElementList.add(inputChat);
        hudElementList.add(postButton);
        hudElementList.add(matchmakingButton);
        hudElementList.add(chat);
        hudElementList.add(quitButton);
        hudElementList.add(manageTeamButton);
        hudElementList.add(shopButton);
        hudElementList.add(optionButton);
        hudElementList.add(news);

        indexOnFocus = 0;
        setFocusOn(indexOnFocus);
        matchFound = false;
        matchId = "";
        background = new Background(TextureKey.LOBBY_BACKGROUD_169);
        frame = new Frame(0, 0, clientContext.getCurrentResolution().getWidth(), clientContext.getCurrentResolution().getHeight(), clientContext.getCurrentResolution().getWidth() / 64, clientContext.getCurrentResolution().getWidth() / 64, TextureKey.LOBBY_EXT_FRAME);

        MessageSubscribeToLobby messageSubscribeToLobby = new MessageSubscribeToLobby(Client.getInstance().getTokenKey());
        clientMessenger.sendMessageToLobbyService(messageSubscribeToLobby);
    }

    private void actionOption() {
        log.debug("OPTION");
        optionScene.setLastScene(this);
        client.setCurrentScene(optionScene);
    }

    public void setFocusOn(int i) {
        for (HUDElement hudElement : hudElementList) {
            hudElement.setHasFocus(false);
        }
        hudElementList.get(i).setHasFocus(true);
    }

    public void actionPostMessage() {
        if (!inputChat.getLabel().isEmpty()) {
            MessageLobbyClient messageLobbyClient = new MessageLobbyClient(Client.getInstance().getTokenKey(), inputChat.getLabel());
            clientMessenger.sendMessageToLobbyService(messageLobbyClient);
            inputChat.clear();
        }
    }

    @Override
    public void receiveMessage() {
        Message message = Client.getInstance().receiveMessage();
        if (null != message) {
            switch (message.getType()) {
                case LOBBY_SERVER:
                    MessageLobbyServer receivedMessage = (MessageLobbyServer) message;
                    String receivedTextMessage = receivedMessage.getMessage();
                    log.debug(" [x] Received Message : [{}]", receivedTextMessage);
                    if (!receivedTextMessage.isEmpty()) {
                        chat.addLine(receivedTextMessage);
                    }
                    break;
                case MATCHMAKING_MATCH_FOUND:
                    log.debug(" [x] MATCH FOUND");
                    MessageMatchFound messageMatchFound = (MessageMatchFound) message;
                    matchFound = true;
                    matchId = messageMatchFound.getMatchKey();
                    break;
                case MATCHMAKING_MATCH_ABORT:
                    log.debug(" [x] MATCH ABORT");
                    matchFound = false;
                    matchId = "";
                    break;
                case GAME_START:
                    log.debug(" [x] GAME START");
                    exitLobby();
                    MessageGameStart messageGameStart = (MessageGameStart) message;
                    Client.getInstance().setCurrentScene(BattleScene.getInstance());
                    BattleScene.getInstance().setBattlefield(messageGameStart.getBattlefield());
                    BattleScene.getInstance().setGameId(messageGameStart.getGameID());
                    break;
                case PLAYER_UPDATE:
                    log.debug(" [x] PLAYER_UPDATE");
                    MessagePlayerUpdate messagePlayerUpdate = (MessagePlayerUpdate) message;
                    Client.getInstance().setPlayer(messagePlayerUpdate.getPlayer());
                    break;
                default:
                    log.error(" [X] UNEXPECTED MESSAGE : {}", message.getType());
                    break;

            }
        }
    }

    private void exitLobby() {
        MessageUnsubscribeToLobby messageUnsubscribeToLobby = new MessageUnsubscribeToLobby(Client.getInstance().getTokenKey());
        clientMessenger.sendMessageToLobbyService(messageUnsubscribeToLobby);
    }

    public void acceptMatch() {
        if (null != matchId && !matchId.isEmpty()) {
            MessageMatchmakingAccept messageMatchmakingAccept = new MessageMatchmakingAccept(Client.getInstance().getTokenKey(), matchId);
            clientMessenger.sendMessageToMatchmakingService(messageMatchmakingAccept);
            matchFound = false;
            matchId = "";
        }
    }

    public void refuseMatch() {
        if (null != matchId && !matchId.isEmpty()) {
            MessageMatchmakingRefuse messageMatchmakingRefuse = new MessageMatchmakingRefuse(Client.getInstance().getTokenKey(), matchId);
            clientMessenger.sendMessageToMatchmakingService(messageMatchmakingRefuse);
            matchFound = false;
            matchId = "";
        }
    }

    public void enterMatchmaking() {
        MessageMatchmakingSubscribe messageMatchmakingSubscribe = new MessageMatchmakingSubscribe(Client.getInstance().getTokenKey());
        clientMessenger.sendMessageToMatchmakingService(messageMatchmakingSubscribe);
    }

    @Override
    public void update(long deltaTime) {
    }

    @Override
    public void render() {
        GraphicsManager.getInstance().make2D();
        background.draw();
        for (HUDElement hudElement : hudElementList) {
            hudElement.draw();
        }
        if (matchFound) {
            acceptButton.draw();
            refuseButton.draw();
        }
        chatOverlay.draw();
        newsOverlay.draw();
        frame.draw();
    }

    @Override
    public void manageInput() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                boolean hudElementIsClicked = false;
                for (HUDElement hudElement : hudElementList) {
                    if (hudElement.isClicked()) {
                        hudElementIsClicked = true;
                        setFocusOn(hudElementList.indexOf(hudElement));
                        if (hudElement instanceof Button) {
                            ((Button) hudElement).onClick();
                        }
                        break;
                    }
                }
                if (!hudElementIsClicked && acceptButton.isClicked()) {
                    hudElementIsClicked = true;
                    acceptButton.onClick();
                }
                if (!hudElementIsClicked && refuseButton.isClicked()) {
                    hudElementIsClicked = true;
                    refuseButton.onClick();
                }
                if (!hudElementIsClicked && frame.isClicked()) {
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
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (InputManager.getInstance().getInput(Keyboard.getEventKey()) != null) {
                    if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.CANCEL)) {
                        inputChat.deleteLastChar();
                    } else if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.VALIDATE)) {
                        actionPostMessage();
                    } else {
                        inputChat.writeChar(Keyboard.getEventCharacter());
                    }
                } else {
                    inputChat.writeChar(Keyboard.getEventCharacter());
                }
            }
        }
        if (frameClicked) {
            Display.setLocation(Display.getX() + (Mouse.getX()) - x, (Display.getY() + (Display.getHeight() - Mouse.getY())) - y);
        }
    }

    @Override
    public void initConnections() throws IOException {
    }

    @Override
    public void closeConnections() throws IOException {
    }
}
