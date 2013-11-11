package com.ghostofpq.kulkan.client.scenes;


public class NewGameCharacterScene implements Scene {
    private static volatile NewGameCharacterScene instance = null;

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
}
