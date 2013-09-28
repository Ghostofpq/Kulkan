package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.Button;
import com.ghostofpq.kulkan.client.graphics.HUDElement;
import com.ghostofpq.kulkan.client.graphics.TextField;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.client.utils.InputManager;
import com.ghostofpq.kulkan.client.utils.InputMap;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageLobbyClient;
import com.ghostofpq.kulkan.entities.messages.MessageLobbyServer;
import com.ghostofpq.kulkan.entities.messages.MessageType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
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
    private final String LOBBY_CLIENT_QUEUE_NAME_BASE = "/client/lobby";
    private Channel channelLobbyOut;
    private Channel channelLobbyIn;
    private QueueingConsumer consumerLobbyIn;
    private List<String> lobbyText;
    private TextField inputText;
    private Button postButton;
    private Button matchmakingButton;
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
        lobbyText = new ArrayList<String>();
        hudElementList = new ArrayList<HUDElement>();
        inputText = new TextField(100, 400, 300, 50, 120);
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
        hudElementList.add(inputText);
        hudElementList.add(postButton);
        hudElementList.add(matchmakingButton);

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
        channelLobbyIn = Client.getInstance().getConnection().createChannel();
        channelLobbyOut = Client.getInstance().getConnection().createChannel();

        channelLobbyIn.queueDeclare(LOBBY_SERVER_QUEUE_NAME_BASE, false, false, false, null);
        channelLobbyOut.queueDeclare(LOBBY_CLIENT_QUEUE_NAME_BASE, false, false, false, null);

        consumerLobbyIn = new QueueingConsumer(channelLobbyIn);
    }

    public void setFocusOn(int i) {
        for (HUDElement hudElement : hudElementList) {
            hudElement.setHasFocus(false);
        }
        hudElementList.get(i).setHasFocus(true);
    }

    public void postMessage() {
        try {
            MessageLobbyClient messageLobbyClient = new MessageLobbyClient(Client.getInstance().getTokenKey(), inputText.getContent());
            log.debug(" [x] Sending '{}' : [{}]", messageLobbyClient.getType(), messageLobbyClient.getLobbyMessage());
            channelLobbyOut.basicPublish("", LOBBY_CLIENT_QUEUE_NAME_BASE, null, messageLobbyClient.getBytes());
            inputText.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage() {
        try {
            QueueingConsumer.Delivery delivery = consumerLobbyIn.nextDelivery();
            Message message = Message.loadFromBytes(delivery.getBody());
            if (null != message) {

                if (message.getType().equals(MessageType.LOBBY_SERVER)) {

                    MessageLobbyServer receivedMessage = (MessageLobbyServer) message;
                    String receivedTextMessage = receivedMessage.getMessage(Client.getInstance().getTokenKey());
                    log.debug(" [x] Received '{}' : [{}]", receivedMessage.getType(), receivedTextMessage);
                    if (!receivedTextMessage.isEmpty()) {
                        lobbyText.add(receivedTextMessage);
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
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
}
