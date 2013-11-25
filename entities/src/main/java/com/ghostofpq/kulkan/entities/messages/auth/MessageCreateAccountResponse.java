package com.ghostofpq.kulkan.entities.messages.auth;

import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageErrorCode;
import com.ghostofpq.kulkan.entities.messages.MessageType;

public class MessageCreateAccountResponse extends Message {
    private MessageErrorCode errorCode;

    public MessageCreateAccountResponse(MessageErrorCode errorCode) {
        this.type = MessageType.CREATE_ACCOUT_RESPONSE;
        this.errorCode = errorCode;
    }

    public MessageErrorCode getErrorCode() {
        return errorCode;
    }
}
