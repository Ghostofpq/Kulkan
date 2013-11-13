package com.ghostofpq.kulkan.client.scenes;


import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.Button;
import com.ghostofpq.kulkan.client.graphics.TextField;
import com.ghostofpq.kulkan.client.utils.InputManager;
import com.ghostofpq.kulkan.client.utils.InputMap;
import com.ghostofpq.kulkan.entities.character.Gender;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.clan.ClanType;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;
import com.ghostofpq.kulkan.entities.messages.auth.MessageCreateNewGameCharacter;
import com.ghostofpq.kulkan.entities.messages.auth.MessageCreateNewGameCharacterResponse;
import com.ghostofpq.kulkan.entities.messages.auth.MessageErrorCode;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

@Slf4j
public class NewGameCharacterScene implements Scene {
    private static volatile NewGameCharacterScene instance = null;
    private final String AUTHENTICATION_QUEUE_NAME = "authentication";
    private String authenticationReplyQueueName;
    private Channel channelAuthenticating;
    private QueueingConsumer consumer;
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
                        try {
                            Player player = Client.getInstance().getPlayer();
                            MessageCreateNewGameCharacter messageCreateNewGameCharacter = new MessageCreateNewGameCharacter(Client.getInstance().getTokenKey(), player.getPseudo(), clanType, gender, name.getContent());
                            Message result = requestServer(messageCreateNewGameCharacter);
                            if (null != result) {
                                if (result.getType().equals(MessageType.CREATE_NEW_GAME_CHARACTER_RESPONSE)) {
                                    MessageCreateNewGameCharacterResponse response = (MessageCreateNewGameCharacterResponse) result;
                                    if (response.getMessageErrorCode().equals(MessageErrorCode.OK)) {
                                        closeConnections();
                                        Client.getInstance().setPlayer(response.getPlayer());
                                        Client.getInstance().setCurrentScene(TeamManagementScene.getInstance());
                                    } else {
                                        log.debug("CREATE OK");
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
        //To change body of implemented methods use File | Settings | File Templates.
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
                if (male.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    male.onClick();
                }
                if (female.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    female.onClick();
                }
                if (gorilla.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    gorilla.onClick();
                }
                if (jaguar.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    jaguar.onClick();
                }
                if (turtle.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    turtle.onClick();
                }
                if (monkey.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    monkey.onClick();
                }
                if (panther.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    panther.onClick();
                }
                if (lizard.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    lizard.onClick();
                }
                if (ara.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    ara.onClick();
                }
                if (eagle.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    eagle.onClick();
                }
                if (validate.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    validate.onClick();
                }
                if (quit.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
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

    public Message requestServer(Message message) throws Exception {
        log.debug("create account");
        Message response = null;
        String corrId = java.util.UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(authenticationReplyQueueName)
                .build();
        channelAuthenticating.basicPublish("", AUTHENTICATION_QUEUE_NAME, props, message.getBytes());
        log.debug(" [x] Sent '{}'", message.getType());
        QueueingConsumer.Delivery delivery = consumer.nextDelivery(1000);
        if (null != delivery) {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response = Message.loadFromBytes(delivery.getBody());
                log.debug(" [x] Received '{}'", response.getType());
            }
        }
        return response;
    }

    private void initConnection() throws IOException {
        channelAuthenticating = Client.getInstance().getConnection().createChannel();
        authenticationReplyQueueName = channelAuthenticating.queueDeclare().getQueue();
        consumer = new QueueingConsumer(channelAuthenticating);
        channelAuthenticating.basicConsume(authenticationReplyQueueName, true, consumer);
    }

    @Override
    public void closeConnections() {
        try {
            channelAuthenticating.close();
            log.debug("channelAuthenticating closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveMessage() {
    }
}
