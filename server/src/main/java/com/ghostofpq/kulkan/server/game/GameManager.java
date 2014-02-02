package com.ghostofpq.kulkan.server.game;

import com.ghostofpq.kulkan.entities.battlefield.Battlefield;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.server.authentication.AuthenticationManager;
import com.ghostofpq.kulkan.server.database.controller.UserController;
import com.ghostofpq.kulkan.server.database.model.User;
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
    private Map<String, Thread> gameMapThread;
    private Map<String, String> tokenKeyToGame;

    private List<String> toRemoveGames;
    private boolean requestClose;
    private String hostIp;
    private Integer hostPort;

    private GameManager() {
        gameMap = new HashMap<String, Game>();
        toRemoveGames = new ArrayList<String>();
        tokenKeyToGame = new HashMap<String, String>();
        gameMapThread = new HashMap<String, Thread>();
        requestClose = false;
    }

    public void addGame(String gameId, Battlefield battlefield, List<User> userList) {
        log.debug(" [-] ADDING GAME {} IN THE GAME MANAGER", gameId);
        Map<String, Player> keyTokenPlayerMap = new HashMap<String, Player>();
        for (User user : userList) {
            tokenKeyToGame.put(user.getTokenKey(), gameId);
            keyTokenPlayerMap.put(user.getTokenKey(), user.toPlayer());
        }

        Game game = new Game(battlefield, gameId, keyTokenPlayerMap, this, hostIp, hostPort);
        gameMap.put(gameId, game);

        Thread gameThread = new Thread(game);
        gameMapThread.put(gameId, gameThread);
        gameThread.start();
    }

    public void closeGame(String gameId) {
        toRemoveGames.add(gameId);
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
        }
    }

    public void setPlayerIsDisconnected(String tokenKey) {
        String concernedGameId = tokenKeyToGame.get(tokenKey);
        Game concernedGame = gameMap.get(concernedGameId);
        Thread concernedGameThread = gameMapThread.get(concernedGameId);

        concernedGame.setPlayerIsDisconnected(tokenKey);
        concernedGameThread.interrupt();
    }

    public List<Player> updatePlayers(List<Player> playerList) {
        List<Player> updatedPlayerList = new ArrayList<Player>();
        for (Player player : playerList) {
            User updatedUser = userController.updateGameCharacters(player);
            updatedPlayerList.add(updatedUser.toPlayer());
        }
        return updatedPlayerList;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public void setHostPort(Integer hostPort) {
        this.hostPort = hostPort;
    }
}
