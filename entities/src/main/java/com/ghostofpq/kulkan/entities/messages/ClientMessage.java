package com.ghostofpq.kulkan.entities.messages;


import java.io.Serializable;

public abstract class ClientMessage extends Message implements Serializable {
    protected String keyToken;

    public String getKeyToken() {
        return keyToken;
    }
}
