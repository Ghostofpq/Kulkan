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
import com.ghostofpq.kulkan.entities.messages.user.MessageError;
import com.ghostofpq.kulkan.entities.messages.user.MessagePlayerUpdate;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LobbyScene implements Scene {

    private Button matchmakingButton;

    private boolean matchFound;
    private String matchId;
    private int indexOnFocus;

    private List<HUDElement> hudElementList;
    // MENU
    private Button shopButton;
    private Button stockButton;
    private Button optionButton;
    private Button quitButton;
    // CHAT & NEWS
    private TextField inputChat;
    private Button postButton;
    private TextArea chat;
    private TextArea news;
    // OVERLAYS
    private HUDTexturedElement chatOverlay;
    private HUDTexturedElement newsOverlay;
    // TEAM
    private Button teamCharacter1;
    private Button teamCharacter2;
    private Button teamCharacter3;
    private Button teamCharacter4;
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
    private Client client;
    @Autowired
    private ClientContext clientContext;
    @Autowired
    private ClientMessenger clientMessenger;
    @Autowired
    private OptionScene optionScene;
    @Autowired
    private CreateGameCharacterScene createGameCharacterScene;
    @Autowired
    private ManageGameCharacterScene manageGameCharacterScene;
    @Autowired
    private BattleScene battleScene;
    @Autowired
    private ShopScene shopScene;
    @Autowired
    private StockScene stockScene;

    public LobbyScene() {
    }

    @Override
    public void init() {
        hudElementList = new ArrayList<HUDElement>();

        // PADDING TO AVOID HAVING TEXT RIGHT NEXT TO THE BORDER
        int padding = 10;

        // CHAT
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

        //NEWS
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


        // MENU
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
                log.debug("SHOP");
                exitLobby();
                client.setCurrentScene(shopScene);
            }
        };

        int stockButtonPosX = matchmakingButtonPosX;
        int stockButtonPosY = shopButtonPosY + shopButtonHeight;
        int stockButtonWidth = matchmakingButtonWidth;
        int stockButtonHeight = matchmakingButtonHeight;
        stockButton = new Button(stockButtonPosX, stockButtonPosY, stockButtonWidth, stockButtonHeight, "STOCK", null, null) {
            @Override
            public void onClick() {
                log.debug("STOCK");
                exitLobby();
                client.setCurrentScene(stockScene);
            }
        };

        int optionButtonPosX = matchmakingButtonPosX;
        int optionButtonPosY = stockButtonPosY + stockButtonHeight;
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
                actionQuit();
            }
        };

        // TEAM  teamCharacter1
        int teamCharacter1PosX = clientContext.getCurrentResolution().getWidth() * 3 / 16;
        int teamCharacter1PosY = clientContext.getCurrentResolution().getHeight() * 7 / 27;
        int teamCharacter1Width = clientContext.getCurrentResolution().getWidth() / 10;
        int teamCharacter1Height = clientContext.getCurrentResolution().getHeight() / 3;
        if (clientContext.getPlayer().getTeam().size() > 0 && null != clientContext.getPlayer().getTeam().get(0)) {
            teamCharacter1 = new Button(teamCharacter1PosX, teamCharacter1PosY, teamCharacter1Width, teamCharacter1Height, clientContext.getPlayer().getTeam().get(0).getName(), TextureKey.COMMON_CHAR_SHADOW, TextureKey.COMMON_CHAR_SHADOW) {
                @Override
                public void onClick() {
                    manageCharacter1();
                }
            };
        } else {
            teamCharacter1 = new Button(teamCharacter1PosX, teamCharacter1PosY, teamCharacter1Width, teamCharacter1Height, "NEW", null, null) {
                @Override
                public void onClick() {
                    actionCreateCharacter();
                }
            };
        }

        // TEAM  teamCharacter2
        int teamCharacter2PosX = teamCharacter1PosX + teamCharacter1Width + (clientContext.getCurrentResolution().getWidth() * 13 / 960);
        int teamCharacter2PosY = teamCharacter1PosY;
        int teamCharacter2Width = teamCharacter1Width;
        int teamCharacter2Height = teamCharacter1Height;
        if (clientContext.getPlayer().getTeam().size() > 1 && null != clientContext.getPlayer().getTeam().get(1)) {
            teamCharacter2 = new Button(teamCharacter2PosX, teamCharacter2PosY, teamCharacter2Width, teamCharacter2Height, clientContext.getPlayer().getTeam().get(1).getName(), TextureKey.COMMON_CHAR_SHADOW, TextureKey.COMMON_CHAR_SHADOW) {
                @Override
                public void onClick() {
                    manageCharacter2();
                }
            };
        } else {
            teamCharacter2 = new Button(teamCharacter2PosX, teamCharacter2PosY, teamCharacter2Width, teamCharacter2Height, "NEW", null, null) {
                @Override
                public void onClick() {
                    actionCreateCharacter();
                }
            };
        }

        // TEAM  teamCharacter3
        int teamCharacter3PosX = teamCharacter2PosX + teamCharacter1Width + (clientContext.getCurrentResolution().getWidth() * 19 / 96);
        int teamCharacter3PosY = teamCharacter1PosY;
        int teamCharacter3Width = teamCharacter1Width;
        int teamCharacter3Height = teamCharacter1Height;
        if (clientContext.getPlayer().getTeam().size() > 2 && null != clientContext.getPlayer().getTeam().get(2)) {
            teamCharacter3 = new Button(teamCharacter3PosX, teamCharacter3PosY, teamCharacter3Width, teamCharacter3Height, clientContext.getPlayer().getTeam().get(2).getName(), TextureKey.COMMON_CHAR_SHADOW, TextureKey.COMMON_CHAR_SHADOW) {
                @Override
                public void onClick() {
                    manageCharacter3();
                }
            };
        } else {
            teamCharacter3 = new Button(teamCharacter3PosX, teamCharacter3PosY, teamCharacter3Width, teamCharacter3Height, "NEW", null, null) {
                @Override
                public void onClick() {
                    actionCreateCharacter();
                }
            };
        }

        // TEAM  teamCharacter1
        int teamCharacter4PosX = teamCharacter3PosX + teamCharacter1Width + (clientContext.getCurrentResolution().getWidth() * 13 / 960);
        int teamCharacter4PosY = teamCharacter1PosY;
        int teamCharacter4Width = teamCharacter1Width;
        int teamCharacter4Height = teamCharacter1Height;
        if (clientContext.getPlayer().getTeam().size() > 3 && null != clientContext.getPlayer().getTeam().get(3)) {
            teamCharacter4 = new Button(teamCharacter4PosX, teamCharacter4PosY, teamCharacter4Width, teamCharacter4Height, clientContext.getPlayer().getTeam().get(3).getName(), TextureKey.COMMON_CHAR_SHADOW, TextureKey.COMMON_CHAR_SHADOW) {
                @Override
                public void onClick() {
                    manageCharacter4();
                }
            };
        } else {
            teamCharacter4 = new Button(teamCharacter4PosX, teamCharacter4PosY, teamCharacter4Width, teamCharacter4Height, "NEW", null, null) {
                @Override
                public void onClick() {
                    actionCreateCharacter();
                }
            };
        }

        hudElementList.add(inputChat);
        hudElementList.add(postButton);
        hudElementList.add(matchmakingButton);
        hudElementList.add(chat);
        hudElementList.add(quitButton);
        hudElementList.add(shopButton);
        hudElementList.add(stockButton);
        hudElementList.add(optionButton);
        hudElementList.add(news);
        hudElementList.add(teamCharacter1);
        hudElementList.add(teamCharacter2);
        hudElementList.add(teamCharacter3);
        hudElementList.add(teamCharacter4);
        indexOnFocus = 0;
        setFocusOn(indexOnFocus);
        matchFound = false;
        matchId = "";
        background = new Background(TextureKey.LOBBY_BACKGROUND_169);
        frame = new Frame(0, 0, clientContext.getCurrentResolution().getWidth(), clientContext.getCurrentResolution().getHeight(), clientContext.getCurrentResolution().getWidth() / 64, clientContext.getCurrentResolution().getWidth() / 64, TextureKey.COMMON_EXT_FRAME);

        MessageSubscribeToLobby messageSubscribeToLobby = new MessageSubscribeToLobby(clientContext.getTokenKey());
        clientMessenger.sendMessageToLobbyService(messageSubscribeToLobby);
    }

    private void manageCharacter1() {
        clientContext.setSelectedCharacterId(clientContext.getPlayer().getTeam().get(0).getId());
        client.setCurrentScene(manageGameCharacterScene);
    }

    private void manageCharacter2() {
        clientContext.setSelectedCharacterId(clientContext.getPlayer().getTeam().get(1).getId());
        client.setCurrentScene(manageGameCharacterScene);
    }

    private void manageCharacter3() {
        clientContext.setSelectedCharacterId(clientContext.getPlayer().getTeam().get(2).getId());
        client.setCurrentScene(manageGameCharacterScene);
    }

    private void manageCharacter4() {
        clientContext.setSelectedCharacterId(clientContext.getPlayer().getTeam().get(3).getId());
        client.setCurrentScene(manageGameCharacterScene);
    }


    private void actionOption() {
        log.debug("OPTION");
        optionScene.setLastScene(this);
        client.setCurrentScene(optionScene);
    }

    private void actionQuit() {
        log.debug("QUIT");
        exitLobby();
        client.quit();
    }

    private void actionCreateCharacter() {
        client.setCurrentScene(createGameCharacterScene);
    }

    private void actionNewPlayer() {
        log.debug("Action : New Player");
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
            MessageLobbyClient messageLobbyClient = new MessageLobbyClient(clientContext.getTokenKey(), inputChat.getLabel());
            clientMessenger.sendMessageToLobbyService(messageLobbyClient);
            inputChat.clear();
        }
    }

    @Override
    public void receiveMessage() {
        Message message = clientMessenger.receiveMessage();
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
                    List<String> popUpOptions = new ArrayList<String>();
                    popUpOptions.add("ACCEPT");
                    popUpOptions.add("REFUSE");
                    popUp = new PopUp(popUpOptions, new StringBuilder().append("Match found : [").append(matchId).append("]").toString());
                    break;
                case MATCHMAKING_MATCH_ABORT:
                    log.debug(" [x] MATCH ABORT");
                    matchFound = false;
                    matchId = "";
                    break;
                case GAME_START:
                    log.debug(" [x] GAME START");
                    exitLobby();
                    popUp = null;
                    MessageGameStart messageGameStart = (MessageGameStart) message;
                    client.setCurrentScene(battleScene);
                    battleScene.setBattlefield(messageGameStart.getBattlefield());
                    battleScene.setGameId(messageGameStart.getGameID());
                    break;
                case PLAYER_UPDATE:
                    log.debug(" [x] PLAYER_UPDATE");
                    MessagePlayerUpdate messagePlayerUpdate = (MessagePlayerUpdate) message;
                    clientContext.setPlayer(messagePlayerUpdate.getPlayer());
                    break;
                case ERROR:
                    List<String> options = new ArrayList<String>();
                    options.add("OK");
                    MessageError messageError = (MessageError) message;
                    popUp = new PopUp(options, messageError.getError());
                    break;
                default:
                    log.error(" [X] UNEXPECTED MESSAGE : {}", message.getType());
                    break;

            }
        }
    }

    private void exitLobby() {
        MessageUnsubscribeToLobby messageUnsubscribeToLobby = new MessageUnsubscribeToLobby(clientContext.getTokenKey());
        clientMessenger.sendMessageToLobbyService(messageUnsubscribeToLobby);
    }

    public void acceptMatch() {
        if (null != matchId && !matchId.isEmpty()) {
            MessageMatchmakingAccept messageMatchmakingAccept = new MessageMatchmakingAccept(clientContext.getTokenKey(), matchId);
            clientMessenger.sendMessageToMatchmakingService(messageMatchmakingAccept);
            matchFound = false;
            matchId = "";
        }
    }

    public void refuseMatch() {
        if (null != matchId && !matchId.isEmpty()) {
            MessageMatchmakingRefuse messageMatchmakingRefuse = new MessageMatchmakingRefuse(clientContext.getTokenKey(), matchId);
            clientMessenger.sendMessageToMatchmakingService(messageMatchmakingRefuse);
            matchFound = false;
            matchId = "";
        }
    }

    public void enterMatchmaking() {
        MessageMatchmakingSubscribe messageMatchmakingSubscribe = new MessageMatchmakingSubscribe(clientContext.getTokenKey());
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
        chatOverlay.draw();
        newsOverlay.draw();
        if (null != popUp) {
            popUp.draw();
        }
        frame.draw();
    }

    @Override
    public void manageInput() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                boolean hudElementIsClicked = false;
                if (null == popUp) {
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
                } else if (popUp.isClicked()) {
                    String onClick = popUp.onClick();
                    if (null != onClick) {
                        if (onClick.equals("OK")) {
                            popUp = null;
                        } else if (onClick.equals("ACCEPT")) {
                            acceptMatch();
                        } else if (onClick.equals("REFUSE")) {
                            refuseMatch();
                            popUp = null;
                        }
                    }
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
        if (frameClicked && !clientContext.isFullscreen()) {
            Display.setLocation(Display.getX() + (Mouse.getX()) - x, (Display.getY() + (Display.getHeight() - Mouse.getY())) - y);
        }
    }
}
