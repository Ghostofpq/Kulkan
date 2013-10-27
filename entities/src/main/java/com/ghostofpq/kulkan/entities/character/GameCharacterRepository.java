package com.ghostofpq.kulkan.entities.character;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameCharacterRepository extends MongoRepository<GameCharacter, String> {


}
