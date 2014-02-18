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

    private GameCharacterDB getGameCharacterDBFromTeam(User user, ObjectId gameCharacterId) {
        GameCharacterDB gameCharacterDB = null;
        for (GameCharacterDB teamMember : user.getTeam()) {
            if (teamMember.getId().equals(gameCharacterId)) {
                gameCharacterDB = teamMember;
                break;
            }
        }
        return gameCharacterDB;
    }

    private GameCharacterDB getGameCharacterDBFromStock(User user, ObjectId gameCharacterId) {
        GameCharacterDB gameCharacterDB = null;
        for (GameCharacterDB stockMember : user.getStock()) {
            if (stockMember.getId().equals(gameCharacterId)) {
                gameCharacterDB = stockMember;
                break;
            }
        }
        return gameCharacterDB;
    }

    public GameCharacterDB getGameCharacterDB(User user, ObjectId gameCharacterId) {
        GameCharacterDB gameCharacterDB = getGameCharacterDBFromTeam(user, gameCharacterId);
        if (null == gameCharacterDB) {
            gameCharacterDB = getGameCharacterDBFromStock(user, gameCharacterId);
        }
        return gameCharacterDB;
    }

    /**
     * Removes the GameCharacter associated with the given gameCharacterId from team.
     *
     * @param tokenKey        the token key known by the user's client
     * @param gameCharacterId the id of the GameCharacter to handle
     * @param newJob          the job to set to the GameCharacter
     * @return ErrorCode.OK if everything went well<br/>
     * ErrorCode.VERIFICATION_FAILED if the tokenKey was not found<br/>
     * ErrorCode.GAME_CHARACTER_WAS_NOT_FOUND if no GameCharacter is associated to the gameCharacterId<br/>
     */
    public ErrorCode setNewJobForGameChar(String tokenKey, ObjectId gameCharacterId, JobType newJob) {
        ErrorCode result;
        // Get user for username
        User user = getUserForTokenKey(tokenKey);
        if (null != user) {
            // Check if username and token key are ok
            // Get the GameCharacterDB associated with gameCharacterId parameter
            GameCharacterDB gameCharacterDB = getGameCharacterDB(user, gameCharacterId);
            if (null != gameCharacterDB) {
                // Set the new Job to the GameCharacterDB
                gameCharacterDB.setCurrentJob(newJob);
                // Save User
                userRepository.save(user);
                result = ErrorCode.OK;
            } else {
                result = ErrorCode.GAME_CHARACTER_WAS_NOT_FOUND;
            }
        } else {
            result = ErrorCode.VERIFICATION_FAILED;
        }
        return result;
    }

    /**
     * Creates a new GameCharacter.
     *
     * @param username the user's username
     * @param tokenKey the token key known by the user's client
     * @param name     the name of the new character
     * @param clanType the clan of the new character
     * @param gender   the gender of the new character
     * @return ErrorCode.OK if everything went well<br/>
     * ErrorCode.VERIFICATION_FAILED if the tokenKey and the actual token of user are different<br/>
     * ErrorCode.USERNAME_INVALID if the username is not valid<br/>
     * ErrorCode.NAME_IS_EMPTY if the name is empty<br/>
     * ErrorCode.TEAM_IS_FULL if the team is full<br/>
     */
    public ErrorCode createGameChar(String username, String tokenKey, String name, ClanType clanType, Gender gender) {
        ErrorCode result;
        // Get user for username
        User user = getUserForUsername(username);
        if (null != user) {
            // Check if username and token key are ok
            if (tokenKey.equals(user.getTokenKey())) {
                // Check the name
                if (!name.isEmpty()) {
                    // Check if Team is full
                    if (user.getTeam().size() < 4) {
                        // Create new game character
                        GameCharacter gameCharacter = new GameCharacter(name, clanType, gender);
                        GameCharacterDB gameCharacterDB = new GameCharacterDB(gameCharacter);
                        user.addGameCharToTeam(gameCharacterDB);
                        userRepository.save(user);
                        result = ErrorCode.OK;
                    } else {
                        result = ErrorCode.TEAM_IS_FULL;
                    }
                } else {
                    result = ErrorCode.NAME_IS_EMPTY;
                }
            } else {
                result = ErrorCode.VERIFICATION_FAILED;
            }
        } else {
            result = ErrorCode.USERNAME_INVALID;
        }
        return result;
    }

    /**
     * Removes the GameCharacter associated with the given gameCharacterId from team.
     *
     * @param username        the user's username
     * @param tokenKey        the token key known by the user's client
     * @param gameCharacterId the id of the GameCharacter to handle
     * @return ErrorCode.OK if everything went well<br/>
     * ErrorCode.VERIFICATION_FAILED if the tokenKey and the actual token of user are different<br/>
     * ErrorCode.USERNAME_INVALID if the username is not valid<br/>
     * ErrorCode.GAME_CHARACTER_WAS_NOT_FOUND if no GameCharacter is associated to the gameCharacterId<br/>
     */
    public ErrorCode removeGameCharFromTeam(String username, String tokenKey, ObjectId gameCharacterId) {
        ErrorCode result;
        // Get user for username
        User user = getUserForUsername(username);
        if (null != user) {
            // Check if username and token key are ok
            if (tokenKey.equals(user.getTokenKey())) {
                // Get the GameCharacterDB associated with gameCharacterId parameter
                GameCharacterDB gameCharacterDB = getGameCharacterDBFromTeam(user, gameCharacterId);
                if (null != gameCharacterDB) {
                    // remove GameCharacterDB from team
                    user.getTeam().remove(gameCharacterDB);
                    // Save User
                    userRepository.save(user);
                    result = ErrorCode.OK;
                } else {
                    result = ErrorCode.GAME_CHARACTER_WAS_NOT_FOUND;
                }
            } else {
                result = ErrorCode.VERIFICATION_FAILED;
            }
        } else {
            result = ErrorCode.USERNAME_INVALID;
        }
        return result;
    }

    /**
     * Removes the GameCharacter associated with the given gameCharacterId from stock.
     *
     * @param username        the user's username
     * @param tokenKey        the token key known by the user's client
     * @param gameCharacterId the id of the GameCharacter to handle
     * @return ErrorCode.OK if everything went well<br/>
     * ErrorCode.VERIFICATION_FAILED if the tokenKey and the actual token of user are different<br/>
     * ErrorCode.USERNAME_INVALID if the username is not valid<br/>
     * ErrorCode.GAME_CHARACTER_WAS_NOT_FOUND if no GameCharacter is associated to the gameCharacterId<br/>
     */
    public ErrorCode removeGameCharFromStock(String username, String tokenKey, ObjectId gameCharacterId) {
        ErrorCode result;
        // Get user for username
        User user = getUserForUsername(username);
        if (null != user) {
            // Check if username and token key are ok
            if (tokenKey.equals(user.getTokenKey())) {
                // Get the GameCharacterDB associated with gameCharacterId parameter
                GameCharacterDB gameCharacterDB = getGameCharacterDBFromStock(user, gameCharacterId);
                if (null != gameCharacterDB) {
                    // remove GameCharacterDB from stock
                    user.getTeam().remove(gameCharacterDB);
                    // Save User
                    userRepository.save(user);
                    result = ErrorCode.OK;
                } else {
                    result = ErrorCode.GAME_CHARACTER_WAS_NOT_FOUND;
                }
            } else {
                result = ErrorCode.VERIFICATION_FAILED;
            }
        } else {
            result = ErrorCode.USERNAME_INVALID;
        }
        return result;
    }


    /**
     * Puts the GameCharacter associated with the given gameCharacterId from team into stock.
     *
     * @param username        the user's username
     * @param tokenKey        the token key known by the user's client
     * @param gameCharacterId the id of the GameCharacter to handle
     * @return ErrorCode.OK if everything went well<br/>
     * ErrorCode.VERIFICATION_FAILED if the tokenKey and the actual token of user are different<br/>
     * ErrorCode.USERNAME_INVALID if the username is not valid<br/>
     * ErrorCode.STOCK_IS_FULL if stock is full<br/>
     * ErrorCode.GAME_CHARACTER_WAS_NOT_FOUND if no GameCharacter is associated to the gameCharacterId<br/>
     */
    public ErrorCode putGameCharFromTeamToStock(String username, String tokenKey, ObjectId gameCharacterId) {
        ErrorCode result;
        // Get user for username
        User user = getUserForUsername(username);
        if (null != user) {
            // Check if username and token key are ok
            if (tokenKey.equals(user.getTokenKey())) {
                // Check if Stock is full
                if (user.getStock().size() + 1 <= user.getNumberOfStockSlots()) {
                    // Get the GameCharacterDB associated with gameCharacterId parameter
                    GameCharacterDB gameCharacterDB = getGameCharacterDBFromTeam(user, gameCharacterId);
                    if (null != gameCharacterDB) {
                        // Put GameCharacterDB from team to stock
                        user.getTeam().remove(gameCharacterDB);
                        user.getStock().add(gameCharacterDB);
                        // Save User
                        userRepository.save(user);
                        result = ErrorCode.OK;
                    } else {
                        result = ErrorCode.GAME_CHARACTER_WAS_NOT_FOUND;
                    }
                } else {
                    result = ErrorCode.STOCK_IS_FULL;
                }
            } else {
                result = ErrorCode.VERIFICATION_FAILED;
            }
        } else {
            result = ErrorCode.USERNAME_INVALID;
        }
        return result;
    }

    /**
     * Puts the GameCharacter associated with the given gameCharacterId from stock into team.
     *
     * @param username        the user's username
     * @param tokenKey        the token key known by the user's client
     * @param gameCharacterId the id of the GameCharacter to handle
     * @return ErrorCode.OK if everything went well<br/>
     * ErrorCode.VERIFICATION_FAILED if the tokenKey and the actual token of user are different<br/>
     * ErrorCode.USERNAME_INVALID if the username is not valid<br/>
     * ErrorCode.TEAM_IS_FULL if team is full<br/>
     * ErrorCode.GAME_CHARACTER_WAS_NOT_FOUND if no GameCharacter is associated to the gameCharacterId<br/>
     */
    public ErrorCode putGameCharFromStockToTeam(String username, String tokenKey, ObjectId gameCharacterId) {
        ErrorCode result;
        // Get user for username
        User user = getUserForUsername(username);
        if (null != user) {
            // Check if username and token key are ok
            if (tokenKey.equals(user.getTokenKey())) {
                // Check if Team is full
                if (user.getTeam().size() <= 4) {
                    // Get the GameCharacterDB associated with gameCharacterId parameter
                    GameCharacterDB gameCharacterDB = getGameCharacterDBFromStock(user, gameCharacterId);
                    if (null != gameCharacterDB) {
                        // Put GameCharacterDB from stock to team
                        user.getStock().remove(gameCharacterDB);
                        user.getTeam().add(gameCharacterDB);
                        // Save User
                        userRepository.save(user);
                        result = ErrorCode.OK;
                    } else {
                        result = ErrorCode.GAME_CHARACTER_WAS_NOT_FOUND;
                    }
                } else {
                    result = ErrorCode.TEAM_IS_FULL;
                }
            } else {
                result = ErrorCode.VERIFICATION_FAILED;
            }
        } else {
            result = ErrorCode.USERNAME_INVALID;
        }
        return result;
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
        USERNAME_INVALID,
        VERIFICATION_FAILED,
        NAME_IS_EMPTY,
        GAME_CHARACTER_WAS_NOT_FOUND,
        TEAM_IS_FULL,
        STOCK_IS_FULL;
    }

    public void setTokenKeySize(Integer tokenKeySize) {
        this.tokenKeySize = tokenKeySize;
    }
}
