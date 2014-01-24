package com.ghostofpq.kulkan.entities.messages.auth;


import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;

public class MessageAuthenticationRequest extends Message implements Serializable {

    private String pseudo;
    private String password;

    public MessageAuthenticationRequest(String pseudo, String password) {
        this.type = MessageType.AUTHENTICATION_REQUEST;
        this.pseudo = pseudo;
        this.password = DigestUtils.shaHex(password);
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getPassword() {
        return password;
    }
}
