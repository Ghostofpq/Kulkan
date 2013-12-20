package com.ghostofpq.kulkan.server;

import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.battlefield.Battlefield;
import com.ghostofpq.kulkan.entities.battlefield.BattlefieldElement;
import com.ghostofpq.kulkan.server.authentication.AuthenticationManager;
import com.ghostofpq.kulkan.server.database.UserService;
import com.ghostofpq.kulkan.server.game.GameManager;
import com.ghostofpq.kulkan.server.lobby.LobbyManager;
import com.ghostofpq.kulkan.server.matchmaking.MatchmakingManager;
import com.ghostofpq.kulkan.server.utils.SaveManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

@Slf4j
public class Server {
    private static final java.lang.String CONTEXT_URI = "META-INF/spring/server-context.xml";
    private AuthenticationManager authenticationManager;
    private LobbyManager lobbyManager;
    private GameManager gameManager;
    private MatchmakingManager matchmakingManager;
    private boolean requestClose;
    @Autowired
    private UserService userService;

    private Server() {
        //createMAp();
        requestClose = false;
    }

    public static void main(String[] argv) throws IOException, InterruptedException {
        ApplicationContext context = new ClassPathXmlApplicationContext(CONTEXT_URI);
        Server s = ((Server) context.getBean("server"));
        s.init();
        s.run();
    }

    private void init() throws IOException, InterruptedException {
        authenticationManager.initConnection();
        lobbyManager.initConnections();
        matchmakingManager.initConnections();
        userService.initConnection();
    }

    public void run() throws IOException, InterruptedException {
        Thread authThread = new Thread(authenticationManager);
        authThread.start();
        Thread gameManagerThread = new Thread(gameManager);
        gameManagerThread.start();
        Thread userServiceThread = new Thread(userService);
        userServiceThread.start();


        while (!requestClose) {
            lobbyManager.run();
            matchmakingManager.run();
        }

        authenticationManager.setRequestClose(true);
        authThread.interrupt();
        gameManager.setRequestClose(true);
        gameManagerThread.interrupt();
        userService.setRequestClose(true);
        userServiceThread.interrupt();
    }

    public void shutDown() {
        requestClose = true;
    }

    public void createMAp() {

        int length = 10;
        int height = 5;
        int depth = 10;

        Battlefield battlefield = new Battlefield(length, height, depth, 2);

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < depth; j++) {
                battlefield.addBattlefieldElement(i, 0, j, BattlefieldElement.BattlefieldElementType.BLOC);
            }
        }

        for (int i = 0; i < length; i++) {
            Position position = new Position(i, 0, 0);
            battlefield.addDeployementZone(0, position);
            Position position2 = new Position(i, 0, depth - 1);
            battlefield.addDeployementZone(1, position2);
        }
        battlefield.setStartingPointsOfViewForPlayer(0, PointOfView.NORTH);
        battlefield.setStartingPointsOfViewForPlayer(1, PointOfView.SOUTH);

        battlefield.addBattlefieldElement(0, 1, 0, BattlefieldElement.BattlefieldElementType.BLOC);
        battlefield.addBattlefieldElement(0, 2, 1, BattlefieldElement.BattlefieldElementType.BLOC);
        battlefield.addBattlefieldElement(0, 3, 2, BattlefieldElement.BattlefieldElementType.BLOC);

        SaveManager saveManager = SaveManager.getInstance();
        saveManager.saveMap(battlefield, "mapTest1");

    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setLobbyManager(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void setMatchmakingManager(MatchmakingManager matchmakingManager) {
        this.matchmakingManager = matchmakingManager;
    }
}
