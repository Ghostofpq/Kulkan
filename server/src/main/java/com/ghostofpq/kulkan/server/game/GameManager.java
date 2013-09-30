package com.ghostofpq.kulkan.server.game;

public class GameManager {
    private static volatile GameManager instance = new GameManager();

    private GameManager() {
    }

    public static GameManager getInstance() {
        if (instance == null) {
            synchronized (GameManager.class) {
                if (instance == null) {
                    instance = new GameManager();
                }
            }
        }
        return instance;
    }


}
