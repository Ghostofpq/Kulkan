package com.ghostofpq.kulkan.client;

import com.ghostofpq.kulkan.entities.character.Player;

public class ClientContext {
    private Player player;
    private String tokenKey;


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
}
