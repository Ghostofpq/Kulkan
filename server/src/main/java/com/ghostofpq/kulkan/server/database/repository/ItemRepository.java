package com.ghostofpq.kulkan.server.database.repository;

import com.ghostofpq.kulkan.entities.inventory.item.Item;
import com.ghostofpq.kulkan.entities.inventory.item.ItemType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends MongoRepository<Item, Long> {
    public List<Item> findByName(String name);

    public List<Item> findByItemType(ItemType itemType);
}

