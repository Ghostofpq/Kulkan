package com.ghostofpq.kulkan.server;

import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.entities.battlefield.Battlefield;
import com.ghostofpq.kulkan.entities.battlefield.BattlefieldElement;
import com.ghostofpq.kulkan.server.authentication.AuthenticationManager;
import com.ghostofpq.kulkan.server.authentication.PingManager;
import com.ghostofpq.kulkan.server.authentication.PingWatchdog;
import com.ghostofpq.kulkan.server.database.ItemService;
import com.ghostofpq.kulkan.server.database.UserService;
import com.ghostofpq.kulkan.server.database.controller.ItemController;
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
    private Thread gameManagerThread;
    private Thread authThread;
    private Thread lobbyManagerThread;
    private Thread userServiceThread;
    private Thread itemServiceThread;
    private Thread matchmakingManagerThread;
    private Thread pingManagerThread;
    private Thread watchdogThread;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private LobbyManager lobbyManager;
    @Autowired
    private GameManager gameManager;
    @Autowired
    private MatchmakingManager matchmakingManager;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemController itemController;
    @Autowired
    private PingManager pingManager;
    private PingWatchdog watchdog;

    private Server() {
    }

    public static void main(String[] argv) throws IOException, InterruptedException {
        ApplicationContext context = new ClassPathXmlApplicationContext(CONTEXT_URI);
        Server s = ((Server) context.getBean("server"));
        s.init();
        s.start();
    }

    private void init() throws IOException, InterruptedException {
        authenticationManager.initConnection();
        lobbyManager.initConnections();
        matchmakingManager.initConnections();
        userService.initConnection();
        itemService.initConnection();
        pingManager.initConnection();
        itemController.populateItemRepository();
    }

    public void start() throws IOException, InterruptedException {
        authThread = new Thread(authenticationManager);
        authThread.start();
        gameManagerThread = new Thread(gameManager);
        gameManagerThread.start();
        userServiceThread = new Thread(userService);
        userServiceThread.start();
        lobbyManagerThread = new Thread(lobbyManager);
        lobbyManagerThread.start();
        matchmakingManagerThread = new Thread(matchmakingManager);
        matchmakingManagerThread.start();
        itemServiceThread = new Thread(itemService);
        itemServiceThread.start();
        pingManagerThread = new Thread(pingManager);
        pingManagerThread.start();

        watchdog = new PingWatchdog(pingManagerThread, 10000);
        watchdogThread = new Thread(watchdog);
        watchdogThread.start();
    }

    public void shutDown() {
        authenticationManager.setRequestClose(true);
        authThread.interrupt();
        gameManager.setRequestClose(true);
        gameManagerThread.interrupt();
        userService.setRequestClose(true);
        userServiceThread.interrupt();
        lobbyManager.setRequestClose(true);
        lobbyManagerThread.interrupt();
        matchmakingManager.setRequestClose(true);
        matchmakingManagerThread.interrupt();
        itemService.setRequestClose(true);
        itemServiceThread.interrupt();
        pingManager.setRequestClose(true);
        pingManagerThread.interrupt();
        watchdog.stop();
        watchdogThread.interrupt();
    }

    public void createMap() {
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
            battlefield.addDeploymentZone(0, position);
            Position position2 = new Position(i, 0, depth - 1);
            battlefield.addDeploymentZone(1, position2);
        }
        battlefield.setStartingPointsOfViewForPlayer(0, PointOfView.NORTH);
        battlefield.setStartingPointsOfViewForPlayer(1, PointOfView.SOUTH);

        battlefield.addBattlefieldElement(0, 1, 0, BattlefieldElement.BattlefieldElementType.BLOC);
        battlefield.addBattlefieldElement(0, 2, 1, BattlefieldElement.BattlefieldElementType.BLOC);
        battlefield.addBattlefieldElement(0, 3, 2, BattlefieldElement.BattlefieldElementType.BLOC);

        SaveManager saveManager = SaveManager.getInstance();
        saveManager.saveMap(battlefield, "mapTest1");
    }
}
