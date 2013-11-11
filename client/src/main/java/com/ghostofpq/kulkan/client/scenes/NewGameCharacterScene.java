package com.ghostofpq.kulkan.client.scenes;


import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.Button;
import com.ghostofpq.kulkan.client.graphics.TextField;
import com.ghostofpq.kulkan.client.utils.InputManager;
import com.ghostofpq.kulkan.client.utils.InputMap;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class NewGameCharacterScene implements Scene {
    private static volatile NewGameCharacterScene instance = null;
    private TextField pseudo;
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
        monkey = new

                Button(monkeyPosX, monkeyPosY, widthStepClan, heightStep, "monkey") {
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


        maleHasFocus();
        gorillaHasFocus();
    }

    public void maleHasFocus() {
        male.setHasFocus(true);
        female.setHasFocus(false);
    }

    public void femaleHasFocus() {
        male.setHasFocus(false);
        female.setHasFocus(true);
    }

    public void gorillaHasFocus() {
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
            }
        }
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (InputManager.getInstance().getInput(Keyboard.getEventKey()) != null) {
                    if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.CANCEL)) {
                        pseudo.deleteLastChar();
                    } else {
                        pseudo.writeChar(Keyboard.getEventCharacter());
                    }
                } else {
                    pseudo.writeChar(Keyboard.getEventCharacter());
                }
            }
        }
    }

    @Override
    public void closeConnections() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void receiveMessage() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
