package com.ghostofpq.kulkan.entities.messages.user;

import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

public class MessageError extends Message {
    private String error;

    public MessageError(String error) {
        this.type = MessageType.ERROR;
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
