package com.ghostofpq.kulkan.entities.messages.item;

import com.ghostofpq.kulkan.entities.inventory.item.ItemType;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

public class MessageItemsByTypeRequest extends ClientMessage {
    private ItemType itemType;

    public MessageItemsByTypeRequest(String keyToken, ItemType itemType) {
        this.type = MessageType.GET_ITEMS_BY_TYPE_REQUEST;
        this.keyToken = keyToken;
        this.itemType = itemType;
    }

    public ItemType getItemType() {
        return itemType;
    }
}
