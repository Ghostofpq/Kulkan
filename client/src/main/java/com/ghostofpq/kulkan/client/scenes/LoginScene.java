package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.*;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.client.utils.InputManager;
import com.ghostofpq.kulkan.client.utils.InputMap;
import com.ghostofpq.kulkan.client.utils.TextureKey;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageErrorCode;
import com.ghostofpq.kulkan.entities.messages.MessageType;
import com.ghostofpq.kulkan.entities.messages.auth.MessageAuthenticationRequest;
import com.ghostofpq.kulkan.entities.messages.auth.MessageAuthenticationResponse;
import com.ghostofpq.kulkan.entities.messages.auth.MessageCreateAccount;
import com.ghostofpq.kulkan.entities.messages.auth.MessageCreateAccountResponse;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginScene implements Scene {
    private static final Logger LOG = LoggerFactory.getLogger(LoginScene.class);
    private static volatile LoginScene instance = null;
    private final String AUTHENTICATION_QUEUE_NAME = "authentication";
    private String authenticationReplyQueueName;
    private Channel channelAuthenticating;
    private TextField pseudo;
    private PasswordField password;
    private List<HUDElement> hudElementList;
    private Button button;
    private int indexOnFocus;
    private QueueingConsumer consumer;
    private Button quitButton;
    private Button createAccountButton;
    private Background background;

    private LoginScene() {
    }

    public static LoginScene getInstance() {
        if (instance == null) {
            synchronized (LoginScene.class) {
                if (instance == null) {
                    instance = new LoginScene();
                }
            }
        }
        return instance;
    }

    @Override
    public void init() {
        pseudo = new TextField(250, 200, 300, 50, 10);
        password = new PasswordField(250, 300, 300, 50, 10);
        button = new Button(300, 400, 200, 50, "CONNECT") {
            @Override
            public void onClick() {
                try {
                    MessageAuthenticationRequest authenticationRequest = new MessageAuthenticationRequest(pseudo.getContent(), password.getContent());
                    Message result = requestServer(authenticationRequest);
                    if (null != result) {
                        if (result.getType().equals(MessageType.AUTHENTICATION_RESPONSE)) {
                            MessageAuthenticationResponse response = (MessageAuthenticationResponse) result;
                            if (response.getErrorCode().equals(MessageErrorCode.OK)) {
                                LOG.debug("AUTH OK : key={}", response.getTokenKey());
                                Client.getInstance().setPlayer(response.getPlayer());
                                Client.getInstance().setTokenKey(response.getTokenKey());
                                Client.getInstance().setCurrentScene(LobbyScene.getInstance());
                            } else {
                                LOG.debug("AUTH KO : BAD INFO");
                            }
                        }
                    } else {
                        LOG.debug("SERVER DOWN");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };


        quitButton = new

                Button(300, 450, 200, 50, "QUIT") {
                    @Override
                    public void onClick() {
                        LOG.debug("QUIT");
                        Client.getInstance().quit();
                    }
                };


        createAccountButton = new

                Button(300, 500, 200, 50, "CREATE ACCOUNT") {
                    @Override
                    public void onClick() {
                        try {
                            MessageCreateAccount messageCreateAccount = new MessageCreateAccount(pseudo.getContent(), password.getContent());
                            Message result = requestServer(messageCreateAccount);
                            if (null != result) {
                                if (result.getType().equals(MessageType.AUTHENTICATION_RESPONSE)) {
                                    MessageCreateAccountResponse response = (MessageCreateAccountResponse) result;
                                    if (response.getErrorCode().equals(MessageErrorCode.OK)) {
                                        LOG.debug("CREATE ACCOUT OK");
                                    } else {
                                        LOG.debug("CREATE ACCOUT KO : USER ALREADY USED");
                                    }
                                }
                            } else {
                                LOG.debug("SERVER DOWN");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
        hudElementList = new ArrayList<HUDElement>();
        hudElementList.add(pseudo);
        hudElementList.add(password);
        hudElementList.add(button);
        hudElementList.add(quitButton);
        hudElementList.add(createAccountButton);
        indexOnFocus = 0;
        setFocusOn(indexOnFocus);
        background = new Background(TextureKey.LOGIN_BACKGROUND);
    }

    public void initConnections() throws IOException {
        channelAuthenticating = Client.getInstance().getConnection().createChannel();
        authenticationReplyQueueName = channelAuthenticating.queueDeclare().getQueue();
        consumer = new QueueingConsumer(channelAuthenticating);
        channelAuthenticating.basicConsume(authenticationReplyQueueName, true, consumer);
    }

    public Message requestServer(Message message) throws Exception {
        LOG.debug("create account");
        Message response = null;
        String corrId = java.util.UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(authenticationReplyQueueName)
                .build();
        channelAuthenticating.basicPublish("", AUTHENTICATION_QUEUE_NAME, props, message.getBytes());
        LOG.debug(" [x] Sent '{}'", message.getType());
        QueueingConsumer.Delivery delivery = consumer.nextDelivery(1000);
        if (null != delivery) {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response = Message.loadFromBytes(delivery.getBody());
                LOG.debug(" [x] Received '{}'", response.getType());
            }
        }
        return response;
    }

    public void setFocusOn(int i) {
        for (HUDElement hudElement : hudElementList) {
            hudElement.setHasFocus(false);
        }
        hudElementList.get(i).setHasFocus(true);
    }

    @Override
    public void update(long deltaTime) {
    }

    @Override
    public void receiveMessage() {
    }

    @Override
    public void render() {
        GraphicsManager.getInstance().make2D();
        background.draw();
        for (HUDElement hudElement : hudElementList) {
            hudElement.draw();
        }
    }

    @Override
    public void manageInput() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                if (pseudo.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    setFocusOn(hudElementList.indexOf(pseudo));
                }
                if (password.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    setFocusOn(hudElementList.indexOf(password));
                }
                if (button.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    setFocusOn(hudElementList.indexOf(button));
                    button.onClick();
                }
                if (quitButton.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    setFocusOn(hudElementList.indexOf(quitButton));
                    quitButton.onClick();
                }
                if (createAccountButton.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    setFocusOn(hudElementList.indexOf(createAccountButton));
                    createAccountButton.onClick();
                }
            }
        }
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (InputManager.getInstance().getInput(Keyboard.getEventKey()) != null) {
                    if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.CANCEL)) {
                        if (pseudo.hasFocus()) {
                            pseudo.deleteLastChar();
                        } else if (password.hasFocus()) {
                            password.deleteLastChar();
                        }
                    } else {
                        if (pseudo.hasFocus()) {
                            pseudo.writeChar(Keyboard.getEventCharacter());
                        } else if (password.hasFocus()) {
                            password.writeChar(Keyboard.getEventCharacter());
                        }
                    }
                } else {
                    if (pseudo.hasFocus()) {
                        pseudo.writeChar(Keyboard.getEventCharacter());
                    } else if (password.hasFocus()) {
                        password.writeChar(Keyboard.getEventCharacter());
                    }
                }

            }
        }
    }

    public void closeConnections() throws IOException {
        channelAuthenticating.close();
        LOG.debug("channelAuthenticating closed");
    }
}
