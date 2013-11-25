package com.ghostofpq.kulkan.client.scenes;

import java.io.IOException;

public interface Scene {

    public void init();

    public void initConnections() throws IOException;

    public void update(long deltaTime);

    public void render();

    public void manageInput();

    public void closeConnections() throws IOException;

    public void receiveMessage();
}
