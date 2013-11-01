package com.ghostofpq.kulkan.server.game;

import com.ghostofpq.kulkan.entities.battlefield.Battlefield;
import com.ghostofpq.kulkan.entities.character.Player;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class GameManager {
    private Map<String, Game> gameMap;
    private List<String> toRemoveGames;

    private GameManager() {
        gameMap = new HashMap<String, Game>();
        toRemoveGames = new ArrayList<String>();
    }

    public void addGame(String gameId, Battlefield battlefield, List<Player> playerList) {
        log.debug(" [-] ADDING GAME {} IN THE GAME MANAGER", gameId);
        gameMap.put(gameId, new Game(battlefield, playerList, gameId));
    }

    public void closeGame(String gameId) {
        toRemoveGames.add(gameId);
    }

    public void getGame(String gameId) {
        gameMap.get(gameId);
    }

    public void run() {
        while (!toRemoveGames.isEmpty()) {
            String gameId = toRemoveGames.get(0);
            gameMap.remove(gameId);
            toRemoveGames.remove(gameId);
        }

        for (Game game : gameMap.values()) {
            game.receiveMessage();
        }
    }
}
