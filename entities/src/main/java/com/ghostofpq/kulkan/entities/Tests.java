package com.ghostofpq.kulkan.entities;

import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.character.PlayerRepository;
import com.mongodb.Mongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.net.UnknownHostException;

@Configuration
@EnableMongoRepositories
public class Tests {

    @Autowired
    PlayerRepository playerRepository;

    @Bean
    Mongo mongo() throws UnknownHostException {
        return new Mongo("localhost");
    }

    public static void main(String[] args) throws Exception {
        MongoOperations mongoOps = new MongoTemplate(new Mongo(), "kulkan");
        Player p1 = new Player("Bob");
        mongoOps.insert(p1);


    }
}
