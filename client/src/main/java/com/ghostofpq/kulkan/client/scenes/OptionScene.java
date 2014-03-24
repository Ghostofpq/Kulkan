package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.ClientContext;
import com.ghostofpq.kulkan.client.graphics.Background;
import com.ghostofpq.kulkan.client.graphics.HUD.Button;
import com.ghostofpq.kulkan.client.graphics.HUD.Frame;
import com.ghostofpq.kulkan.client.graphics.HUD.TextField;
import com.ghostofpq.kulkan.client.graphics.HUD.TextZone;
import com.ghostofpq.kulkan.client.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class OptionScene implements Scene {
    @Autowired
    private ClientContext clientContext;
    @Autowired
    private Client client;
    @Autowired
    private LoginScene loginScene;

    private Scene lastScene;
    private Button applyButton;
    private Button backButton;
    private int index;
    private ResolutionRatio resolutionRatio;

    // RESOLUTION CHOICE
    private TextZone resolution;
    private Button nextButton;
    private Button prevButton;
    private Button switchRatioButton;
    // NETWORK
    private TextField serverIP;
    private TextField serverPort;

    // BACKGROUND
    private Background background;
    // FRAME
    private Frame frame;
    private int x;
    private int y;
    private boolean frameClicked;

    public void setLastScene(Scene lastScene) {
        this.lastScene = lastScene;
    }

    public OptionScene() {
    }

    @Override
    public void init() {
        background = new Background(TextureKey.BACKGROUND_BASIC);
        frame = new Frame(0, 0, clientContext.getCurrentResolution().getWidth(), clientContext.getCurrentResolution().getHeight(), clientContext.getCurrentResolution().getWidth() / 64, clientContext.getCurrentResolution().getWidth() / 64, TextureKey.COMMON_EXT_FRAME);
        index = 0;

        int widthStep = clientContext.getCurrentResolution().getWidth() / 10;
        int heightStep = clientContext.getCurrentResolution().getHeight() / 10;

        int buttonPrevNextWidth = 50;
        int buttonResolutionWidth = 300;
        int posXPrev = clientContext.getCurrentResolution().getWidth() / 2 - (buttonResolutionWidth / 2) - buttonPrevNextWidth;
        int posXRes = clientContext.getCurrentResolution().getWidth() / 2 - (buttonResolutionWidth / 2);
        int posXNext = clientContext.getCurrentResolution().getWidth() / 2 + (buttonResolutionWidth / 2);

        int posY = heightStep * 2;
        int buttonHeight = 50;
        resolutionRatio = clientContext.getCurrentResolution().getResolutionRatio();

        prevButton = new Button(posXPrev, posY, buttonPrevNextWidth, buttonHeight, "<") {
            @Override
            public void onClick() {
                if (index > 0) {
                    index--;
                    updateFields();
                }
            }
        };

        resolution = new TextZone(posXRes, posY, buttonResolutionWidth, buttonHeight, "");

        nextButton = new Button(posXNext, posY, buttonPrevNextWidth, buttonHeight, ">") {
            @Override
            public void onClick() {
                switch (resolutionRatio) {
                    case RATIO_4_3:
                        if (index < clientContext.getResolutions43().size() - 1) {
                            index++;
                            updateFields();
                        }
                        break;
                    case RATIO_16_9:
                        if (index < clientContext.getResolutions169().size() - 1) {
                            index++;
                            updateFields();
                        }
                        break;
                }
            }
        };

        int buttonWidth = 100;
        int posXSwitchRatio = clientContext.getCurrentResolution().getWidth() / 2 - (buttonWidth / 2);
        int posYSwitchRatio = heightStep * 3;

        if (clientContext.getResolutions43().size() != 0 && clientContext.getResolutions169().size() != 0) {
            switchRatioButton = new
                    Button(posXSwitchRatio, posYSwitchRatio, buttonWidth, buttonHeight, "") {
                        @Override
                        public void onClick() {
                            switch (resolutionRatio) {
                                case RATIO_4_3:
                                    resolutionRatio = ResolutionRatio.RATIO_16_9;
                                    index = 0;
                                    updateFields();
                                    break;
                                case RATIO_16_9:
                                    resolutionRatio = ResolutionRatio.RATIO_4_3;
                                    index = 0;
                                    updateFields();
                                    break;
                            }
                        }
                    };
        }

        int posXServerIP = clientContext.getCurrentResolution().getWidth() / 2 - (buttonWidth / 2);
        int posYServerIP = heightStep * 4;

        serverIP = new TextField(posXServerIP, posYServerIP, buttonWidth, buttonHeight, 14);
        serverIP.setLabel(clientContext.getServerIP());

        int posXServerPort = clientContext.getCurrentResolution().getWidth() / 2 - (buttonWidth / 2);
        int posYServerPort = heightStep * 5;

        serverPort = new TextField(posXServerPort, posYServerPort, buttonWidth, buttonHeight, 6);
        serverPort.setLabel(String.valueOf(clientContext.getServerPort()));

        int posXApply = clientContext.getCurrentResolution().getWidth() / 2 - (3 * buttonWidth / 2);
        int posYApply = heightStep * 8;
        applyButton = new Button(posXApply, posYApply, buttonWidth, buttonHeight, "APPLY") {
            @Override
            public void onClick() {
                actionApply();
            }
        };

        int posXBackButton = clientContext.getCurrentResolution().getWidth() / 2 + (buttonWidth / 2);
        int posYBackButton = heightStep * 8;
        backButton = new Button(posXBackButton, posYBackButton, buttonWidth, buttonHeight, "BACK") {
            @Override
            public void onClick() {
                client.setCurrentScene(lastScene);
            }
        };
        updateFields();
    }

    private void actionApply() {
        switch (resolutionRatio) {
            case RATIO_4_3:
                clientContext.setCurrentResolution(clientContext.getResolutions43().get(index));
                client.updateDisplay();
                break;
            case RATIO_16_9:
                clientContext.setCurrentResolution(clientContext.getResolutions169().get(index));
                client.updateDisplay();
                break;
        }

        if (!serverPort.getLabel().equals(String.valueOf(clientContext.getServerPort())) || (!serverIP.getLabel().equals(String.valueOf(clientContext.getServerIP())))) {
            clientContext.setServer(serverIP.getLabel(), Integer.valueOf(serverPort.getLabel()));
            setLastScene(loginScene);
        }

        updateFields();
    }


    private void updateFields() {
        String text = "";
        switch (resolutionRatio) {
            case RATIO_4_3:
                text = new StringBuilder().append(clientContext.getResolutions43().get(index).getWidth())
                        .append("x")
                        .append(clientContext.getResolutions43().get(index).getHeight())
                        .toString();
                if (clientContext.getResolutions43().size() != 0 && clientContext.getResolutions169().size() != 0) {
                    switchRatioButton.setLabel("4:3");
                }
                break;
            case RATIO_16_9:
                text = new StringBuilder().append(clientContext.getResolutions169().get(index).getWidth())
                        .append("x")
                        .append(clientContext.getResolutions169().get(index).getHeight())
                        .toString();
                if (clientContext.getResolutions43().size() != 0 && clientContext.getResolutions169().size() != 0) {
                    switchRatioButton.setLabel("16:9");
                }
                break;
        }
        resolution.setLabel(text);
        serverIP.setLabel(clientContext.getServerIP());
        serverPort.setLabel(String.valueOf(clientContext.getServerPort()));
    }

    @Override
    public void update(long deltaTime) {

    }

    @Override
    public void render() {
        GraphicsManager.getInstance().make2D();
        background.draw();
        prevButton.draw();
        nextButton.draw();
        resolution.draw();
        serverIP.draw();
        serverPort.draw();
        if (null != switchRatioButton) {
            switchRatioButton.draw();
        }
        //switchFullscreenButton.draw();
        applyButton.draw();
        backButton.draw();
        frame.draw();
    }

    @Override
    public void manageInput() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (InputManager.getInstance().getInput(Keyboard.getEventKey()) != null) {
                    if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.CANCEL)) {
                        if (serverIP.hasFocus()) {
                            serverIP.deleteLastChar();
                        } else if (serverPort.hasFocus()) {
                            serverPort.deleteLastChar();
                        }
                    } else {
                        if (serverIP.hasFocus()) {
                            serverIP.writeChar(Keyboard.getEventCharacter());
                        } else if (serverPort.hasFocus()) {
                            serverPort.writeChar(Keyboard.getEventCharacter());
                        }
                    }
                } else {
                    if (serverIP.hasFocus()) {
                        serverIP.writeChar(Keyboard.getEventCharacter());
                    } else if (serverPort.hasFocus()) {
                        serverPort.writeChar(Keyboard.getEventCharacter());
                    }
                }
            }
        }

        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                if (nextButton.isClicked()) {
                    nextButton.onClick();
                } else if (prevButton.isClicked()) {
                    prevButton.onClick();
                } else if (applyButton.isClicked()) {
                    applyButton.onClick();
                } else if (backButton.isClicked()) {
                    backButton.onClick();
                } else if (serverIP.isClicked()) {
                    serverIP.setHasFocus(true);
                    serverPort.setHasFocus(false);
                } else if (serverPort.isClicked()) {
                    serverIP.setHasFocus(false);
                    serverPort.setHasFocus(true);
                } else if (null != switchRatioButton && switchRatioButton.isClicked()) {
                    switchRatioButton.onClick();
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
        if (frameClicked && !clientContext.isFullscreen()) {
            Display.setLocation(Display.getX() + (Mouse.getX()) - x, (Display.getY() + (Display.getHeight() - Mouse.getY())) - y);
        }
    }


    @Override
    public void receiveMessage() {

    }
}
