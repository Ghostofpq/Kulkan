package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.ClientContext;
import com.ghostofpq.kulkan.client.ClientMessenger;
import com.ghostofpq.kulkan.client.graphics.Background;
import com.ghostofpq.kulkan.client.graphics.HUD.Button;
import com.ghostofpq.kulkan.client.graphics.HUD.Frame;
import com.ghostofpq.kulkan.client.graphics.HUD.PopUp;
import com.ghostofpq.kulkan.client.graphics.KeyValueRender;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.client.utils.TextureKey;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.user.MessageError;
import com.ghostofpq.kulkan.entities.messages.user.MessagePlayerUpdate;
import com.ghostofpq.kulkan.entities.messages.user.MessagePutGameCharacterFromStockToTeam;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class StockScene implements Scene {
    private KeyValueRender bloodMoney;
    private List<Button> stockSlots;

    private Button backButton;

    private int widthSeparator;
    private int heightSeparator;
    @Autowired
    private Client client;
    @Autowired
    private ClientContext clientContext;
    @Autowired
    private ClientMessenger clientMessenger;
    @Autowired
    private LobbyScene lobbyScene;
    @Autowired
    private ManageGameCharacterScene manageGameCharacterScene;
    // BACKGROUND
    private Background background;
    // POPUP
    private PopUp popUp;
    // FRAME
    private Frame frame;
    private int x;
    private int y;
    private boolean frameClicked;

    public StockScene() {
    }


    @Override
    public void init() {
        background = new Background(TextureKey.BACKGROUND_BASIC);
        frame = new Frame(0, 0, clientContext.getCurrentResolution().getWidth(), clientContext.getCurrentResolution().getHeight(), clientContext.getCurrentResolution().getWidth() / 64, clientContext.getCurrentResolution().getWidth() / 64, TextureKey.COMMON_EXT_FRAME);

        int widthStep = clientContext.getCurrentResolution().getWidth() / 10;
        int heightStep = clientContext.getCurrentResolution().getHeight() / 10;
        stockSlots = new ArrayList<Button>();
        int numberOfCharPerLine = 5;

        int stockCharacterWidth = clientContext.getCurrentResolution().getWidth() / 10;
        int stockCharacterHeight = clientContext.getCurrentResolution().getHeight() / 3;
        int startOfLinePoxX = clientContext.getCurrentResolution().getWidth() / 10;
        int startOfLinePoxY = clientContext.getCurrentResolution().getHeight() / 12;
        int paddingX = (clientContext.getCurrentResolution().getWidth() - numberOfCharPerLine * stockCharacterWidth - 2 * startOfLinePoxX) / numberOfCharPerLine;
        int paddingY = clientContext.getCurrentResolution().getHeight() / 12;

        for (int i = 0; i < 10; i++) {
            int posX = (i % numberOfCharPerLine) * (paddingX + stockCharacterWidth) + startOfLinePoxX;
            int posY;
            if (i < numberOfCharPerLine) {
                posY = startOfLinePoxY;
            } else {
                posY = startOfLinePoxY + paddingY + stockCharacterHeight;
            }
            if (i < clientContext.getPlayer().getNumberOfStockSlots()) {
                if (clientContext.getPlayer().getStock().size() > i && null != clientContext.getPlayer().getStock().get(i)) {
                    GameCharacter gameCharacter = clientContext.getPlayer().getStock().get(i);
                    String name = gameCharacter.getName();
                    TextureKey textureKey = TextureKey.COMMON_CHAR_SHADOW;
                    final ObjectId id = gameCharacter.getId();
                    Button stockCharacterButton = new Button(posX, posY, stockCharacterWidth, stockCharacterHeight, name, textureKey, textureKey) {
                        @Override
                        public void onClick() {
                            //  clientContext.setSelectedCharacterId(id);
                            //  client.setCurrentScene(manageGameCharacterScene);
                            actionReactivateCharacter(id);
                        }
                    };
                    stockSlots.add(stockCharacterButton);
                } else {
                    Button emptySlotButton = new Button(posX, posY, stockCharacterWidth, stockCharacterHeight, "EMPTY", null, null) {
                        @Override
                        public void onClick() {

                        }
                    };
                    stockSlots.add(emptySlotButton);
                }
            } else {
                Button unlockSlotButton = new Button(posX, posY, stockCharacterWidth, stockCharacterHeight, "UNLOCK", null, null) {
                    @Override
                    public void onClick() {

                    }
                };
                stockSlots.add(unlockSlotButton);
            }
        }
        int buttonWidth = 100;
        int buttonHeight = 50;
        int posXBackButton = clientContext.getCurrentResolution().getWidth() / 2 - (buttonWidth / 2);
        int posYBackButton = clientContext.getCurrentResolution().getHeight() - (buttonHeight * 2);
        backButton = new Button(posXBackButton, posYBackButton, buttonWidth, buttonHeight, "BACK") {
            @Override
            public void onClick() {
                client.setCurrentScene(lobbyScene);
            }
        };
    }

    private void actionReactivateCharacter(ObjectId gameCharacterId) {
        MessagePutGameCharacterFromStockToTeam messagePutGameCharacterFromStockToTeam = new MessagePutGameCharacterFromStockToTeam(clientContext.getTokenKey(), clientContext.getPlayer().getPseudo(), gameCharacterId);
        clientMessenger.sendMessageToUserService(messagePutGameCharacterFromStockToTeam);
    }


    @Override
    public void update(long deltaTime) {
    }

    @Override
    public void render() {
        GraphicsManager.getInstance().make2D();
        background.draw();
        for (Button button : stockSlots) {
            button.draw();
        }
        backButton.draw();
        if (null != popUp) {
            popUp.draw();
        }
        frame.draw();
    }

    @Override
    public void manageInput() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                if (null == popUp) {
                    for (Button button : stockSlots) {
                        if (button.isClicked()) {
                            button.onClick();
                        }
                    }
                    if (backButton.isClicked()) {
                        backButton.onClick();
                    }
                } else if (popUp.isClicked()) {
                    String onClick = popUp.onClick();
                    if (null != onClick) {
                        if (onClick.equals("OK")) {
                            popUp = null;
                        }
                    }
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
        Message message = clientMessenger.receiveMessage();
        if (null != message) {
            switch (message.getType()) {
                case PLAYER_UPDATE:
                    log.debug("PLAYER_UPDATE");
                    MessagePlayerUpdate response = (MessagePlayerUpdate) message;
                    clientContext.setPlayer(response.getPlayer());
                    init();
                    break;
                case ERROR:
                    List<String> options = new ArrayList<String>();
                    options.add("OK");
                    MessageError messageError = (MessageError) message;
                    popUp = new PopUp(options, messageError.getError());
                    break;
            }
        }
    }
}
