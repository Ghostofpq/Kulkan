package com.ghostofpq.kulkan.server.database.controller;

import com.ghostofpq.kulkan.server.database.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ItemController {
    @Autowired
    private ItemRepository itemRepository;
}
