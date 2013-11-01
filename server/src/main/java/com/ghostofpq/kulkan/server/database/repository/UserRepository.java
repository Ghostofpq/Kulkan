package com.ghostofpq.kulkan.server.database.repository;


import com.ghostofpq.kulkan.server.database.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, Long> {
    public List<User> findByUsername(String username);
    public List<User> findByAuthKey(String authKey);
}
