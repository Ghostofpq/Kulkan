package com.ghostofpq.kulkan.entities.messages;


import java.io.Serializable;
import java.util.List;

public class MessageLobbyServer extends MessageServer implements Serializable {
    private String message;

    public MessageLobbyServer(List<String> targetList, String message) {
        this.type = MessageType.LOBBY_SERVER;
        this.targetList = targetList;
        this.message = message;
    }

    public String getMessage(String tokenKey) {
        String result = "";
        if (targetList.contains(tokenKey)) {
            result = message;
        }
        return result;
    }
}
