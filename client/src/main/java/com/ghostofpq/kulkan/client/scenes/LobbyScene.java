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
import com.ghostofpq.kulkan.entities.messages.MessageLobbyClient;
import com.ghostofpq.kulkan.entities.messages.MessageLobbyPong;
import com.ghostofpq.kulkan.entities.messages.MessageLobbyServer;
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
    private Channel channelLobbyOut;
    private TextField inputText;
    private TextArea lobbyMessages;
    private Button postButton;
    private Button matchmakingButton;
    private Button quitButton;
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
                Client.getInstance().quit();
            }
        };
        hudElementList.add(inputText);
        hudElementList.add(postButton);
        hudElementList.add(matchmakingButton);
        hudElementList.add(lobbyMessages);
        hudElementList.add(quitButton);
        indexOnFocus = 0;
        setFocusOn(indexOnFocus);
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
                default:

            }
        }
    }

    public void enterMatchmaking() {

    }

    @Override
    public void update(long deltaTime) {
        receiveMessage();
    }

    @Override
    public void render() {
        GraphicsManager.getInstance().make2D();
        for (HUDElement hudElement : hudElementList) {
            hudElement.draw();
        }
    }

    @Override
    public void manageInput() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                if (inputText.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    setFocusOn(hudElementList.indexOf(inputText));
                }
                if (postButton.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    setFocusOn(hudElementList.indexOf(postButton));
                    postButton.onClick();
                }
                if (matchmakingButton.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    setFocusOn(hudElementList.indexOf(matchmakingButton));
                    matchmakingButton.onClick();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
