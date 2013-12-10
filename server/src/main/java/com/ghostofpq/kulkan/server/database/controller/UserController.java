package com.ghostofpq.kulkan.server.database.controller;

import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.job.Job;
import com.ghostofpq.kulkan.entities.job.JobType;
import com.ghostofpq.kulkan.entities.job.capacity.Capacity;
import com.ghostofpq.kulkan.server.database.model.GameCharacterDB;
import com.ghostofpq.kulkan.server.database.model.User;
import com.ghostofpq.kulkan.server.database.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.bson.types.ObjectId;
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
            log.error("Multiple results for authKey : [{}]", tokenKey);
        } else {
            log.error("No result for authKey : [{}]", tokenKey);
        }
        return user;
    }

    public User setNewJobForGameCharacterWithId(String tokenKey, ObjectId gameCharId, JobType newJob) {
        User user = getUserForTokenKey(tokenKey);
        if (null != user) {
            GameCharacterDB gameCharacterDB = null;
            for (GameCharacterDB teamMember : user.getTeam()) {
                if (teamMember.getId().equals(gameCharId)) {
                    gameCharacterDB = teamMember;
                    break;
                }
            }
            if (null == gameCharacterDB) {
                for (GameCharacterDB teamMember : user.getStock()) {
                    if (teamMember.getId().equals(gameCharId)) {
                        gameCharacterDB = teamMember;
                        break;
                    }
                }
            }
            gameCharacterDB.setCurrentJob(newJob);
            user = userRepository.save(user);
        }
        return user;
    }

    public User addGameCharToUser(String username, String tokenKey, GameCharacterDB gameCharacterDB) {
        User user = getUserForUsername(username);
        if (tokenKey.equals(user.getTokenKey())) {
            log.debug("addGameCharToUser : {}", username);
            if (user.getTeam().size() >= 4) {
                log.warn("TEAM IS COMPLETE");
                if (user.getStock().size() >= 12) {
                    log.error("STOCK IS COMPLETE");
                } else {
                    user.addGameCharToStock(gameCharacterDB);
                    user = userRepository.save(user);
                }
            } else {
                user.addGameCharToTeam(gameCharacterDB);
                user = userRepository.save(user);
            }
        } else {
            log.error("verification failed");
        }
        return user;
    }

    public User removeGameCharFromTeam(String username, String tokenKey, String gameCharacterName) {
        User user = getUserForUsername(username);
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
            }
            user = userRepository.save(user);
        } else {
            log.error("verification failed");
        }
        return user;
    }

    public User removeGameCharFromStock(String username, String tokenKey, String gameCharacterName) {
        User user = getUserForUsername(username);
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
            }
            user = userRepository.save(user);
        } else {
            log.error("verification failed");
        }
        return user;
    }

    public User putGameCharFromTeamToStock(String username, String tokenKey, String gameCharacterName) {
        User user = getUserForUsername(username);
        if (user.getStock().size() >= 12) {
            log.error("STOCK IS COMPLETE");
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
        if (user.getTeam().size() >= 4) {
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

    public User unlockCapacityForJobForGameCharacter(String tokenKey, String gameCharacterName, JobType jobType, String capacityName) {
        User user = getUserForTokenKey(tokenKey);
        if (null != user) {
            Player player = user.toPlayer();
            GameCharacter gameCharacterToUpdate = null;
            boolean gameCharacterIsInTeam = false;
            for (GameCharacter teamMember : player.getTeam()) {
                if (teamMember.getName().equals(gameCharacterName)) {
                    gameCharacterToUpdate = teamMember;
                    gameCharacterIsInTeam = true;
                    break;
                }
            }
            if (null != gameCharacterToUpdate) {
                for (GameCharacter teamMember : player.getStock()) {
                    if (teamMember.getName().equals(gameCharacterName)) {
                        gameCharacterToUpdate = teamMember;
                        gameCharacterIsInTeam = false;
                        break;
                    }
                }
            }
            if (null != gameCharacterToUpdate) {
                Job jobToUpdate = gameCharacterToUpdate.getJob(jobType);
                Capacity capacityToUnlock = null;
                for (Capacity capacity : jobToUpdate.getSkillTree()) {
                    if (capacity.getName().equals(capacityName)) {
                        capacityToUnlock = capacity;
                        break;
                    }
                }
                if (null != gameCharacterToUpdate) {
                    log.debug("{} unlocks {} ({} jp) for job {} of {}", player.getPseudo(), capacityToUnlock.getName(), capacityToUnlock.getPrice(), jobToUpdate.getName(), gameCharacterToUpdate.getName());
                    log.debug("JP before  : {}", jobToUpdate.getJobPoints());
                    jobToUpdate.unlockCapacity(capacityToUnlock);
                    log.debug("JP after  : {}", jobToUpdate.getJobPoints());

                    user.updateGameChar(gameCharacterToUpdate, gameCharacterIsInTeam);
                    user = userRepository.save(user);
                } else {
                    log.error("Capacity not found");
                }
            } else {
                log.error("GameChar not found");
            }
        } else {
            log.error("User not found");
        }
        return user;
    }

    public User updateGameCharacters(Player player) {
        User user = getUserForUsername(player.getPseudo());
        user.updateTeam(player.getTeam());
        user = userRepository.save(user);
        return user;
    }

    public void setTokenKeySize(Integer tokenKeySize) {
        this.tokenKeySize = tokenKeySize;
    }
}
