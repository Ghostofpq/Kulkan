package com.ghostofpq.kulkan.entities.messages.user;

import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

public class MessageBuyItem extends ClientMessage {
    private String itemId;

    public MessageBuyItem(String keyToken, String username, String itemId) {
        this.type = MessageType.BUY_ITEM_REQUEST;
        this.keyToken = keyToken;
        this.username = username;
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }
}
