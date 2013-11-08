package com.ghostofpq.kulkan.server.database.controller;

import com.ghostofpq.kulkan.server.database.model.User;
import com.ghostofpq.kulkan.server.database.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class UserController {
    @Autowired
    private UserRepository userRepositoryRepository;
    private Integer tokenKeySize;

    public String getTokenKeyForUsername(String username) {
        String user = getUserForUsername(username).getTokenKey();
        return user;
    }

    public String getNameForTokenKey(String tokenKey) {
        String user = getUserForTokenKey(tokenKey).getUsername();
        return user;
    }

    public String generateTokenKeyForUser(User user) {
        String newTokenKey = RandomStringUtils.randomNumeric(tokenKeySize);
        while (!getNameForTokenKey(newTokenKey).equals("")) {
            log.error("Key [{}] is already in use", newTokenKey);
            newTokenKey = RandomStringUtils.randomNumeric(tokenKeySize);
        }
        user.setTokenKey(newTokenKey);
        userRepositoryRepository.save(user);
        return newTokenKey;
    }

    public User getUserForUsername(String username) {
        User user = null;
        List<User> userList = userRepositoryRepository.findByUsername(username);
        if (userList.size() == 1) {
            user = userList.get(0);
        } else if (userList.size() > 1) {
            log.error("multiple results for username : [{}]", username);
        } else {
            log.error("no result for username : [{}]", username);
        }
        return user;
    }

    public User getUserForTokenKey(String tokenKey) {
        User user = null;
        List<User> userList = userRepositoryRepository.findByAuthKey(tokenKey);
        if (userList.size() == 1) {
            user = userList.get(0);
        } else if (userList.size() > 1) {
            log.error("multiple results for authKey : [{}]", tokenKey);
        } else {
            log.error("no result for authKey : [{}]", tokenKey);
        }
        return user;
    }

    public void setTokenKeySize(Integer tokenKeySize) {
        this.tokenKeySize = tokenKeySize;
    }
}
