package com.ghostofpq.kulkan.game.scenes;

import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.game.graphics.CharacterRender;
import com.ghostofpq.kulkan.game.main.Game;
import org.newdawn.slick.Color;


public class PlayerMenu implements Scene {
    private static PlayerMenu instance = new PlayerMenu();
    private Player player;
    private CharacterRender characterRender1;
    private CharacterRender characterRender2;
    private CharacterRender characterRender3;
    private CharacterRender characterRender4;

    private PlayerMenu() {
    }

    public static PlayerMenu getInstance() {
        if (instance == null) {
            synchronized (MainMenu.class) {
                if (instance == null) {
                    instance = new PlayerMenu();
                }
            }
        }
        return instance;
    }

    @Override
    public void init() {
        player = Game.getInstance().getPlayer();

        characterRender1 = new CharacterRender(0, 0, 400, 150, 3, player.getTeam().getTeam().get(0));
        characterRender2 = new CharacterRender(0, 150, 400, 150, 3, player.getTeam().getTeam().get(0));
        characterRender3 = new CharacterRender(0, 300, 400, 150, 3, player.getTeam().getTeam().get(0));
        characterRender4 = new CharacterRender(0, 450, 400, 150, 3, player.getTeam().getTeam().get(0));


    }

    @Override
    public void update(long deltaTime) {

    }

    @Override
    public void render() {
        characterRender1.render(Color.white);
        characterRender2.render(Color.white);
        characterRender3.render(Color.white);
        characterRender4.render(Color.white);

    }

    @Override
    public void manageInput() {

    }
}
