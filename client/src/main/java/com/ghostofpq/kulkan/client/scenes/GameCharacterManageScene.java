package com.ghostofpq.kulkan.client.scenes;


import com.ghostofpq.kulkan.entities.character.GameCharacter;

public class GameCharacterManageScene implements Scene {
    private static volatile GameCharacterManageScene instance = null;
    private GameCharacter gameCharacter;

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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void update(long deltaTime) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void render() {
        //To change body of implemented methods use File | Settings | File Templates.
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
