package com.ghostofpq.kulkan.entities.messages.auth;

import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

public class MessageCreateAccount extends Message {
    private String userName;
    private String password;

    public MessageCreateAccount(String userName, String password) {
        this.type = MessageType.CREATE_ACCOUT;
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}