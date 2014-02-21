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


    /**
     * Creates a new GameCharacter.
     *
     * @param username the user's username
     * @param tokenKey the token key known by the user's client
     * @param name     the name of the new character
     * @param clanType the clan of the new character
     * @param gender   the gender of the new character
     * @return the updated user
     * @throws InvalidUsernameException    if the username is not valid
     * @throws VerificationFailedException if the tokenKey and the actual token of user are different
     * @throws InvalidNameException        if the name is empty
     * @throws TeamIsFullException         if the team is full
     */
    public User createGameChar(String username, String tokenKey, String name, ClanType clanType, Gender gender)
            throws InvalidUsernameException, VerificationFailedException, InvalidNameException, TeamIsFullException {
        // Get user for username
        User user = getUserForUsername(username);
        if (null != user) {
            // Check if username and token key are ok
            String databaseTokenKey = user.getTokenKey();
            if (tokenKey.equals(databaseTokenKey)) {
                // Check the name
                if (!name.isEmpty()) {
                    // Check if Team is full
                    if (user.getTeam().size() < 4) {
                        // Create new game character
                        GameCharacter gameCharacter = new GameCharacter(name, clanType, gender);
                        GameCharacterDB gameCharacterDB = new GameCharacterDB(gameCharacter);
                        user.addGameCharToTeam(gameCharacterDB);
                        return userRepository.save(user);
                    } else {
                        throw new TeamIsFullException();
                    }
                } else {
                    throw new InvalidNameException(name);
                }
            } else {
                throw new VerificationFailedException(username, tokenKey, databaseTokenKey);
            }
        } else {
            throw new InvalidUsernameException(username);
        }
    }

    /**
     * Removes the GameCharacter associated with the given gameCharacterId from team.
     *
     * @param username        the user's username
     * @param tokenKey        the token key known by the user's client
     * @param gameCharacterId the id of the GameCharacter to handle
     * @return the updated user
     * @throws InvalidGameCharacterIdException if no GameCharacter is associated to the gameCharacterId
     * @throws VerificationFailedException     if the tokenKey and the actual token of user are different
     * @throws InvalidUsernameException        if the username is not valid
     */
    public User removeGameCharFromTeam(String username, String tokenKey, ObjectId gameCharacterId)
            throws InvalidGameCharacterIdException, VerificationFailedException, InvalidUsernameException {
        // Get user for username
        User user = getUserForUsername(username);
        if (null != user) {
            // Check if username and token key are ok
            String databaseTokenKey = user.getTokenKey();
            if (tokenKey.equals(databaseTokenKey)) {
                // Get the GameCharacterDB associated with gameCharacterId parameter
                GameCharacterDB gameCharacterDB = getGameCharacterDBFromTeam(user, gameCharacterId);
                if (null != gameCharacterDB) {
                    // remove GameCharacterDB from team
                    user.getTeam().remove(gameCharacterDB);
                    // Save User
                    return userRepository.save(user);
                } else {
                    throw new InvalidGameCharacterIdException(gameCharacterId.toString());
                }
            } else {
                throw new VerificationFailedException(username, tokenKey, databaseTokenKey);
            }
        } else {
            throw new InvalidUsernameException(username);
        }
    }

    /**
     * Removes the GameCharacter associated with the given gameCharacterId from stock.
     *
     * @param username        the user's username
     * @param tokenKey        the token key known by the user's client
     * @param gameCharacterId the id of the GameCharacter to handle
     * @return the updated user
     * @throws InvalidGameCharacterIdException if no GameCharacter is associated to the gameCharacterId
     * @throws VerificationFailedException     if the tokenKey and the actual token of user are different
     * @throws InvalidUsernameException        if the username is not valid
     */
    public User removeGameCharFromStock(String username, String tokenKey, ObjectId gameCharacterId)
            throws InvalidGameCharacterIdException, VerificationFailedException, InvalidUsernameException {
        // Get user for username
        User user = getUserForUsername(username);
        if (null != user) {
            // Check if username and token key are ok
            String databaseTokenKey = user.getTokenKey();
            if (tokenKey.equals(databaseTokenKey)) {
                // Get the GameCharacterDB associated with gameCharacterId parameter
                GameCharacterDB gameCharacterDB = getGameCharacterDBFromStock(user, gameCharacterId);
                if (null != gameCharacterDB) {
                    // remove GameCharacterDB from stock
                    user.getTeam().remove(gameCharacterDB);
                    // Save User
                    return userRepository.save(user);
                } else {
                    throw new InvalidGameCharacterIdException(gameCharacterId.toString());
                }
            } else {
                throw new VerificationFailedException(username, tokenKey, databaseTokenKey);
            }
        } else {
            throw new InvalidUsernameException(username);
        }
    }


    /**
     * Puts the GameCharacter associated with the given gameCharacterId from team into stock.
     *
     * @param username        the user's username
     * @param tokenKey        the token key known by the user's client
     * @param gameCharacterId the id of the GameCharacter to handle
     * @return the updated user
     * @throws VerificationFailedException     if the tokenKey and the actual token of user are different
     * @throws InvalidUsernameException        if the username is not valid
     * @throws InvalidGameCharacterIdException if no GameCharacter is associated to the gameCharacterId
     * @throws StockIsFullException            if stock is full
     */
    public User putGameCharFromTeamToStock(String username, String tokenKey, ObjectId gameCharacterId)
            throws VerificationFailedException, InvalidUsernameException, InvalidGameCharacterIdException, StockIsFullException {
        // Get user for username
        User user = getUserForUsername(username);
        if (null != user) {
            // Check if username and token key are ok
            String databaseTokenKey = user.getTokenKey();
            if (tokenKey.equals(databaseTokenKey)) {
                // Check if Stock is full
                if (user.getStock().size() + 1 <= user.getNumberOfStockSlots()) {
                    // Get the GameCharacterDB associated with gameCharacterId parameter
                    GameCharacterDB gameCharacterDB = getGameCharacterDBFromTeam(user, gameCharacterId);
                    if (null != gameCharacterDB) {
                        // Put GameCharacterDB from team to stock
                        user.getTeam().remove(gameCharacterDB);
                        user.getStock().add(gameCharacterDB);
                        // Save User
                        return userRepository.save(user);
                    } else {
                        throw new InvalidGameCharacterIdException(gameCharacterId.toString());
                    }
                } else {
                    throw new StockIsFullException();
                }
            } else {
                throw new VerificationFailedException(username, tokenKey, databaseTokenKey);
            }
        } else {
            throw new InvalidUsernameException(username);
        }
    }

    /**
     * Puts the GameCharacter associated with the given gameCharacterId from stock into team.
     *
     * @param username        the user's username
     * @param tokenKey        the token key known by the user's client
     * @param gameCharacterId the id of the GameCharacter to handle
     * @return the updated user
     * @throws VerificationFailedException     if the tokenKey and the actual token of user are different
     * @throws InvalidUsernameException        if the username is not valid
     * @throws InvalidGameCharacterIdException if no GameCharacter is associated to the gameCharacterId
     * @throws TeamIsFullException             if team is full
     */
    public User putGameCharFromStockToTeam(String username, String tokenKey, ObjectId gameCharacterId)
            throws VerificationFailedException, InvalidUsernameException, InvalidGameCharacterIdException, TeamIsFullException {
        // Get user for username
        User user = getUserForUsername(username);
        if (null != user) {
            // Check if username and token key are ok
            String databaseTokenKey = user.getTokenKey();
            if (tokenKey.equals(databaseTokenKey)) {
                // Check if Team is full
                if (user.getTeam().size() < 4) {
                    // Get the GameCharacterDB associated with gameCharacterId parameter
                    GameCharacterDB gameCharacterDB = getGameCharacterDBFromStock(user, gameCharacterId);
                    if (null != gameCharacterDB) {
                        // Put GameCharacterDB from stock to team
                        user.getStock().remove(gameCharacterDB);
                        user.getTeam().add(gameCharacterDB);
                        // Save User
                        return userRepository.save(user);
                    } else {
                        throw new InvalidGameCharacterIdException(gameCharacterId.toString());
                    }
                } else {
                    throw new TeamIsFullException();
                }
            } else {
                throw new VerificationFailedException(username, tokenKey, databaseTokenKey);
            }
        } else {
            throw new InvalidUsernameException(username);
        }
    }


    /**
     * Sets the new job to the GameCharacter associated with the given gameCharacterId.
     *
     * @param username        the user's username
     * @param tokenKey        the token key known by the user's client
     * @param gameCharacterId the id of the GameCharacter to handle
     * @param newJob          the job to set to the GameCharacter
     * @return the updated user
     * @throws InvalidGameCharacterIdException if no GameCharacter is associated to the gameCharacterId
     * @throws InvalidUsernameException        if the username is not valid
     * @throws VerificationFailedException     if the tokenKey and the actual token of user are different
     */
    public User setNewJobForGameChar(String username, String tokenKey, ObjectId gameCharacterId, JobType newJob)
            throws InvalidGameCharacterIdException, InvalidUsernameException, VerificationFailedException {
        // Get user for username
        User user = getUserForUsername(username);
        if (null != user) {
            // Check if username and token key are ok
            String databaseTokenKey = user.getTokenKey();
            if (tokenKey.equals(databaseTokenKey)) {
                // Get the GameCharacterDB associated with gameCharacterId parameter
                GameCharacterDB gameCharacterDB = getGameCharacterDB(user, gameCharacterId);
                if (null != gameCharacterDB) {
                    // Set the new Job to the GameCharacterDB
                    gameCharacterDB.setCurrentJob(newJob);
                    // Save User
                    return userRepository.save(user);
                } else {
                    throw new InvalidGameCharacterIdException(gameCharacterId.toString());
                }
            } else {
                throw new VerificationFailedException(username, tokenKey, databaseTokenKey);
            }
        } else {
            throw new InvalidUsernameException(username);
        }
    }

    /**
     * Unlocks the give Capacity of the given Job for the GameCharacter associated with the given gameCharacterId.
     *
     * @param username        the user's username
     * @param tokenKey        the token key known by the user's client
     * @param gameCharacterId the id of the GameCharacter to handle
     * @param jobType         the job to handle
     * @param capacityName    the capacity to unlock
     * @return the updated user
     * @throws InvalidGameCharacterIdException if no GameCharacter is associated to the gameCharacterId
     * @throws InvalidUsernameException        if the username is not valid
     * @throws VerificationFailedException     if the tokenKey and the actual token of user are different
     * @throws InvalidJobTypeException         if the GameCharacter can't access this job
     * @throws InvalidCapacityNameException    if no Capacity is associated to the capacityName
     */
    public User unlockCapacityForJobForGameCharacter(String username, String tokenKey, ObjectId gameCharacterId, JobType jobType, String capacityName)
            throws InvalidGameCharacterIdException, InvalidUsernameException, VerificationFailedException, InvalidJobTypeException, InvalidCapacityNameException {
        // Get user for username
        User user = getUserForUsername(username);
        if (null != user) {
            // Check if username and token key are ok
            String databaseTokenKey = user.getTokenKey();
            if (tokenKey.equals(databaseTokenKey)) {
                // Get the player from the user
                Player player = user.toPlayer();
                // Get the GameCharacter from the gameCharacterId
                GameCharacter gameCharacterToUpdate = player.getGameCharWithId(gameCharacterId);
                if (null != gameCharacterToUpdate) {
                    // Get the Job        from the jobType
                    Job jobToUpdate = gameCharacterToUpdate.getJob(jobType);
                    if (null != jobToUpdate) {
                        // Get the Capacity        from the capacityName
                        Capacity capacityToUnlock = jobToUpdate.getCapacity(capacityName);
                        if (null != gameCharacterToUpdate) {
                            // Unlock Capacity
                            gameCharacterToUpdate.unlockCapacity(jobType, capacityToUnlock);
                            // update GameCharacterDB
                            user.updateGameChar(gameCharacterToUpdate);
                            // Save User
                            return userRepository.save(user);
                        } else {
                            throw new InvalidCapacityNameException(capacityName, jobType);
                        }
                    } else {
                        throw new InvalidJobTypeException(jobType, gameCharacterId.toString());
                    }
                } else {
                    throw new InvalidGameCharacterIdException(gameCharacterId.toString());
                }
            } else {
                throw new VerificationFailedException(username, tokenKey, databaseTokenKey);
            }
        } else {
            throw new InvalidUsernameException(username);
        }
    }

    /**
     * Buys the Item associated with the given itemId for user
     *
     * @param username the user's username
     * @param tokenKey the token key known by the user's client
     * @param itemId   the id of the item to buy
     * @return the updated user
     * @throws InvalidUsernameException    if the username is not valid
     * @throws VerificationFailedException if the tokenKey and the actual token of user are different
     * @throws InvalidItemIdException      if no Item is associated to the itemId
     * @throws NotEnoughMoneyException     if the user has not enough money to buy the item
     */
    public User buyItem(String username, String tokenKey, String itemId) throws InvalidUsernameException, VerificationFailedException, InvalidItemIdException, NotEnoughMoneyException {
        // Get user for username
        User user = getUserForUsername(username);
        if (null != user) {
            // Check if username and token key are ok
            String databaseTokenKey = user.getTokenKey();
            if (tokenKey.equals(databaseTokenKey)) {
                // Get item to Buy
                Item itemToBuy = itemController.getItemById(itemId);
                if (null != itemToBuy) {
                    if (itemToBuy.getPrice() <= user.getMoney()) {
                        // Update money
                        user.setMoney(user.getMoney() - itemToBuy.getPrice());
                        // Add item
                        user.getInventory().addOne(itemId);
                        // Save User
                        return userRepository.save(user);
                    } else {
                        throw new NotEnoughMoneyException();
                    }
                } else {
                    throw new InvalidItemIdException(itemId);
                }
            } else {
                throw new VerificationFailedException(username, tokenKey, databaseTokenKey);
            }
        } else {
            throw new InvalidUsernameException(username);
        }
    }

    /**
     * Equips the GameCharacter associated with the given gameCharacterId with the Item associated with the given itemId
     *
     * @param username        the user's username
     * @param tokenKey        the token key known by the user's client
     * @param gameCharacterId the id of the GameCharacter to equip
     * @param itemId          the id of the item to equip
     * @return the updated user
     * @throws InvalidUsernameException        if the username is not valid
     * @throws VerificationFailedException     if the tokenKey and the actual token of user are different
     * @throws InvalidGameCharacterIdException if no GameCharacter is associated to the gameCharacterId
     * @throws InvalidItemIdException          if no Item is associated to the itemId
     */
    public User equipItem(String username, String tokenKey, ObjectId gameCharacterId, String itemId)
            throws InvalidUsernameException, VerificationFailedException, InvalidGameCharacterIdException, InvalidItemIdException {
        // Get user for username
        User user = getUserForUsername(username);
        if (null != user) {
            // Check if username and token key are ok
            String databaseTokenKey = user.getTokenKey();
            if (tokenKey.equals(databaseTokenKey)) {
                // Get the GameCharacterDB associated with gameCharacterId parameter
                GameCharacterDB gameCharacterDB = getGameCharacterDBFromStock(user, gameCharacterId);
                if (null != gameCharacterDB) {
                    Item item = itemController.getItemById(itemId);
                    if (null != item) {
                        user.equipItem(item, gameCharacterDB);
                        return userRepository.save(user);
                    } else {
                        throw new InvalidItemIdException(itemId);
                    }
                } else {
                    throw new InvalidGameCharacterIdException(gameCharacterId.toString());
                }
            } else {
                throw new VerificationFailedException(username, tokenKey, databaseTokenKey);
            }
        } else {
            throw new InvalidUsernameException(username);
        }
    }

    /**
     * Unequips the given ItemType of the GameCharacter associated with the given gameCharacterId
     *
     * @param username        the user's username
     * @param tokenKey        the token key known by the user's client
     * @param gameCharacterId the id of the GameCharacter to equip
     * @param itemType        the inventory slot to clear
     * @return the updated user
     * @throws InvalidUsernameException        if the username is not valid
     * @throws VerificationFailedException     if the tokenKey and the actual token of user are different
     * @throws InvalidGameCharacterIdException if no GameCharacter is associated to the gameCharacterId
     */
    public User unequipItem(String username, String tokenKey, ObjectId gameCharacterId, ItemType itemType)
            throws InvalidUsernameException, VerificationFailedException, InvalidGameCharacterIdException {
        User user = getUserForUsername(username);
        if (null != user) {
            // Check if username and token key are ok
            String databaseTokenKey = user.getTokenKey();
            if (tokenKey.equals(databaseTokenKey)) {
                // Get the GameCharacterDB associated with gameCharacterId parameter
                GameCharacterDB gameCharacterDB = getGameCharacterDBFromStock(user, gameCharacterId);
                if (null != gameCharacterDB) {
                    user.unequipItem(itemType, gameCharacterDB);
                    return userRepository.save(user);
                } else {
                    throw new InvalidGameCharacterIdException(gameCharacterId.toString());
                }
            } else {
                throw new VerificationFailedException(username, tokenKey, databaseTokenKey);
            }
        } else {
            throw new InvalidUsernameException(username);
        }
    }


    public User updateGameCharacters(Player player) {
        User user = getUserForUsername(player.getPseudo());
        user.updateTeam(player.getTeam());
        user = userRepository.save(user);
        return user;
    }


    //GETTERS

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

    private GameCharacterDB getGameCharacterDB(User user, ObjectId gameCharacterId) {
        GameCharacterDB gameCharacterDB = getGameCharacterDBFromTeam(user, gameCharacterId);
        if (null == gameCharacterDB) {
            gameCharacterDB = getGameCharacterDBFromStock(user, gameCharacterId);
        }
        return gameCharacterDB;
    }

    //SPRING

    public void setTokenKeySize(Integer tokenKeySize) {
        this.tokenKeySize = tokenKeySize;
    }

    // EXCEPTIONS

    public class InvalidUsernameException extends Exception {
        public InvalidUsernameException(String username) {
            super(String.format("Username [%1$s] was not found.", username));
        }
    }

    public class VerificationFailedException extends Exception {
        public VerificationFailedException(String username, String givenTokenKey, String actualTokenKey) {
            super(String.format("Given tokenKey (%1$s) doesn't match actual tokenKey for user [%2$s] (%3$s)", givenTokenKey, username, actualTokenKey));
        }
    }

    public class TeamIsFullException extends Exception {
        public TeamIsFullException() {
            super("Team is full. Put some of your characters into stock.");
        }
    }

    public class StockIsFullException extends Exception {
        public StockIsFullException() {
            super("Stock is full. Please buy more slots.");
        }
    }

    public class InvalidGameCharacterIdException extends Exception {
        public InvalidGameCharacterIdException(String gameCharacterId) {
            super(String.format("Game Character with id [%1$s] was not found.", gameCharacterId));
        }
    }

    public class InvalidJobTypeException extends Exception {
        public InvalidJobTypeException(JobType jobType, String gameCharacterName) {
            super(String.format("Job [%1$s] is not available for GameCharacter [%2$s].", jobType, gameCharacterName));
        }
    }

    public class InvalidCapacityNameException extends Exception {
        public InvalidCapacityNameException(String CapacityName, JobType jobType) {
            super(String.format("Capacity [%1$s] is not available for Job [%2$s].", CapacityName, jobType));
        }
    }

    public class InvalidNameException extends Exception {
        public InvalidNameException(String name) {
            super(String.format("[%1$s] is not a valid Name.", name));
        }
    }

    public class InvalidItemIdException extends Exception {
        public InvalidItemIdException(String itemId) {
            super(String.format("Item with id [%1$s] was not found.", itemId));
        }
    }

    public class NotEnoughMoneyException extends Exception {
        public NotEnoughMoneyException() {
            super("You have not enough money.");
        }
    }

}
