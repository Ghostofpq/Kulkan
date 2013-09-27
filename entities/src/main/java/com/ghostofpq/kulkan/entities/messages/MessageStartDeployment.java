package com.ghostofpq.kulkan.entities.messages;

import java.io.Serializable;

public class MessageStartDeployment extends Message implements Serializable {

    public MessageStartDeployment() {
        type = MessageType.START_DEPLOYMENT;
    }
}
