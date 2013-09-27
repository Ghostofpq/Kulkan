package com.ghostofpq.kulkan.entities.messages;

import java.io.Serializable;

public class MessageFinishDeployment extends Message implements Serializable {
    public MessageFinishDeployment() {
        type = MessageType.FINISH_DEPLOYMENT;
    }
}
