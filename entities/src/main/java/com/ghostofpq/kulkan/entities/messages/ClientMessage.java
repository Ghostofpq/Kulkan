package com.ghostofpq.kulkan.entities.messages;


import java.io.Serializable;

public abstract class ClientMessage extends Message implements Serializable {
    protected String keyToken;
    protected String username;

    public String getKeyToken() {
        return keyToken;
    }

    public String getUsername() {
        return username;
    }
}
