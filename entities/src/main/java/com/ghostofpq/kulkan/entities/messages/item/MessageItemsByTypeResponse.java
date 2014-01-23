package com.ghostofpq.kulkan.entities.messages.item;

import com.ghostofpq.kulkan.entities.inventory.item.Item;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

import java.util.List;

public class MessageItemsByTypeResponse extends Message {
    private List<Item> items;

    public MessageItemsByTypeResponse(List<Item> items) {
        this.type = MessageType.GET_ITEMS_BY_TYPE_RESPONSE;
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }
}
