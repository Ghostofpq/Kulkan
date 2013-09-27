package com.ghostofpq.kulkan.entities.messages;


import java.io.Serializable;

public class MessageAuthenticationResponse extends Message implements Serializable {

    private String pseudo;
    private String password;
    private String tokenKey;
    private MessageErrorCode errorCode;

    public MessageAuthenticationResponse(String pseudo, String password, String tokenKey, MessageErrorCode errorCode) {
        this.type = MessageType.AUTHENTICATION_RESPONSE;
        this.pseudo = pseudo;
        this.password = password;
        this.tokenKey = tokenKey;
        this.errorCode = errorCode;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getPassword() {
        return password;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public MessageErrorCode getErrorCode() {
        return errorCode;
    }
}
