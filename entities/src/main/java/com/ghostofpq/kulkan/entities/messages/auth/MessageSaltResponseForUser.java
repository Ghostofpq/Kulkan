package com.ghostofpq.kulkan.entities.messages.auth;

import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

public class MessageSaltResponseForUser extends Message {
    private String salt;

    public MessageSaltResponseForUser(String salt) {
        this.type = MessageType.AUTHENTICATION_SALT_RESPONSE;
        this.salt = salt;
    }

    public String getSalt() {
        return salt;
    }
}