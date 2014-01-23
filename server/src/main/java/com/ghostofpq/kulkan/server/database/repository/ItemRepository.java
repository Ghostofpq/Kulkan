package com.ghostofpq.kulkan.server.database.repository;

import com.ghostofpq.kulkan.entities.inventory.item.ItemType;
import com.ghostofpq.kulkan.server.database.model.ItemDB;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends MongoRepository<ItemDB, Long> {
    public List<ItemDB> findById(String id);

    public List<ItemDB> findByName(String name);
    public List<ItemDB> findByItemType(ItemType itemType);
}

