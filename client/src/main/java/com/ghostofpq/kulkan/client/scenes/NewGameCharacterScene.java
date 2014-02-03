package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.HUD.Button;
import com.ghostofpq.kulkan.client.graphics.HUD.TextField;
import com.ghostofpq.kulkan.client.utils.InputManager;
import com.ghostofpq.kulkan.client.utils.InputMap;
import com.ghostofpq.kulkan.entities.character.Gender;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.clan.ClanType;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.user.MessageCreateNewGameCharacter;
import com.ghostofpq.kulkan.entities.messages.user.MessagePlayerUpdate;
import com.rabbitmq.client.Channel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NewGameCharacterScene implements Scene {
    private static final Logger LOG = LoggerFactory.getLogger(NewGameCharacterScene.class);
    private static volatile NewGameCharacterScene instance = null;
    private final String USER_SERVICE_QUEUE_NAME = "server/users";
    private Channel channelOut;
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

    private NewGameCharacterScene() {
    }

    public static NewGameCharacterScene getInstance() {
        if (instance == null) {
            synchronized (NewGameCharacterScene.class) {
                if (instance == null) {
                    instance = new NewGameCharacterScene();
                }
            }
        }
        return instance;
    }

    @Override
    public void init() {
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

        gorilla = new

                Button(gorillaPosX, gorillaPosY, widthStepClan, heightStep, "gorilla") {
                    @Override
                    public void onClick() {
                        gorillaHasFocus();
                    }
                };

        jaguar = new

                Button(jaguarPosX, jaguarPosY, widthStepClan, heightStep, "jaguar") {
                    @Override
                    public void onClick() {
                        jaguarHasFocus();
                    }
                };

        turtle = new

                Button(turtlePosX, turtlePosY, widthStepClan, heightStep, "turtle") {
                    @Override
                    public void onClick() {
                        turtleHasFocus();
                    }
                };
        monkey = new

                Button(monkeyPosX, monkeyPosY, widthStepClan, heightStep, "monkey") {
                    @Override
                    public void onClick() {
                        monkeyHasFocus();
                    }
                };

        panther = new

                Button(pantherPosX, pantherPosY, widthStepClan, heightStep, "panther") {
                    @Override
                    public void onClick() {
                        pantherHasFocus();
                    }
                };

        lizard = new

                Button(lizardPosX, lizardPosY, widthStepClan, heightStep, "lizard") {
                    @Override
                    public void onClick() {
                        lizardHasFocus();
                    }
                };

        ara = new

                Button(araPosX, araPosY, widthStepClan, heightStep, "ara") {
                    @Override
                    public void onClick() {
                        araHasFocus();
                    }
                };

        eagle = new

                Button(eaglePosX, eaglePosY, widthStepClan, heightStep, "eagle") {
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

        validate = new

                Button(validatePosX, validatePosY, widthDesc, heightStep, "validate") {
                    @Override
                    public void onClick() {
                        LOG.debug("Sending a CreateGameCharacterRequest");
                        LOG.debug("Name : '{}'", name);
                        LOG.debug("Gender : '{}'", gender);
                        LOG.debug("ClanType : '{}'", clanType);
                        try {
                            LOG.debug("Sending ");
                            Player player = Client.getInstance().getPlayer();
                            MessageCreateNewGameCharacter messageCreateNewGameCharacter = new MessageCreateNewGameCharacter(Client.getInstance().getTokenKey(), player.getPseudo(), clanType, gender, name.getContent());
                            channelOut.basicPublish("", USER_SERVICE_QUEUE_NAME, null, messageCreateNewGameCharacter.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

        quit = new

                Button(quitPosX, quitPosY, widthDesc, heightStep, "back") {
                    @Override
                    public void onClick() {
                        Client.getInstance().setCurrentScene(TeamManagementScene.getInstance());
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
    }

    @Override
    public void manageInput() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                if (male.isClicked()) {
                    male.onClick();
                }
                if (female.isClicked()) {
                    female.onClick();
                }
                if (gorilla.isClicked()) {
                    gorilla.onClick();
                }
                if (jaguar.isClicked()) {
                    jaguar.onClick();
                }
                if (turtle.isClicked()) {
                    turtle.onClick();
                }
                if (monkey.isClicked()) {
                    monkey.onClick();
                }
                if (panther.isClicked()) {
                    panther.onClick();
                }
                if (lizard.isClicked()) {
                    lizard.onClick();
                }
                if (ara.isClicked()) {
                    ara.onClick();
                }
                if (eagle.isClicked()) {
                    eagle.onClick();
                }
                if (validate.isClicked()) {
                    validate.onClick();
                }
                if (quit.isClicked()) {
                    quit.onClick();
                }
            }
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
        channelOut = Client.getInstance().getConnection().createChannel();
        channelOut.queueDeclare(USER_SERVICE_QUEUE_NAME, false, false, false, null);
    }

    @Override
    public void closeConnections() throws IOException {
        channelOut.close();
        LOG.debug("channelOut closed");
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
                    Client.getInstance().setPlayer(response.getPlayer());
                    Client.getInstance().setCurrentScene(TeamManagementScene.getInstance());
                    break;
            }
        }
    }
}
