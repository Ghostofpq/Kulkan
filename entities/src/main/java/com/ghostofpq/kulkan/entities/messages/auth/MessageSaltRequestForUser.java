package com.ghostofpq.kulkan.entities.messages.auth;

import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

public class MessageSaltRequestForUser extends Message {
    private String userName;

    public MessageSaltRequestForUser(String userName) {
        this.type = MessageType.AUTHENTICATION_SALT_REQUEST;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
