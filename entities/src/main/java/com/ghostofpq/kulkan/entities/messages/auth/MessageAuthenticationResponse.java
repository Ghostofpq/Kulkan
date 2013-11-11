package com.ghostofpq.kulkan.entities.messages.auth;


import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.io.Serializable;

public class MessageAuthenticationResponse extends Message implements Serializable {
    private String pseudo;
    private String password;
    private String tokenKey;
    private MessageErrorCode errorCode;
    private Player player;

    public MessageAuthenticationResponse(String pseudo, String password, String tokenKey, Player player, MessageErrorCode errorCode) {
        this.type = MessageType.AUTHENTICATION_RESPONSE;
        this.pseudo = pseudo;
        this.password = password;
        this.tokenKey = tokenKey;
        this.player = player;
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

    public Player getPlayer() {
        return player;
    }
}
