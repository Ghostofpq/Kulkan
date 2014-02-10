package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.ClientContext;
import com.ghostofpq.kulkan.client.ClientMessenger;
import com.ghostofpq.kulkan.client.graphics.Background;
import com.ghostofpq.kulkan.client.graphics.HUD.Button;
import com.ghostofpq.kulkan.client.graphics.HUD.Frame;
import com.ghostofpq.kulkan.client.graphics.HUD.TextField;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.client.utils.InputManager;
import com.ghostofpq.kulkan.client.utils.InputMap;
import com.ghostofpq.kulkan.client.utils.TextureKey;
import com.ghostofpq.kulkan.entities.character.Gender;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.clan.ClanType;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.user.MessageCreateNewGameCharacter;
import com.ghostofpq.kulkan.entities.messages.user.MessagePlayerUpdate;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class CreateGameCharacterScene implements Scene {
    private static final Logger LOG = LoggerFactory.getLogger(CreateGameCharacterScene.class);
    private ClanType clanType;
    private Gender gender;
    private TextField name;
    private Button male;
    private Button female;
    private Button gorilla;
    private Button jaguar;
    private Button turtle;
    private Button monkey;
    private Button panther;
    private Button lizard;
    private Button ara;
    private Button eagle;
    private Button validate;
    private Button quit;
    private int widthSeparator = 50;
    private int widthStep;
    private int widthStepClan;
    private int heightSeparator = 50;
    private int heightStep;
    // BACKGROUND
    private Background background;
    // FRAME
    private Frame frame;
    private int x;
    private int y;
    private boolean frameClicked;
    @Autowired
    private Client client;
    @Autowired
    private ClientContext clientContext;
    @Autowired
    private ClientMessenger clientMessenger;
    @Autowired
    private OptionScene optionScene;
    @Autowired
    private LobbyScene lobbyScene;

    public CreateGameCharacterScene() {
    }

    @Override
    public void init() {
        background = new Background(TextureKey.BACKGROUND_BASIC);
        frame = new Frame(0, 0, clientContext.getCurrentResolution().getWidth(), clientContext.getCurrentResolution().getHeight(), clientContext.getCurrentResolution().getWidth() / 64, clientContext.getCurrentResolution().getWidth() / 64, TextureKey.COMMON_EXT_FRAME);

        widthSeparator = Client.getInstance().getWidth() / 20;
        heightSeparator = Client.getInstance().getHeight() / 20;

        widthStep = (Client.getInstance().getWidth() - 3 * widthSeparator) / 5;
        widthStepClan = (Client.getInstance().getWidth() - 2 * widthSeparator) / 8;

        heightStep = (Client.getInstance().getHeight() - 4 * heightSeparator) / 8;

        int malePosX = widthSeparator;
        int malePosY = heightSeparator;

        int femalePosX = malePosX + widthStep / 2;
        int femalePosY = heightSeparator;

        male = new Button(malePosX, malePosY, widthStep / 2, heightStep, "MALE") {
            @Override
            public void onClick() {
                maleHasFocus();
            }
        };
        female = new

                Button(femalePosX, femalePosY, widthStep / 2, heightStep, "FEMALE") {
                    @Override
                    public void onClick() {
                        femaleHasFocus();
                    }
                };
        int namePosX = 2 * widthSeparator + widthStep;
        int namePosY = heightSeparator;

        name = new TextField(namePosX, namePosY, 3 * widthStep, heightStep, 15);

        int gorillaPosX = widthSeparator;
        int gorillaPosY = 2 * heightSeparator + heightStep;

        int turtlePosX = gorillaPosX + widthStepClan;
        int turtlePosY = gorillaPosY;

        int jaguarPosX = turtlePosX + widthStepClan;
        int jaguarPosY = gorillaPosY;

        int monkeyPosX = jaguarPosX + widthStepClan;
        int monkeyPosY = gorillaPosY;

        int pantherPosX = monkeyPosX + widthStepClan;
        int pantherPosY = gorillaPosY;

        int lizardPosX = pantherPosX + widthStepClan;
        int lizardPosY = gorillaPosY;

        int araPosX = lizardPosX + widthStepClan;
        int araPosY = gorillaPosY;

        int eaglePosX = araPosX + widthStepClan;
        int eaglePosY = gorillaPosY;

        gorilla = new Button(gorillaPosX, gorillaPosY, widthStepClan, heightStep, "gorilla") {
            @Override
            public void onClick() {
                gorillaHasFocus();
            }
        };

        jaguar = new Button(jaguarPosX, jaguarPosY, widthStepClan, heightStep, "jaguar") {
            @Override
            public void onClick() {
                jaguarHasFocus();
            }
        };

        turtle = new Button(turtlePosX, turtlePosY, widthStepClan, heightStep, "turtle") {
            @Override
            public void onClick() {
                turtleHasFocus();
            }
        };
        monkey = new Button(monkeyPosX, monkeyPosY, widthStepClan, heightStep, "monkey") {
            @Override
            public void onClick() {
                monkeyHasFocus();
            }
        };

        panther = new Button(pantherPosX, pantherPosY, widthStepClan, heightStep, "panther") {
            @Override
            public void onClick() {
                pantherHasFocus();
            }
        };

        lizard = new Button(lizardPosX, lizardPosY, widthStepClan, heightStep, "lizard") {
            @Override
            public void onClick() {
                lizardHasFocus();
            }
        };

        ara = new Button(araPosX, araPosY, widthStepClan, heightStep, "ara") {
            @Override
            public void onClick() {
                araHasFocus();
            }
        };

        eagle = new Button(eaglePosX, eaglePosY, widthStepClan, heightStep, "eagle") {
            @Override
            public void onClick() {
                eagleHasFocus();
            }
        };

        int widthDesc = (Client.getInstance().getWidth() - 4 * widthSeparator) / 7;

        int validatePosX = 6 * widthDesc + 3 * widthSeparator;
        int validatePosY = 3 * heightSeparator + 2 * heightStep;

        int quitPosX = validatePosX;
        int quitPosY = 3 * heightSeparator + 4 * heightStep;

        validate = new Button(validatePosX, validatePosY, widthDesc, heightStep, "validate") {
            @Override
            public void onClick() {
                LOG.debug("Sending a CreateGameCharacterRequest");
                LOG.debug("Name : '{}'", name);
                LOG.debug("Gender : '{}'", gender);
                LOG.debug("ClanType : '{}'", clanType);
                LOG.debug("Sending ");
                Player player = Client.getInstance().getPlayer();
                MessageCreateNewGameCharacter messageCreateNewGameCharacter = new MessageCreateNewGameCharacter(Client.getInstance().getTokenKey(), player.getPseudo(), clanType, gender, name.getLabel());
                clientMessenger.sendMessageToUserService(messageCreateNewGameCharacter);
            }
        };

        quit = new Button(quitPosX, quitPosY, widthDesc, heightStep, "back") {
            @Override
            public void onClick() {
                client.setCurrentScene(lobbyScene);
            }
        };

        maleHasFocus();
        gorillaHasFocus();
    }

    public void maleHasFocus() {
        gender = Gender.MALE;
        male.setHasFocus(true);
        female.setHasFocus(false);
    }

    public void femaleHasFocus() {
        gender = Gender.FEMALE;
        male.setHasFocus(false);
        female.setHasFocus(true);
    }

    public void gorillaHasFocus() {
        clanType = ClanType.GORILLA;
        gorilla.setHasFocus(true);
        jaguar.setHasFocus(false);
        turtle.setHasFocus(false);
        monkey.setHasFocus(false);
        panther.setHasFocus(false);
        lizard.setHasFocus(false);
        ara.setHasFocus(false);
        eagle.setHasFocus(false);
    }

    public void jaguarHasFocus() {
        clanType = ClanType.JAGUAR;
        gorilla.setHasFocus(false);
        jaguar.setHasFocus(true);
        turtle.setHasFocus(false);
        monkey.setHasFocus(false);
        panther.setHasFocus(false);
        lizard.setHasFocus(false);
        ara.setHasFocus(false);
        eagle.setHasFocus(false);
    }

    public void turtleHasFocus() {
        clanType = ClanType.TURTLE;
        gorilla.setHasFocus(false);
        jaguar.setHasFocus(false);
        turtle.setHasFocus(true);
        monkey.setHasFocus(false);
        panther.setHasFocus(false);
        lizard.setHasFocus(false);
        ara.setHasFocus(false);
        eagle.setHasFocus(false);
    }

    public void monkeyHasFocus() {
        clanType = ClanType.MONKEY;
        gorilla.setHasFocus(false);
        jaguar.setHasFocus(false);
        turtle.setHasFocus(false);
        monkey.setHasFocus(true);
        panther.setHasFocus(false);
        lizard.setHasFocus(false);
        ara.setHasFocus(false);
        eagle.setHasFocus(false);
    }

    public void pantherHasFocus() {
        clanType = ClanType.PANTHER;
        gorilla.setHasFocus(false);
        jaguar.setHasFocus(false);
        turtle.setHasFocus(false);
        monkey.setHasFocus(false);
        panther.setHasFocus(true);
        lizard.setHasFocus(false);
        ara.setHasFocus(false);
        eagle.setHasFocus(false);
    }

    public void lizardHasFocus() {
        clanType = ClanType.LIZARD;
        gorilla.setHasFocus(false);
        jaguar.setHasFocus(false);
        turtle.setHasFocus(false);
        monkey.setHasFocus(false);
        panther.setHasFocus(false);
        lizard.setHasFocus(true);
        ara.setHasFocus(false);
        eagle.setHasFocus(false);
    }

    public void araHasFocus() {
        clanType = ClanType.ARA;
        gorilla.setHasFocus(false);
        jaguar.setHasFocus(false);
        turtle.setHasFocus(false);
        monkey.setHasFocus(false);
        panther.setHasFocus(false);
        lizard.setHasFocus(false);
        ara.setHasFocus(true);
        eagle.setHasFocus(false);
    }

    public void eagleHasFocus() {
        clanType = ClanType.EAGLE;
        gorilla.setHasFocus(false);
        jaguar.setHasFocus(false);
        turtle.setHasFocus(false);
        monkey.setHasFocus(false);
        panther.setHasFocus(false);
        lizard.setHasFocus(false);
        ara.setHasFocus(false);
        eagle.setHasFocus(true);
    }

    @Override
    public void update(long deltaTime) {

    }

    @Override
    public void render() {
        GraphicsManager.getInstance().make2D();
        background.draw();
        male.draw();
        female.draw();
        gorilla.draw();
        jaguar.draw();
        turtle.draw();
        monkey.draw();
        panther.draw();
        lizard.draw();
        ara.draw();
        eagle.draw();
        name.draw();
        validate.draw();
        quit.draw();
        frame.draw();
    }

    @Override
    public void manageInput() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                if (male.isClicked()) {
                    male.onClick();
                } else if (female.isClicked()) {
                    female.onClick();
                } else if (gorilla.isClicked()) {
                    gorilla.onClick();
                } else if (jaguar.isClicked()) {
                    jaguar.onClick();
                } else if (turtle.isClicked()) {
                    turtle.onClick();
                } else if (monkey.isClicked()) {
                    monkey.onClick();
                } else if (panther.isClicked()) {
                    panther.onClick();
                } else if (lizard.isClicked()) {
                    lizard.onClick();
                } else if (ara.isClicked()) {
                    ara.onClick();
                } else if (eagle.isClicked()) {
                    eagle.onClick();
                } else if (validate.isClicked()) {
                    validate.onClick();
                } else if (quit.isClicked()) {
                    quit.onClick();
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

        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (InputManager.getInstance().getInput(Keyboard.getEventKey()) != null) {
                    if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.CANCEL)) {
                        name.deleteLastChar();
                    } else {
                        name.writeChar(Keyboard.getEventCharacter());
                    }
                } else {
                    name.writeChar(Keyboard.getEventCharacter());
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

    @Override
    public void receiveMessage() {
        Message message = Client.getInstance().receiveMessage();
        if (null != message) {
            switch (message.getType()) {
                case PLAYER_UPDATE:
                    LOG.debug("PLAYER_UPDATE");
                    MessagePlayerUpdate response = (MessagePlayerUpdate) message;
                    LOG.debug("CREATE OK");
                    clientContext.setPlayer(response.getPlayer());
                    client.setCurrentScene(lobbyScene);
                    break;
            }
        }
    }
}
