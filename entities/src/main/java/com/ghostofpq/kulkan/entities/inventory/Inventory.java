package com.ghostofpq.kulkan.entities.inventory;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
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

    public void remove(String itemId, int quantity) throws IllegalArgumentException {
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

    public void removeOne(String itemId) throws IllegalArgumentException {
        remove(itemId, 1);
    }
}
