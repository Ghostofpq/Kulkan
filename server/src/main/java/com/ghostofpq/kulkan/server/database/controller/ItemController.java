package com.ghostofpq.kulkan.server.database.controller;

import com.ghostofpq.kulkan.entities.inventory.ItemFactory;
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

    public Item getItemById(String id) {
        List<ItemDB> itemDBs = itemRepository.findById(id);
        Item item = null;
        if (itemDBs.size() == 1) {
            item = itemDBs.get(0).toItem();
        } else if (itemDBs.size() > 1) {
            log.error("multiple results for item id : [{}]", id);
        } else {
            log.error("no result for item id : [{}]", id);
        }
        return item;
    }

    public ItemDB getItemDBById(String id) {
        List<ItemDB> itemDBs = itemRepository.findByName(id);
        ItemDB itemDB = null;
        if (itemDBs.size() == 1) {
            itemDB = itemDBs.get(0);
        } else if (itemDBs.size() > 1) {
            log.error("multiple results for item id : [{}]", id);
        } else {
            log.error("no result for item id : [{}]", id);
        }
        return itemDB;
    }

    public Item getItemByName(String name) {
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

    public ItemDB getItemDBByName(String name) {
        List<ItemDB> itemDBs = itemRepository.findByName(name);
        ItemDB itemDB = null;
        if (itemDBs.size() == 1) {
            itemDB = itemDBs.get(0);
        } else if (itemDBs.size() > 1) {
            log.error("multiple results for item name : [{}]", name);
        } else {
            log.error("no result for item name : [{}]", name);
        }
        return itemDB;
    }

    public List<Item> getByItemType(ItemType itemType) {
        List<ItemDB> itemDBs = itemRepository.findByItemType(itemType);
        List<Item> items = new ArrayList<Item>();
        for (ItemDB itemDB : itemDBs) {
            items.add(itemDB.toItem());
        }
        return items;
    }

    public List<Item> getAll(ItemType itemType) {
        List<ItemDB> itemDBs = itemRepository.findAll();
        List<Item> items = new ArrayList<Item>();
        for (ItemDB itemDB : itemDBs) {
            items.add(itemDB.toItem());
        }
        return items;
    }

    public void populateItemRepository() {
        List<Item> items = new ArrayList<Item>();
        items.add(ItemFactory.createItem("000"));
        items.add(ItemFactory.createItem("001"));
        items.add(ItemFactory.createItem("002"));
        items.add(ItemFactory.createItem("003"));
        items.add(ItemFactory.createItem("004"));
        items.add(ItemFactory.createItem("005"));
        items.add(ItemFactory.createItem("006"));
        items.add(ItemFactory.createItem("007"));
        items.add(ItemFactory.createItem("008"));
        items.add(ItemFactory.createItem("009"));
        items.add(ItemFactory.createItem("010"));

        for (Item item : items) {
            ItemDB newItemDB = new ItemDB(item);
            itemRepository.save(newItemDB);
        }
    }
}
