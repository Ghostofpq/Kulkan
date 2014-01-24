package com.ghostofpq.kulkan.client;

import com.ghostofpq.kulkan.entities.character.Player;

public class ClientContext {
    // WINDOW
    public static int height;
    public static int width;
    //MESSAGING
    private final String AUTHENTICATION_QUEUE_NAME = "authentication";
    // USER INFO
    private Player player;
    private String tokenKey;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public String getAuthenticationQueueName() {
        return AUTHENTICATION_QUEUE_NAME;
    }
}
