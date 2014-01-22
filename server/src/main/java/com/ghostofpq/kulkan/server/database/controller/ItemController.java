package com.ghostofpq.kulkan.server.database.controller;

import com.ghostofpq.kulkan.entities.inventory.item.Item;
import com.ghostofpq.kulkan.entities.inventory.item.ItemType;
import com.ghostofpq.kulkan.server.database.model.ItemDB;
import com.ghostofpq.kulkan.server.database.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ItemController {
    @Autowired
    private ItemRepository itemRepository;

    private Item getItemByName(String name) {
        List<ItemDB> itemDBs = itemRepository.findByName(name);
        Item item = null;
        if (itemDBs.size() == 1) {
            item = itemDBs.get(0).toItem();
        } else if (itemDBs.size() > 1) {
            log.error("multiple results for item name : [{}]", name);
        } else {
            log.error("no result for item name : [{}]", name);
        }
        return item;
    }

    private List<Item> getByItemType(ItemType itemType) {
        List<ItemDB> itemDBs = itemRepository.findByItemType(itemType);
        List<Item> items = new ArrayList<Item>();
        for (ItemDB itemDB : itemDBs) {
            items.add(itemDB.toItem());
        }
        return items;
    }

    private List<Item> getAll(ItemType itemType) {
        List<ItemDB> itemDBs = itemRepository.findAll();
        List<Item> items = new ArrayList<Item>();
        for (ItemDB itemDB : itemDBs) {
            items.add(itemDB.toItem());
        }
        return items;
    }
}
