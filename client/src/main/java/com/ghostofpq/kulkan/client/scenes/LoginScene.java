package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.ClientContext;
import com.ghostofpq.kulkan.client.ClientMessenger;
import com.ghostofpq.kulkan.client.graphics.Background;
import com.ghostofpq.kulkan.client.graphics.HUD.Button;
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

    private LoginScene() {
    }

    @Override
    public void init() {
        pseudoField = new TextField(250, 200, 300, 50, 10);

        passwordField = new PasswordField(250, 300, 300, 50, 10);

        connectButton = new
                Button(300, 400, 200, 50, "CONNECT") {
                    @Override
                    public void onClick() {
                        actionConnect();
                    }
                };

        quitButton = new

                Button(300, 450, 200, 50, "QUIT") {
                    @Override
                    public void onClick() {
                        actionQuit();
                    }
                };
        optionButton = new

                Button(10, 10, 50, 50, "Opt.") {
                    @Override
                    public void onClick() {
                        actionOption();
                    }
                };

        createAccountButton = new

                Button(300, 500, 200, 50, "CREATE ACCOUNT") {
                    @Override
                    public void onClick() {
                        actionCreateAccount();
                    }
                };

        hudElementList = new ArrayList<HUDElement>();

        hudElementList.add(pseudoField);
        hudElementList.add(passwordField);
        hudElementList.add(connectButton);
        hudElementList.add(quitButton);
        hudElementList.add(optionButton);
        hudElementList.add(createAccountButton);
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
                }
                if (passwordField.isClicked()) {
                    setFocusOn(hudElementList.indexOf(passwordField));
                }
                if (connectButton.isClicked()) {
                    setFocusOn(hudElementList.indexOf(connectButton));
                    connectButton.onClick();
                }
                if (quitButton.isClicked()) {
                    setFocusOn(hudElementList.indexOf(quitButton));
                    quitButton.onClick();
                }
                if (createAccountButton.isClicked()) {
                    setFocusOn(hudElementList.indexOf(createAccountButton));
                    createAccountButton.onClick();
                }
                if (optionButton.isClicked()) {
                    setFocusOn(hudElementList.indexOf(optionButton));
                    optionButton.onClick();
                }
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
    }

    @Override
    public void initConnections() throws IOException {
    }

    @Override
    public void closeConnections() throws IOException {
    }
}
