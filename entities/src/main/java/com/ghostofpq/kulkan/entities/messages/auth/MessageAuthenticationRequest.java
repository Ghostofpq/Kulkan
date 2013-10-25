package com.ghostofpq.kulkan.entities.messages.auth;


import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;

public class MessageAuthenticationRequest extends Message implements Serializable {

    private String pseudo;
    private String password;

    public MessageAuthenticationRequest(String pseudo, String password) {
        this.type = MessageType.AUTHENTICATION_REQUEST;
        this.pseudo = pseudo;
        this.password = password;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getPassword() {
        return password;
    }
}
