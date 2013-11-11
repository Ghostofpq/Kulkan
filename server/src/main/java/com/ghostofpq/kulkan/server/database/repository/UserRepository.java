package com.ghostofpq.kulkan.server.database.repository;

import com.ghostofpq.kulkan.server.database.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {
    public List<User> findByUsername(String username);

    public List<User> findByTokenKey(String tokenKey);
}
