package com.ghostofpq.kulkan.client.scenes;


import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.Button;
import com.ghostofpq.kulkan.client.graphics.KeyValueRender;
import com.ghostofpq.kulkan.client.graphics.PrimaryCharacteristicsRender;
import com.ghostofpq.kulkan.client.graphics.SecondaryCharacteristicsRender;
import com.ghostofpq.kulkan.entities.character.GameCharacter;

public class GameCharacterManageScene implements Scene {
    private static volatile GameCharacterManageScene instance = null;
    private GameCharacter gameCharacter;
    private Button manageJobButton;
    private Button manageEquipementButton;
    private Button quitButton;
    private Button deleteGameCharButton;
    private int widthSeparator = 50;
    private int widthStep;
    private int widthStepClan;
    private int heightSeparator = 50;
    private int heightStep;
    private PrimaryCharacteristicsRender primaryCharacteristicsRender;
    private SecondaryCharacteristicsRender secondaryCharacteristicsRender;
    private KeyValueRender hpRender;
    private KeyValueRender mpRender;
    private KeyValueRender xpRender;

    private GameCharacterManageScene() {
    }

    public static GameCharacterManageScene getInstance() {
        if (instance == null) {
            synchronized (GameCharacterManageScene.class) {
                if (instance == null) {
                    instance = new GameCharacterManageScene();
                }
            }
        }
        return instance;
    }

    @Override
    public void init() {
        widthSeparator = Client.getInstance().getWidth() / 20;
        heightSeparator = Client.getInstance().getHeight() / 20;

        widthStep = (Client.getInstance().getWidth() - 3 * widthSeparator) / 4;
        heightStep = (Client.getInstance().getHeight() - 4 * heightSeparator) / 8;

        primaryCharacteristicsRender = new PrimaryCharacteristicsRender(widthSeparator, heightSeparator, widthStep * 2, heightStep * 3, gameCharacter.getAggregatedCharacteristics());
        secondaryCharacteristicsRender = new SecondaryCharacteristicsRender(2 * widthSeparator + widthStep * 2, heightSeparator, widthStep * 2, heightStep * 5, gameCharacter.getAggregatedSecondaryCharacteristics());

        hpRender = new KeyValueRender(widthSeparator, heightSeparator + heightStep * 3, widthStep, heightStep, "HP", String.valueOf(gameCharacter.getMaxHealthPoint()), 5);
        mpRender = new KeyValueRender(widthSeparator, heightSeparator + heightStep * 4, widthStep, heightStep, "MP", String.valueOf(gameCharacter.getMaxManaPoint()), 5);
        xpRender = new KeyValueRender(widthSeparator, heightSeparator + heightStep * 5, widthStep, heightStep, "XP", String.valueOf(gameCharacter.getExperience()), 5);
    }

    @Override
    public void update(long deltaTime) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void render() {
        primaryCharacteristicsRender.draw();
        secondaryCharacteristicsRender.draw();
        hpRender.draw();
        mpRender.draw();
        xpRender.draw();
    }

    @Override
    public void manageInput() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void closeConnections() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void receiveMessage() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public GameCharacter getGameCharacter() {
        return gameCharacter;
    }

    public void setGameCharacter(GameCharacter gameCharacter) {
        this.gameCharacter = gameCharacter;
    }
}
