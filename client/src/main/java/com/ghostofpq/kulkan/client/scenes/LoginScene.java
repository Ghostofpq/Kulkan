package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.ClientContext;
import com.ghostofpq.kulkan.client.ClientMessenger;
import com.ghostofpq.kulkan.client.graphics.Background;
import com.ghostofpq.kulkan.client.graphics.HUD.Button;
import com.ghostofpq.kulkan.client.graphics.HUD.Frame;
import com.ghostofpq.kulkan.client.graphics.HUD.HUDElement;
import com.ghostofpq.kulkan.client.graphics.HUD.TextField;
import com.ghostofpq.kulkan.client.graphics.PasswordField;
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
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LoginScene implements Scene {
    @Autowired
    private Client client;
    @Autowired
    private ClientContext clientContext;
    @Autowired
    private ClientMessenger clientMessenger;
    @Autowired
    private LobbyScene lobbyScene;
    @Autowired
    private OptionScene optionScene;
    // PSEUDO FIELD
    private TextField pseudoField;
    // PASSWORD FIELD
    private PasswordField passwordField;
    // CONNECT BUTTON
    private Button connectButton;
    private Button optionButton;
    // CREATE ACCOUNT BUTTON
    private Button createAccountButton;
    // QUIT BUTTON
    private Button quitButton;
    // ELEMENT LIST
    private List<HUDElement> hudElementList;
    // BACKGROUND
    private Background background;
    // FRAME
    private Frame frame;
    private int x;
    private int y;
    private boolean frameClicked;

    private LoginScene() {
    }

    @Override
    public void init() {
        int widthStep = clientContext.getCurrentResolution().getWidth() / 10;
        int heightStep = clientContext.getCurrentResolution().getHeight() / 10;
        int textFieldsWidth = 300;
        int textFieldsHeight = 50;
        int pseudoFieldPosX = (5 * widthStep) - (textFieldsWidth / 2);
        int pseudoFieldPosY = (4 * heightStep) - (textFieldsHeight / 2);
        int passwordFieldPosX = pseudoFieldPosX;
        int passwordFieldPosY = pseudoFieldPosY + (textFieldsHeight * 3 / 2);

        int buttonsWidth = 200;
        int buttonsHeight = 50;
        int connectButtonPosX = (5 * widthStep) - (buttonsWidth / 2);
        int connectButtonPosY = passwordFieldPosY + (buttonsHeight * 3 / 2);
        int createAccountButtonPosX = connectButtonPosX;
        int createAccountButtonPosY = connectButtonPosY + buttonsHeight;
        int quitButtonPosX = connectButtonPosX;
        int quitButtonPosY = createAccountButtonPosY + buttonsHeight;

        pseudoField = new TextField(pseudoFieldPosX, pseudoFieldPosY, textFieldsWidth, textFieldsHeight, 10);

        passwordField = new PasswordField(passwordFieldPosX, passwordFieldPosY, textFieldsWidth, textFieldsHeight, 10);

        connectButton = new Button(connectButtonPosX, connectButtonPosY, buttonsWidth, buttonsHeight, "CONNECT") {
            @Override
            public void onClick() {
                actionConnect();
            }
        };

        createAccountButton = new Button(createAccountButtonPosX, createAccountButtonPosY, buttonsWidth, buttonsHeight, "CREATE ACCOUNT") {
            @Override
            public void onClick() {
                actionCreateAccount();
            }
        };
        quitButton = new Button(quitButtonPosX, quitButtonPosY, buttonsWidth, buttonsHeight, "QUIT") {
            @Override
            public void onClick() {
                actionQuit();
            }
        };

        optionButton = new Button(10, 10, 50, 50, "Opt.") {
            @Override
            public void onClick() {
                actionOption();
            }
        };

        frame = new Frame(0, 0, clientContext.getCurrentResolution().getWidth(), clientContext.getCurrentResolution().getHeight(), clientContext.getCurrentResolution().getWidth() / 32, clientContext.getCurrentResolution().getWidth() / 32, TextureKey.LOBBY_EXT_FRAME);

        hudElementList = new ArrayList<HUDElement>();

        hudElementList.add(pseudoField);
        hudElementList.add(passwordField);
        hudElementList.add(connectButton);
        hudElementList.add(quitButton);
        hudElementList.add(optionButton);
        hudElementList.add(createAccountButton);
        hudElementList.add(frame);

        setFocusOn(hudElementList.indexOf(pseudoField));

        background = new Background(TextureKey.LOGIN_BACKGROUND);
    }

    private void actionConnect() {
        try {
            MessageAuthenticationRequest authenticationRequest = new MessageAuthenticationRequest(pseudoField.getContent(), passwordField.getContent());
            Message result = clientMessenger.requestOnAuthenticationChannel(authenticationRequest);
            if (null != result) {
                if (result.getType().equals(MessageType.AUTHENTICATION_RESPONSE)) {
                    MessageAuthenticationResponse response = (MessageAuthenticationResponse) result;
                    if (response.getErrorCode().equals(MessageErrorCode.OK)) {
                        log.debug("AUTH OK : key={}", response.getTokenKey());
                        clientContext.setPlayer(response.getPlayer());
                        clientContext.setTokenKey(response.getTokenKey());
                        clientMessenger.openChannelsAfterAuthentication(response.getTokenKey());
                        Client.getInstance().setTokenKey(response.getTokenKey());
                        Client.getInstance().setCurrentScene(lobbyScene);
                    } else {
                        log.debug("AUTH KO : BAD INFO");
                    }
                }
            } else {
                log.debug("SERVER DOWN");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actionQuit() {
        log.debug("QUIT");
        Client.getInstance().quit();
    }

    private void actionOption() {
        log.debug("OPTION");
        client.setCurrentScene(optionScene);
    }

    private void actionCreateAccount() {
        try {
            MessageCreateAccount messageCreateAccount = new MessageCreateAccount(pseudoField.getContent(), passwordField.getContent());
            Message result = clientMessenger.requestOnAuthenticationChannel(messageCreateAccount);
            if (null != result) {
                if (result.getType().equals(MessageType.AUTHENTICATION_RESPONSE)) {
                    MessageCreateAccountResponse response = (MessageCreateAccountResponse) result;
                    if (response.getErrorCode().equals(MessageErrorCode.OK)) {
                        log.debug("CREATE ACCOUT OK");
                    } else {
                        log.debug("CREATE ACCOUT KO : USER ALREADY USED");
                    }
                }
            } else {
                log.debug("SERVER DOWN");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the focus on an element of the hudElementList.
     *
     * @param index of the element having the focus.
     */
    private void setFocusOn(int index) {
        for (HUDElement hudElement : hudElementList) {
            hudElement.setHasFocus(false);
        }
        hudElementList.get(index).setHasFocus(true);
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
                if (pseudoField.isClicked()) {
                    setFocusOn(hudElementList.indexOf(pseudoField));
                } else if (passwordField.isClicked()) {
                    setFocusOn(hudElementList.indexOf(passwordField));
                } else if (connectButton.isClicked()) {
                    setFocusOn(hudElementList.indexOf(connectButton));
                    connectButton.onClick();
                } else if (quitButton.isClicked()) {
                    setFocusOn(hudElementList.indexOf(quitButton));
                    quitButton.onClick();
                } else if (createAccountButton.isClicked()) {
                    setFocusOn(hudElementList.indexOf(createAccountButton));
                    createAccountButton.onClick();
                } else if (optionButton.isClicked()) {
                    setFocusOn(hudElementList.indexOf(optionButton));
                    optionButton.onClick();
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
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (InputManager.getInstance().getInput(Keyboard.getEventKey()) != null) {
                    if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.VALIDATE)) {
                        if (pseudoField.hasFocus()) {
                            setFocusOn(hudElementList.indexOf(passwordField));
                        } else if (passwordField.hasFocus()) {
                            setFocusOn(hudElementList.indexOf(connectButton));
                            connectButton.onClick();
                        } else if (connectButton.hasFocus()) {
                            connectButton.onClick();
                        } else if (quitButton.hasFocus()) {
                            quitButton.onClick();
                        } else if (createAccountButton.hasFocus()) {
                            createAccountButton.onClick();
                        }
                    } else if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.SWITCH)) {
                        if (pseudoField.hasFocus()) {
                            setFocusOn(hudElementList.indexOf(passwordField));
                        } else if (passwordField.hasFocus()) {
                            setFocusOn(hudElementList.indexOf(connectButton));
                        } else if (connectButton.hasFocus()) {
                            setFocusOn(hudElementList.indexOf(quitButton));
                        } else if (quitButton.hasFocus()) {
                            setFocusOn(hudElementList.indexOf(createAccountButton));
                        } else if (createAccountButton.hasFocus()) {
                            setFocusOn(hudElementList.indexOf(pseudoField));
                        }
                    } else if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.CANCEL)) {
                        if (pseudoField.hasFocus()) {
                            pseudoField.deleteLastChar();
                        } else if (passwordField.hasFocus()) {
                            passwordField.deleteLastChar();
                        }
                    } else {
                        if (pseudoField.hasFocus()) {
                            pseudoField.writeChar(Keyboard.getEventCharacter());
                        } else if (passwordField.hasFocus()) {
                            passwordField.writeChar(Keyboard.getEventCharacter());
                        }
                    }
                } else {
                    if (pseudoField.hasFocus()) {
                        pseudoField.writeChar(Keyboard.getEventCharacter());
                    } else if (passwordField.hasFocus()) {
                        passwordField.writeChar(Keyboard.getEventCharacter());
                    }
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
