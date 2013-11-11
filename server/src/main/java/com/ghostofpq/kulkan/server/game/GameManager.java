package com.ghostofpq.kulkan.server.game;

import com.ghostofpq.kulkan.entities.battlefield.Battlefield;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.server.authentication.AuthenticationManager;
import com.ghostofpq.kulkan.server.database.controller.UserController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class GameManager implements Runnable {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserController userController;
    private Map<String, Game> gameMap;
    private List<String> toRemoveGames;
    private boolean requestClose;
    private String hostIp;
    private Integer hostPort;

    private GameManager() {
        gameMap = new HashMap<String, Game>();
        toRemoveGames = new ArrayList<String>();
        requestClose = false;
    }

    public void addGame(String gameId, Battlefield battlefield, List<Player> playerList) {
        log.debug(" [-] ADDING GAME {} IN THE GAME MANAGER", gameId);
        Game game = new Game(battlefield, playerList, gameId, authenticationManager, userController, this, hostIp, hostPort);
        gameMap.put(gameId, game);
    }

    public void closeGame(String gameId) {
        toRemoveGames.add(gameId);
    }

    public void getGame(String gameId) {
        gameMap.get(gameId);
    }

    public void setRequestClose(boolean requestClose) {
        this.requestClose = requestClose;
    }

    public void run() {
        while (!requestClose) {
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

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public void setHostPort(Integer hostPort) {
        this.hostPort = hostPort;
    }
}
