package com.ghostofpq.kulkan.entities.inventory;

import com.ghostofpq.kulkan.entities.inventory.item.Item;
import com.ghostofpq.kulkan.entities.inventory.item.ItemType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory implements Serializable {
    private Map<String, Integer> itemIdQuantityMap;

    public Inventory() {
        itemIdQuantityMap = new HashMap<String, Integer>();
    }

    public void add(String itemId, int quantity) {
        if (itemIdQuantityMap.keySet().contains(itemId)) {
            Integer currentQuantity = itemIdQuantityMap.get(itemId);
            Integer newQuantity = currentQuantity + quantity;
            itemIdQuantityMap.put(itemId, newQuantity);
        } else {
            itemIdQuantityMap.put(itemId, quantity);
        }
    }

    public void addOne(String itemId) {
        add(itemId, 1);
    }

    public void remove(String itemId, int quantity) {
        if (itemIdQuantityMap.keySet().contains(itemId)) {
            Integer currentQuantity = itemIdQuantityMap.get(itemId);
            Integer newQuantity = currentQuantity - quantity;
            if (newQuantity >= 0) {
                itemIdQuantityMap.put(itemId, newQuantity);
            } else {
                throw new IllegalArgumentException("Quantity to remove is superior than current quantity");
            }
        } else {
            throw new IllegalArgumentException("Invalid itemId");
        }
    }

    public void removeOne(String itemId) {
        remove(itemId, 1);
    }

    public int getNumberOf(String itemId) {
        Integer result = itemIdQuantityMap.get(itemId);
        if (null == result) {
            result = 0;
        }
        return result;
    }

    public List<Item> getItemsByType(ItemType itemType) {
        List<Item> itemList = new ArrayList<Item>();
        for (String itemId : itemIdQuantityMap.keySet()) {
            Item item = ItemFactory.createItem(itemId);
            if (item.getItemType().equals(itemType)) {
                if (itemIdQuantityMap.get(itemId) > 0) {
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    public List<Item> getAll() {
        List<Item> itemList = new ArrayList<Item>();
        for (String itemId : itemIdQuantityMap.keySet()) {
            Item item = ItemFactory.createItem(itemId);
            if (itemIdQuantityMap.get(itemId) > 0) {
                itemList.add(item);
            }
        }
        return itemList;
    }
}
