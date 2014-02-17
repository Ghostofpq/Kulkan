package com.ghostofpq.kulkan.server.database.controller;

import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.character.Gender;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.clan.ClanType;
import com.ghostofpq.kulkan.entities.inventory.item.Item;
import com.ghostofpq.kulkan.entities.inventory.item.ItemType;
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

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemController itemController;
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

    public void removeTokenKey(String tokenKey) {
        User user = getUserForTokenKey(tokenKey);
        if (null != user) {
            user.setTokenKey("");
            user = userRepository.save(user);
        }
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
        String name = "";
        User user = getUserForTokenKey(tokenKey);
        if (null != user) {
            name = user.getUsername();
        }
        return name;
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

    public ErrorCode createGameChar(String username, String tokenKey, String name, ClanType clanType, Gender gender) {
        ErrorCode result;
        if (name.isEmpty()) {
            result = ErrorCode.NAME_IS_EMPTY;
        } else {
            User user = getUserForUsername(username);
            GameCharacter gameCharacter = new GameCharacter(name, clanType, gender);
            GameCharacterDB gameCharacterDB = new GameCharacterDB(gameCharacter);
            if (tokenKey.equals(user.getTokenKey())) {
                log.debug("createGameChar : {}", username);
                if (user.getTeam().size() >= 4) {
                    log.error("TEAM IS COMPLETE");
                    result = ErrorCode.TEAM_IS_FULL;
                } else {
                    user.addGameCharToTeam(gameCharacterDB);
                    user = userRepository.save(user);
                    result = ErrorCode.OK;
                }
            } else {
                log.error("verification failed");
                result = ErrorCode.VERIFICATION_FAILED;
            }
        }
        return result;
    }

    public User removeGameCharFromTeam(String username, String tokenKey, ObjectId gameCharacterId) {
        User user = getUserForUsername(username);
        if (tokenKey.equals(user.getTokenKey())) {
            log.debug("removeGameCharFromTeam : {}", username);
            GameCharacterDB gameCharacterDB = null;
            for (GameCharacterDB teamMember : user.getTeam()) {
                if (teamMember.getId().equals(gameCharacterId)) {
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

    public User removeGameCharFromStock(String username, String tokenKey, ObjectId gameCharacterId) {
        User user = getUserForUsername(username);
        if (tokenKey.equals(user.getTokenKey())) {
            log.debug("removeGameCharFromStock : {}", username);
            GameCharacterDB gameCharacterDB = null;
            for (GameCharacterDB stockMember : user.getStock()) {
                if (stockMember.getId().equals(gameCharacterId)) {
                    gameCharacterDB = stockMember;
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

    public User putGameCharFromTeamToStock(String username, String tokenKey, ObjectId gameCharacterId) {
        User user = getUserForUsername(username);
        if (user.getStock().size() + 1 > user.getNumberOfStockSlots()) {
            log.error("STOCK IS COMPLETE");
        } else {
            if (tokenKey.equals(user.getTokenKey())) {
                log.debug("putGameCharFromTeamToStock : {}", username);
                GameCharacterDB gameCharacterDB = null;
                for (GameCharacterDB teamMember : user.getTeam()) {
                    if (teamMember.getId().equals(gameCharacterId)) {
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

    public User putGameCharFromStockToTeam(String username, String tokenKey, ObjectId gameCharacterId) {
        User user = getUserForUsername(username);
        if (user.getTeam().size() >= 4) {
            log.error("TEAM IS COMPLETE");
        } else {
            if (tokenKey.equals(user.getTokenKey())) {
                log.debug("putGameCharFromStockToTeam : {}", username);
                GameCharacterDB gameCharacterDB = null;
                for (GameCharacterDB stockMember : user.getStock()) {
                    if (stockMember.getId().equals(gameCharacterId)) {
                        gameCharacterDB = stockMember;
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

    public User unlockCapacityForJobForGameCharacter(String tokenKey, ObjectId gameCharacterId, JobType jobType, String capacityName) {
        User user = getUserForTokenKey(tokenKey);
        if (null != user) {
            Player player = user.toPlayer();
            GameCharacter gameCharacterToUpdate = null;
            List<GameCharacter> allGameCharactersForUser = new ArrayList<GameCharacter>();
            allGameCharactersForUser.addAll(player.getTeam());
            allGameCharactersForUser.addAll(player.getStock());
            for (GameCharacter gameCharacter : allGameCharactersForUser) {
                if (gameCharacter.getId().equals(gameCharacterId)) {
                    gameCharacterToUpdate = gameCharacter;
                    break;
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
                    user.updateGameChar(gameCharacterToUpdate);
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

    public User buyItem(String tokenKey, String itemId) {
        User user = getUserForTokenKey(tokenKey);
        if (null != user) {
            Item itemToBuy = itemController.getItemById(itemId);
            if (null != itemToBuy) {
                if (itemToBuy.getPrice() <= user.getMoney()) {
                    log.debug("{} just bought a {}", user.getFirstName(), itemToBuy.getName());
                    user.setMoney(user.getMoney() - itemToBuy.getPrice());
                    user.getInventory().addOne(itemId);
                    user = userRepository.save(user);
                } else {
                    log.warn("{} can't buy a {}", user.getUsername(), itemToBuy.getName());
                }
            } else {
                log.error("Item not found");
            }
        } else {
            log.error("User not found");
        }
        return user;
    }

    public User equipItem(String tokenKey, ObjectId gameCharId, String itemId) {
        User user = getUserForTokenKey(tokenKey);
        if (null != user) {
            List<GameCharacterDB> allGameCharactersForUser = new ArrayList<GameCharacterDB>();
            allGameCharactersForUser.addAll(user.getTeam());
            allGameCharactersForUser.addAll(user.getStock());
            for (GameCharacterDB gameCharacter : allGameCharactersForUser) {
                if (gameCharacter.getId().equals(gameCharId)) {
                    Item item = itemController.getItemById(itemId);
                    user.equipItem(item, gameCharacter);
                    user = userRepository.save(user);
                    break;
                }
            }
        }
        return user;
    }

    public User unequipItem(String tokenKey, ObjectId gameCharId, ItemType itemType) {
        User user = getUserForTokenKey(tokenKey);
        if (null != user) {
            List<GameCharacterDB> allGameCharactersForUser = new ArrayList<GameCharacterDB>();
            allGameCharactersForUser.addAll(user.getTeam());
            allGameCharactersForUser.addAll(user.getStock());
            for (GameCharacterDB gameCharacter : allGameCharactersForUser) {
                if (gameCharacter.getId().equals(gameCharId)) {
                    user.unequipItem(itemType, gameCharacter);
                    user = userRepository.save(user);
                    break;
                }
            }
        }
        return user;
    }


    public User updateGameCharacters(Player player) {
        User user = getUserForUsername(player.getPseudo());
        user.updateTeam(player.getTeam());
        user = userRepository.save(user);
        return user;
    }

    public enum ErrorCode {
        OK,
        VERIFICATION_FAILED,
        NAME_IS_EMPTY,
        TEAM_IS_FULL,
        STOCK_IS_FULL;
    }

    public void setTokenKeySize(Integer tokenKeySize) {
        this.tokenKeySize = tokenKeySize;
    }
}
