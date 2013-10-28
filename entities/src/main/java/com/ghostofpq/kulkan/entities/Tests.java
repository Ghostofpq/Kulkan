package com.ghostofpq.kulkan.entities;

import com.ghostofpq.kulkan.entities.character.Player;
import com.mongodb.Mongo;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@Configuration
@EnableMongoRepositories
public class Tests {
    public static void main(String[] args) throws Exception {
        MongoOperations mongoOps = new MongoTemplate(new Mongo(), "kulkan");
        Player p1 = new Player("Bob");
        mongoOps.insert(p1);


    }
}
