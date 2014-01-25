package com.ghostofpq.kulkan.entities.messages.auth;

import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;
import org.apache.commons.codec.digest.DigestUtils;

public class MessageCreateAccount extends Message {
    private String pseudo;
    private String password;

    public MessageCreateAccount(String pseudo, String password) {
        this.type = MessageType.CREATE_ACCOUNT;
        this.pseudo = pseudo;
        this.password = DigestUtils.shaHex(password);
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return new StringBuffer().append(System.getProperty("line.separator"))
                .append("     ").append("Message Type : ").append(type).append(System.getProperty("line.separator"))
                .append("     ").append("Pseudo : ").append(pseudo).append(System.getProperty("line.separator"))
                .append("     ").append("Password : ").append(password).toString();
    }
}
