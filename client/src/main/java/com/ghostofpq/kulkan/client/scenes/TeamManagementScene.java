package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.Button;
import com.ghostofpq.kulkan.client.graphics.TeamManagementCharacterRender;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class TeamManagementScene implements Scene {
    private static volatile TeamManagementScene instance = null;
    private List<TeamManagementCharacterRender> teamManagementCharacterRenderList;
    private List<Button> buttonsNewGameCharacter;
    private Button backButton;

    private TeamManagementScene() {
        teamManagementCharacterRenderList = new ArrayList<TeamManagementCharacterRender>();
        buttonsNewGameCharacter = new ArrayList<Button>();
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
        backButton = new Button(width, 0, Client.getInstance().getWidth() / 6, 50, "BACK") {
            @Override
            public void onClick() {
                Client.getInstance().setCurrentScene(LobbyScene.getInstance());
            }
        };

    }

    @Override
    public void update(long deltaTime) {

    }

    @Override
    public void render() {
        GraphicsManager.getInstance().make2D();
        for (TeamManagementCharacterRender teamManagementCharacterRender : teamManagementCharacterRenderList) {
            teamManagementCharacterRender.draw();
        }
        for (Button button : buttonsNewGameCharacter) {
            button.draw();
        }
        backButton.draw();
    }

    @Override
    public void manageInput() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                for (Button button : buttonsNewGameCharacter) {
                    if (button.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                        button.onClick();
                    }
                }
                for (TeamManagementCharacterRender teamManagementCharacterRender : teamManagementCharacterRenderList) {
                    if (teamManagementCharacterRender.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                        Client.getInstance().setCurrentScene(GameCharacterManageScene.getInstance());
                        GameCharacterManageScene.getInstance().setGameCharacter(teamManagementCharacterRender.getCharacter());
                    }
                }
                if (backButton.isClicked(Mouse.getX(), Client.getInstance().getHeight() - Mouse.getY())) {
                    backButton.onClick();
                }
            }
        }
    }

    @Override
    public void closeConnections() {
    }

    @Override
    public void receiveMessage() {
    }
}
