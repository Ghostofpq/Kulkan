package com.ghostofpq.kulkan.server.game;

import com.ghostofpq.kulkan.entities.battlefield.Battlefield;
import com.ghostofpq.kulkan.entities.character.Player;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class GameManager {
    private static volatile GameManager instance = new GameManager();
    private Map<String, Game> gameMap;

    private GameManager() {
        gameMap = new HashMap<String, Game>();
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

    public void addGame(String gameId, Battlefield battlefield, List<Player> playerList) {
        log.debug(" [-] ADDING GAME {} IN THE GAME MANAGER", gameId);
        gameMap.put(gameId, new Game(battlefield, playerList, gameId));
    }

    public void getGame(String gameId) {
        gameMap.get(gameId);
    }

    public void run() {
        for (Game game : gameMap.values()) {
            game.receiveMessage();
        }
    }
}
