package com.ghostofpq.kulkan.entities;

import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.character.PlayerRepository;
import com.mongodb.Mongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
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

    public static void main(String[] args) {
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(Tests.class);
        PlayerRepository repository = context.getBean(PlayerRepository.class);

        repository.deleteAll();

        repository.save(new Player("Bob"));


    }
}
