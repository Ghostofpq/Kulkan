package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.Button;
import com.ghostofpq.kulkan.client.graphics.TeamManagementCharacterRender;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.user.MessagePlayerUpdate;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TeamManagementScene implements Scene {
    private static volatile TeamManagementScene instance = null;
    private List<TeamManagementCharacterRender> teamManagementCharacterRenderList;
    private List<TeamManagementCharacterRender> stockManagementCharacterRenderListPage1;
    private List<TeamManagementCharacterRender> stockManagementCharacterRenderListPage2;
    private List<TeamManagementCharacterRender> stockManagementCharacterRenderListPage3;
    private List<Button> buttonsNewGameCharacter;
    private List<Button> buttonsNewGameCharacterPage1;
    private List<Button> buttonsNewGameCharacterPage2;
    private List<Button> buttonsNewGameCharacterPage3;
    private Button backButton;
    private Button teamViewButton;
    private Button stockViewPage1Button;
    private Button stockViewPage2Button;
    private Button stockViewPage3Button;
    private ManagementView view;

    private TeamManagementScene() {
        teamManagementCharacterRenderList = new ArrayList<TeamManagementCharacterRender>();
        buttonsNewGameCharacter = new ArrayList<Button>();

        stockManagementCharacterRenderListPage1 = new ArrayList<TeamManagementCharacterRender>();
        stockManagementCharacterRenderListPage2 = new ArrayList<TeamManagementCharacterRender>();
        stockManagementCharacterRenderListPage3 = new ArrayList<TeamManagementCharacterRender>();

        buttonsNewGameCharacterPage1 = new ArrayList<Button>();
        buttonsNewGameCharacterPage2 = new ArrayList<Button>();
        buttonsNewGameCharacterPage3 = new ArrayList<Button>();
    }

    public static TeamManagementScene getInstance() {
        if (instance == null) {
            synchronized (TeamManagementScene.class) {
                if (instance == null) {
                    instance = new TeamManagementScene();
                }
            }
        }
        return instance;
    }

    @Override
    public void init() {
        teamManagementCharacterRenderList = new ArrayList<TeamManagementCharacterRender>();
        buttonsNewGameCharacter = new ArrayList<Button>();

        stockManagementCharacterRenderListPage1 = new ArrayList<TeamManagementCharacterRender>();
        stockManagementCharacterRenderListPage2 = new ArrayList<TeamManagementCharacterRender>();
        stockManagementCharacterRenderListPage3 = new ArrayList<TeamManagementCharacterRender>();

        buttonsNewGameCharacterPage1 = new ArrayList<Button>();
        buttonsNewGameCharacterPage2 = new ArrayList<Button>();
        buttonsNewGameCharacterPage3 = new ArrayList<Button>();

        int width = 5 * Client.getInstance().getWidth() / 6;
        int height = 3 * Client.getInstance().getHeight() / 4;
        int[] posXs = {0, width / 2, 0, width / 2};
        int[] posYs = {0, 0, height / 2, height / 2};

        for (int i = 0; i < 4; i++) {
            if (Client.getInstance().getPlayer().getTeam().size() > i) {
                teamManagementCharacterRenderList.add(
                        new TeamManagementCharacterRender(posXs[i], posYs[i],
                                width / 2, height / 2,
                                2, Client.getInstance().getPlayer().getTeam().get(i)));
            } else {
                buttonsNewGameCharacter.add(new Button(posXs[i], posYs[i], width / 2, height / 2, "HIRE NEW WARRIOR") {
                    @Override
                    public void onClick() {
                        Client.getInstance().setCurrentScene(NewGameCharacterScene.getInstance());
                    }
                });
            }
        }

        for (int i = 0; i < 4; i++) {
            if (Client.getInstance().getPlayer().getStock().size() > i) {
                stockManagementCharacterRenderListPage1.add(
                        new TeamManagementCharacterRender(posXs[i], posYs[i],
                                width / 2, height / 2,
                                2, Client.getInstance().getPlayer().getStock().get(i)));
            } else {
                buttonsNewGameCharacterPage1.add(new Button(posXs[i], posYs[i], width / 2, height / 2, "HIRE NEW WARRIOR") {
                    @Override
                    public void onClick() {
                        Client.getInstance().setCurrentScene(NewGameCharacterScene.getInstance());
                    }
                });
            }
        }

        for (int i = 4; i < 8; i++) {
            if (Client.getInstance().getPlayer().getStock().size() > i) {
                stockManagementCharacterRenderListPage2.add(
                        new TeamManagementCharacterRender(posXs[i - 4], posYs[i - 4],
                                width / 2, height / 2,
                                2, Client.getInstance().getPlayer().getStock().get(i)));
            } else {
                buttonsNewGameCharacterPage2.add(new Button(posXs[i - 4], posYs[i - 4], width / 2, height / 2, "HIRE NEW WARRIOR") {
                    @Override
                    public void onClick() {
                        Client.getInstance().setCurrentScene(NewGameCharacterScene.getInstance());
                    }
                });
            }
        }

        for (int i = 8; i < 12; i++) {
            if (Client.getInstance().getPlayer().getStock().size() > i) {
                stockManagementCharacterRenderListPage3.add(
                        new TeamManagementCharacterRender(posXs[i - 8], posYs[i - 8],
                                width / 2, height / 2,
                                2, Client.getInstance().getPlayer().getStock().get(i)));
            } else {
                buttonsNewGameCharacterPage3.add(new Button(posXs[i - 8], posYs[i - 8], width / 2, height / 2, "HIRE NEW WARRIOR") {
                    @Override
                    public void onClick() {
                        Client.getInstance().setCurrentScene(NewGameCharacterScene.getInstance());
                    }
                });
            }
        }

        backButton = new Button(width, 0, Client.getInstance().getWidth() / 6, 50, "BACK") {
            @Override
            public void onClick() {
                Client.getInstance().setCurrentScene(LobbyScene.getInstance());
            }
        };

        teamViewButton = new Button(width, 50, Client.getInstance().getWidth() / 6, 50, "TEAM") {
            @Override
            public void onClick() {
                view = ManagementView.TEAM;
            }
        };

        stockViewPage1Button = new Button(width, 100, Client.getInstance().getWidth() / 6, 50, "PAGE 1") {
            @Override
            public void onClick() {
                view = ManagementView.STOCK_PAGE_1;
            }
        };

        stockViewPage2Button = new Button(width, 150, Client.getInstance().getWidth() / 6, 50, "PAGE 2") {
            @Override
            public void onClick() {
                view = ManagementView.STOCK_PAGE_2;
            }
        };

        stockViewPage3Button = new Button(width, 200, Client.getInstance().getWidth() / 6, 50, "PAGE 3") {
            @Override
            public void onClick() {
                view = ManagementView.STOCK_PAGE_3;
            }
        };

        view = ManagementView.TEAM;
    }

    @Override
    public void update(long deltaTime) {

    }

    @Override
    public void render() {
        GraphicsManager.getInstance().make2D();
        switch (view) {
            case STOCK_PAGE_1:
                for (TeamManagementCharacterRender teamManagementCharacterRender : stockManagementCharacterRenderListPage1) {
                    teamManagementCharacterRender.draw();
                }
                for (Button button : buttonsNewGameCharacterPage1) {
                    button.draw();
                }
                break;
            case STOCK_PAGE_2:
                for (TeamManagementCharacterRender teamManagementCharacterRender : stockManagementCharacterRenderListPage2) {
                    teamManagementCharacterRender.draw();
                }
                for (Button button : buttonsNewGameCharacterPage2) {
                    button.draw();
                }
                break;
            case STOCK_PAGE_3:
                for (TeamManagementCharacterRender teamManagementCharacterRender : stockManagementCharacterRenderListPage3) {
                    teamManagementCharacterRender.draw();
                }
                for (Button button : buttonsNewGameCharacterPage3) {
                    button.draw();
                }
                break;
            case TEAM:
                for (TeamManagementCharacterRender teamManagementCharacterRender : teamManagementCharacterRenderList) {
                    teamManagementCharacterRender.draw();
                }
                for (Button button : buttonsNewGameCharacter) {
                    button.draw();
                }
                break;
        }

        backButton.draw();
        teamViewButton.draw();
        stockViewPage1Button.draw();
        stockViewPage2Button.draw();
        stockViewPage3Button.draw();
    }

    @Override
    public void manageInput() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                if (view.equals(ManagementView.TEAM)) {
                    for (Button button : buttonsNewGameCharacter) {
                        if (button.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                            button.onClick();
                        }
                    }
                    for (TeamManagementCharacterRender teamManagementCharacterRender : teamManagementCharacterRenderList) {
                        if (teamManagementCharacterRender.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                            log.debug(teamManagementCharacterRender.getCharacter().getName());
                            GameCharacterManageScene.getInstance().setGameCharacter(teamManagementCharacterRender.getCharacter());
                            Client.getInstance().setCurrentScene(GameCharacterManageScene.getInstance());
                        }
                    }
                }
                if (view.equals(ManagementView.STOCK_PAGE_1)) {
                    for (Button button : buttonsNewGameCharacterPage1) {
                        if (button.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                            button.onClick();
                        }
                    }
                    for (TeamManagementCharacterRender teamManagementCharacterRender : stockManagementCharacterRenderListPage1) {
                        if (teamManagementCharacterRender.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                            log.debug(teamManagementCharacterRender.getCharacter().getName());
                            GameCharacterManageScene.getInstance().setGameCharacter(teamManagementCharacterRender.getCharacter());
                            Client.getInstance().setCurrentScene(GameCharacterManageScene.getInstance());
                        }
                    }
                }
                if (view.equals(ManagementView.STOCK_PAGE_2)) {
                    for (Button button : buttonsNewGameCharacterPage2) {
                        if (button.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                            button.onClick();
                        }
                    }
                    for (TeamManagementCharacterRender teamManagementCharacterRender : stockManagementCharacterRenderListPage2) {
                        if (teamManagementCharacterRender.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                            log.debug(teamManagementCharacterRender.getCharacter().getName());
                            GameCharacterManageScene.getInstance().setGameCharacter(teamManagementCharacterRender.getCharacter());
                            Client.getInstance().setCurrentScene(GameCharacterManageScene.getInstance());
                        }
                    }
                }
                if (view.equals(ManagementView.STOCK_PAGE_3)) {
                    for (Button button : buttonsNewGameCharacterPage3) {
                        if (button.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                            button.onClick();
                        }
                    }
                    for (TeamManagementCharacterRender teamManagementCharacterRender : stockManagementCharacterRenderListPage3) {
                        if (teamManagementCharacterRender.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                            log.debug(teamManagementCharacterRender.getCharacter().getName());
                            GameCharacterManageScene.getInstance().setGameCharacter(teamManagementCharacterRender.getCharacter());
                            Client.getInstance().setCurrentScene(GameCharacterManageScene.getInstance());
                        }
                    }
                }

                if (backButton.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    backButton.onClick();
                }
                if (teamViewButton.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    teamViewButton.onClick();
                }
                if (stockViewPage1Button.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    stockViewPage1Button.onClick();
                }
                if (stockViewPage2Button.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    stockViewPage2Button.onClick();
                }
                if (stockViewPage3Button.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    stockViewPage3Button.onClick();
                }
            }
        }
    }

    @Override
    public void closeConnections() {
    }

    @Override
    public void receiveMessage() {
        Message message = Client.getInstance().receiveMessage();
        if (null != message) {
            switch (message.getType()) {
                case PLAYER_UPDATE:
                    log.debug("PLAYER_UPDATE");
                    MessagePlayerUpdate messagePlayerUpdate = (MessagePlayerUpdate) message;
                    Client.getInstance().setPlayer(messagePlayerUpdate.getPlayer());
                    Client.getInstance().setCurrentScene(TeamManagementScene.getInstance());
                    break;
            }
        }
    }

    public enum ManagementView {
        TEAM, STOCK_PAGE_1, STOCK_PAGE_2, STOCK_PAGE_3
    }
}
