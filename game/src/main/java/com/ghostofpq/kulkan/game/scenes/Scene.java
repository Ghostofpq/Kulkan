package com.ghostofpq.kulkan.game.scenes;

public interface Scene {

    public void init();

    public void update(long deltaTime);

    public void render();

    public void manageInput();
}
