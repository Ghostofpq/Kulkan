package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.Button;
import com.ghostofpq.kulkan.client.graphics.HUDElement;
import com.ghostofpq.kulkan.client.graphics.TextArea;
import com.ghostofpq.kulkan.client.graphics.TextField;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.client.utils.InputManager;
import com.ghostofpq.kulkan.client.utils.InputMap;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.game.MessageGameStart;
import com.ghostofpq.kulkan.entities.messages.lobby.*;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LobbyScene implements Scene {

    private static volatile LobbyScene instance = null;
    private final String LOBBY_SERVER_QUEUE_NAME_BASE = "/server/lobby";
    private final String MATCHMAKING_SERVER_QUEUE_NAME_BASE = "/server/matchmaking";
    private Channel channelLobbyOut;
    private Channel channelMatchmakingOut;
    private TextField inputText;
    private TextArea lobbyMessages;
    private Button postButton;
    private Button matchmakingButton;
    private Button quitButton;
    private Button acceptButton;
    private Button refuseButton;
    private Button manageTeamButton;
    private boolean matchFound;
    private String matchId;
    private List<HUDElement> hudElementList;
    private int indexOnFocus;

    private LobbyScene() {
    }

    public static LobbyScene getInstance() {
        if (instance == null) {
            synchronized (LoginScene.class) {
                if (instance == null) {
                    instance = new LobbyScene();
                }
            }
        }
        return instance;
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

        quitButton = new Button(550, 0, 50, 50, "QUIT") {
            @Override
            public void onClick() {
                log.debug("QUIT");
                Client.getInstance().quit();
            }
        };

        acceptButton = new Button(550, 100, 50, 50, "ACCEPT") {
            @Override
            public void onClick() {
                log.debug("ACCEPT");
                acceptMatch();
            }
        };

        refuseButton = new Button(550, 150, 50, 50, "REFUSE") {
            @Override
            public void onClick() {
                log.debug("REFUSE");
                refuseMatch();
            }
        };

        manageTeamButton = new Button(550, 200, 50, 50, "MANAGE TEAM") {
            @Override
            public void onClick() {
                log.debug("MANAGE TEAM");
                Client.getInstance().setCurrentScene(TeamManagementScene.getInstance());
            }
        };

        hudElementList.add(inputText);
        hudElementList.add(postButton);
        hudElementList.add(matchmakingButton);
        hudElementList.add(lobbyMessages);
        hudElementList.add(quitButton);
        hudElementList.add(manageTeamButton);
        indexOnFocus = 0;
        setFocusOn(indexOnFocus);
        matchFound = false;
        matchId = "";
        try {
            initConnection();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void initConnection() throws IOException {
        channelLobbyOut = Client.getInstance().getConnection().createChannel();
        channelLobbyOut.queueDeclare(LOBBY_SERVER_QUEUE_NAME_BASE, false, false, false, null);
        channelMatchmakingOut = Client.getInstance().getConnection().createChannel();
        channelMatchmakingOut.queueDeclare(MATCHMAKING_SERVER_QUEUE_NAME_BASE, false, false, false, null);
    }

    public void setFocusOn(int i) {
        for (HUDElement hudElement : hudElementList) {
            hudElement.setHasFocus(false);
        }
        hudElementList.get(i).setHasFocus(true);
    }

    public void postMessage() {
        try {
            if (!inputText.getContent().isEmpty()) {
                MessageLobbyClient messageLobbyClient = new MessageLobbyClient(Client.getInstance().getTokenKey(), inputText.getContent());
                channelLobbyOut.basicPublish("", LOBBY_SERVER_QUEUE_NAME_BASE, null, messageLobbyClient.getBytes());
                log.debug(" [-] WRITE ON {} : [{}]", LOBBY_SERVER_QUEUE_NAME_BASE, messageLobbyClient.getLobbyMessage());
                inputText.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendLobbyPong() {
        MessageLobbyPong pong = new MessageLobbyPong(Client.getInstance().getTokenKey());
        try {
            channelLobbyOut.basicPublish("", LOBBY_SERVER_QUEUE_NAME_BASE, null, pong.getBytes());
            log.debug(" [-] PONG ON {}", LOBBY_SERVER_QUEUE_NAME_BASE);
        } catch (IOException e) {
            e.printStackTrace();
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
                case LOBBY_PING:
                    log.debug(" [x] Received Ping");
                    sendLobbyPong();
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
                    MessageGameStart messageGameStart = (MessageGameStart) message;
                    Client.getInstance().setCurrentScene(BattleScene.getInstance());
                    BattleScene.getInstance().setBattlefield(messageGameStart.getBattlefield());
                    BattleScene.getInstance().setGameId(messageGameStart.getGameID());
                    break;
                default:
                    log.error(" [X] UNEXPECTED MESSAGE : {}", message.getType());
                    break;

            }
        }
    }

    public void acceptMatch() {
        if (null != matchId && !matchId.isEmpty()) {
            MessageMatchmakingAccept messageMatchmakingAccept = new MessageMatchmakingAccept(Client.getInstance().getTokenKey(), matchId);
            try {
                channelMatchmakingOut.basicPublish("", MATCHMAKING_SERVER_QUEUE_NAME_BASE, null, messageMatchmakingAccept.getBytes());
                log.debug(" [-] ACCEPT MATCH");
                matchFound = false;
                matchId = "";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void refuseMatch() {
        if (null != matchId && !matchId.isEmpty()) {
            MessageMatchmakingRefuse messageMatchmakingRefuse = new MessageMatchmakingRefuse(Client.getInstance().getTokenKey(), matchId);
            try {
                channelMatchmakingOut.basicPublish("", MATCHMAKING_SERVER_QUEUE_NAME_BASE, null, messageMatchmakingRefuse.getBytes());
                log.debug(" [-] REFUSE MATCH");
                matchFound = false;
                matchId = "";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void enterMatchmaking() {
        MessageMatchmakingSubscribe messageMatchmakingSubscribe = new MessageMatchmakingSubscribe(Client.getInstance().getTokenKey());
        try {
            channelMatchmakingOut.basicPublish("", MATCHMAKING_SERVER_QUEUE_NAME_BASE, null, messageMatchmakingSubscribe.getBytes());
            log.debug(" [-] SUBSCRIBE MATCHMAKING");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(long deltaTime) {
    }

    @Override
    public void render() {
        GraphicsManager.getInstance().make2D();
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

    public void closeConnections() {
        try {
            channelLobbyOut.close();
            log.debug("channelLobbyOut closed");
            channelMatchmakingOut.close();
            log.debug("channelMatchmakingOut closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
