package com.ghostofpq.kulkan.client.scenes;

public interface Scene {

    public void init();

    public void update(long deltaTime);

    public void render();

    public void manageInput();

    public void closeConnections();
}
