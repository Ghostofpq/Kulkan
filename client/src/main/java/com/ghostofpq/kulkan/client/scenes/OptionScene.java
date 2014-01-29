package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.ClientContext;
import com.ghostofpq.kulkan.client.graphics.HUD.Button;
import com.ghostofpq.kulkan.client.graphics.HUD.TextZone;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.client.utils.ResolutionRatio;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.input.Mouse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Slf4j
public class OptionScene implements Scene {
    @Autowired
    private ClientContext clientContext;
    @Autowired
    private Client client;
    @Autowired
    private LoginScene loginScene;
    private TextZone resolution;
    private Button nextButton;
    private Button prevButton;
    private Button switchRatioButton;
    private Button applyButton;
    private Button backButton;
    private int index;
    private ResolutionRatio resolutionRatio;

    public OptionScene() {
    }

    @Override
    public void init() {
        index = 0;
        int widthStep = (clientContext.getCurrentResolution().getWidth() / 10);
        int heightStep = (clientContext.getCurrentResolution().getHeight() / 6);
        int posXPrev = widthStep * 2;
        int posXRes = widthStep * 3;
        int posXNext = widthStep * 7;

        int posY = heightStep;


        resolutionRatio = clientContext.getCurrentResolution().getResolutionRatio();

        prevButton = new Button(posXPrev, posY, widthStep, heightStep, "<") {
            @Override
            public void onClick() {
                if (index > 0) {
                    index--;
                    updateFields();
                }
            }
        };

        resolution = new TextZone(posXRes, posY, widthStep * 4, heightStep, "");

        nextButton = new

                Button(posXNext, posY, widthStep, heightStep, ">") {
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


        int posXSwitch = widthStep * 4;
        int posYSwitch = heightStep * 3;
        if (clientContext.getResolutions43().size() != 0 && clientContext.getResolutions169().size() != 0) {
            switchRatioButton = new
                    Button(posXSwitch, posYSwitch, widthStep * 2, heightStep, "") {
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


        int posXApply = widthStep * 2;
        int posYApply = heightStep * 5;
        applyButton = new

                Button(posXApply, posYApply, widthStep * 2, heightStep, "APPLY") {
                    @Override
                    public void onClick() {
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
                        updateFields();
                    }
                };
        int posXQuit = widthStep * 7;
        int posYQuit = heightStep * 5;
        backButton = new

                Button(posXQuit, posYQuit, widthStep * 2, heightStep, "BACK") {
                    @Override
                    public void onClick() {
                        client.setCurrentScene(loginScene);
                    }
                };
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
                    switchRatioButton.setLabel(ResolutionRatio.RATIO_16_9.toString());
                }
                break;
            case RATIO_16_9:
                text = new StringBuilder().append(clientContext.getResolutions169().get(index).getWidth())
                        .append("x")
                        .append(clientContext.getResolutions169().get(index).getHeight())
                        .toString();
                if (clientContext.getResolutions43().size() != 0 && clientContext.getResolutions169().size() != 0) {
                    switchRatioButton.setLabel(ResolutionRatio.RATIO_4_3.toString());
                }
                break;
        }
        resolution.setText(text);
    }

    @Override
    public void initConnections() throws IOException {

    }

    @Override
    public void update(long deltaTime) {

    }

    @Override
    public void render() {
        GraphicsManager.getInstance().make2D();
        prevButton.draw();
        nextButton.draw();
        resolution.draw();
        if (null != switchRatioButton) {
            switchRatioButton.draw();
        }
        applyButton.draw();
        backButton.draw();
    }

    @Override
    public void manageInput() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                if (nextButton.isClicked()) {
                    nextButton.onClick();
                }
                if (prevButton.isClicked()) {
                    prevButton.onClick();
                }
                if (applyButton.isClicked()) {
                    applyButton.onClick();
                }
                if (backButton.isClicked()) {
                    backButton.onClick();
                }
                if (null != switchRatioButton) {
                    if (switchRatioButton.isClicked()) {
                        switchRatioButton.onClick();
                    }
                }
            }
        }
    }

    @Override
    public void closeConnections() throws IOException {

    }

    @Override
    public void receiveMessage() {

    }
}
