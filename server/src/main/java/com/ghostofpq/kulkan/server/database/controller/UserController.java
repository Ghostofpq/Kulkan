package com.ghostofpq.kulkan.server.database.controller;

import com.ghostofpq.kulkan.server.database.model.GameCharacterDB;
import com.ghostofpq.kulkan.server.database.model.User;
import com.ghostofpq.kulkan.server.database.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class UserController {
    @Autowired
    private UserRepository userRepository;
    private Integer tokenKeySize;

    public User generateTokenKeyForUser(User user) {
        String newTokenKey = RandomStringUtils.randomNumeric(tokenKeySize);
        while (getUserForTokenKey(newTokenKey) != null) {
            log.error("key [{}] is already in use", newTokenKey);
            newTokenKey = RandomStringUtils.randomNumeric(tokenKeySize);
        }
        user.setTokenKey(newTokenKey);
        user = userRepository.save(user);
        return user;
    }

    public String getTokenKeyForUsername(String username) {
        String user = getUserForUsername(username).getTokenKey();
        return user;
    }

    public User getUserForUsername(String username) {
        User user = null;
        List<User> userList = userRepository.findByUsername(username);
        if (userList.size() == 1) {
            user = userList.get(0);
        } else if (userList.size() > 1) {
            log.error("multiple results for username : [{}]", username);
        } else {
            log.error("no result for username : [{}]", username);
        }
        return user;
    }

    public String getNameForTokenKey(String tokenKey) {
        String user = getUserForTokenKey(tokenKey).getUsername();
        return user;
    }

    public User getUserForTokenKey(String tokenKey) {
        User user = null;
        List<User> userList = userRepository.findByTokenKey(tokenKey);
        if (userList.size() == 1) {
            user = userList.get(0);
        } else if (userList.size() > 1) {
            log.error("multiple results for authKey : [{}]", tokenKey);
        } else {
            log.error("no result for authKey : [{}]", tokenKey);
        }
        return user;
    }

    public User addGameCharToUser(String username, String tokenKey, GameCharacterDB gameCharacterDB) {
        User user = getUserForUsername(username);
        if (tokenKey.equals(user.getTokenKey())) {
            log.debug("addGameCharToUser : {}", username);
            user.addGameCharToTeam(gameCharacterDB);
            user = userRepository.save(user);
        } else {
            log.error("verification failed");
        }
        return user;
    }

    public User putGameCharFromTeamToStock(String username, String tokenKey, String gameCharacterName) {
        User user = getUserForUsername(username);
        if (user.getStock().size() == 10) {
            log.error("TEAM IS COMPLETE");
        } else {
            if (tokenKey.equals(user.getTokenKey())) {
                log.debug("addGameCharToUser : {}", username);
                GameCharacterDB gameCharacterDB = null;
                for (GameCharacterDB teamMember : user.getTeam()) {
                    if (teamMember.getName().equals(gameCharacterName)) {
                        gameCharacterDB = teamMember;
                        break;
                    }
                }
                if (null != gameCharacterDB) {
                    user.getTeam().remove(gameCharacterDB);
                    user.getStock().add(gameCharacterDB);
                }
                user = userRepository.save(user);
            } else {
                log.error("verification failed");
            }
        }
        return user;
    }

    public User putGameCharFromStockToTeam(String username, String tokenKey, String gameCharacterName) {
        User user = getUserForUsername(username);
        if (user.getTeam().size() == 4) {
            log.error("TEAM IS COMPLETE");
        } else {
            if (tokenKey.equals(user.getTokenKey())) {
                log.debug("addGameCharToUser : {}", username);
                GameCharacterDB gameCharacterDB = null;
                for (GameCharacterDB teamMember : user.getStock()) {
                    if (teamMember.getName().equals(gameCharacterName)) {
                        gameCharacterDB = teamMember;
                        break;
                    }
                }
                if (null != gameCharacterDB) {
                    user.getStock().remove(gameCharacterDB);
                    user.getTeam().add(gameCharacterDB);
                }
                user = userRepository.save(user);
            } else {
                log.error("verification failed");
            }
        }
        return user;
    }

    public void setTokenKeySize(Integer tokenKeySize) {
        this.tokenKeySize = tokenKeySize;
    }
}
