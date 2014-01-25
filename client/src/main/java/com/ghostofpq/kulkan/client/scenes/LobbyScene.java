package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.ClientContext;
import com.ghostofpq.kulkan.client.ClientMessenger;
import com.ghostofpq.kulkan.client.graphics.*;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LobbyScene implements Scene {
    private TextField inputText;
    private TextArea lobbyMessages;
    private Button postButton;
    private Button matchmakingButton;
    private Button shopButton;
    private Button quitButton;
    private Button acceptButton;
    private Button refuseButton;
    private Button manageTeamButton;
    private boolean matchFound;
    private String matchId;
    private List<HUDElement> hudElementList;
    private int indexOnFocus;
    private Background background;
    @Autowired
    private ClientContext clientContext;
    @Autowired
    private ClientMessenger clientMessenger;

    public LobbyScene() {
    }

    @Override
    public void init() {
        hudElementList = new ArrayList<HUDElement>();
        inputText = new TextField(100, 400, 300, 50, 120);
        lobbyMessages = new TextArea(100, 100, 60, 10);
        postButton = new Button(450, 400, 50, 50, "POST") {
            @Override
            public void onClick() {
                postMessage();
            }
        };
        matchmakingButton = new

                Button(550, 400, 50, 50, "GAME") {
                    @Override
                    public void onClick() {
                        enterMatchmaking();
                    }
                };

        quitButton = new

                Button(550, 0, 50, 50, "QUIT") {
                    @Override
                    public void onClick() {
                        log.debug("QUIT");
                        Client.getInstance().quit();
                    }
                };

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
        shopButton = new

                Button(550, 250, 50, 50, "SHOP") {
                    @Override
                    public void onClick() {
                        log.debug("SHOP TEAM");
                        exitLobby();
                        Client.getInstance().setCurrentScene(ShopScene.getInstance());
                    }
                };
        hudElementList.add(inputText);
        hudElementList.add(postButton);
        hudElementList.add(matchmakingButton);
        hudElementList.add(lobbyMessages);
        hudElementList.add(quitButton);
        hudElementList.add(manageTeamButton);
        hudElementList.add(shopButton);
        indexOnFocus = 0;
        setFocusOn(indexOnFocus);
        matchFound = false;
        matchId = "";
        background = new Background(TextureKey.BACKGROUND_BASIC);
        MessageSubscribeToLobby messageSubscribeToLobby = new MessageSubscribeToLobby(Client.getInstance().getTokenKey());
        clientMessenger.sendMessageToLobbyService(messageSubscribeToLobby);
    }

    public void setFocusOn(int i) {
        for (HUDElement hudElement : hudElementList) {
            hudElement.setHasFocus(false);
        }
        hudElementList.get(i).setHasFocus(true);
    }

    public void postMessage() {
        if (!inputText.getContent().isEmpty()) {
            MessageLobbyClient messageLobbyClient = new MessageLobbyClient(Client.getInstance().getTokenKey(), inputText.getContent());
            clientMessenger.sendMessageToLobbyService(messageLobbyClient);
            inputText.clear();
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
                        lobbyMessages.addLine(receivedTextMessage);
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
    }

    @Override
    public void manageInput() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                for (HUDElement hudElement : hudElementList) {
                    if (hudElement.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                        setFocusOn(hudElementList.indexOf(hudElement));
                        if (hudElement instanceof Button) {
                            ((Button) hudElement).onClick();
                        }
                    }
                }
                if (acceptButton.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    acceptButton.onClick();
                }
                if (refuseButton.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    refuseButton.onClick();
                }
            }
        }
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (InputManager.getInstance().getInput(Keyboard.getEventKey()) != null) {
                    if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.CANCEL)) {
                        if (inputText.hasFocus()) {
                            inputText.deleteLastChar();
                        }
                    } else {
                        if (inputText.hasFocus()) {
                            inputText.writeChar(Keyboard.getEventCharacter());
                        }
                    }
                } else {
                    if (inputText.hasFocus()) {
                        inputText.writeChar(Keyboard.getEventCharacter());
                    }
                }

            }
        }
    }

    @Override
    public void initConnections() throws IOException {
    }

    @Override
    public void closeConnections() throws IOException {
    }
}
