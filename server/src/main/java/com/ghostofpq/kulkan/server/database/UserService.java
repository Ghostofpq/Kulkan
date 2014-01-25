package com.ghostofpq.kulkan.server.database;

import com.ghostofpq.kulkan.entities.character.Gender;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.clan.ClanType;
import com.ghostofpq.kulkan.entities.inventory.item.ItemType;
import com.ghostofpq.kulkan.entities.job.JobType;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.user.*;
import com.ghostofpq.kulkan.server.database.controller.UserController;
import com.ghostofpq.kulkan.server.database.model.GameCharacterDB;
import com.ghostofpq.kulkan.server.database.model.JobStatusDB;
import com.ghostofpq.kulkan.server.database.model.User;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class UserService implements Runnable {
    private static final String CLIENT_QUEUE_NAME_BASE = "client/";
    private final String serviceQueueName = "server/users";
    // PARAMETERS - SPRING
    private String hostIp;
    private Integer hostPort;
    private Integer authKeySize;
    @Autowired
    private UserController userController;
    // MESSAGING
    private QueueingConsumer consumer;
    private Connection connection;
    private Channel channelServiceIn;
    private Channel channelServiceOut;
    // THREAD ROUTINE
    private boolean requestClose;

    private UserService() {
        requestClose = false;
    }

    public void initConnection() throws IOException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostIp);
        factory.setPort(hostPort);
        log.debug("{}:{}", hostIp, hostPort);
        connection = factory.newConnection();
        channelServiceIn = connection.createChannel();
        channelServiceIn.queueDeclare(serviceQueueName, false, false, false, null);
        channelServiceIn.basicQos(1);
        consumer = new QueueingConsumer(channelServiceIn);
        channelServiceIn.basicConsume(serviceQueueName, true, consumer);
        channelServiceOut = connection.createChannel();
    }

    private void receiveMessage() throws InterruptedException, IOException {
        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
        if (null != delivery) {
            Message message = Message.loadFromBytes(delivery.getBody());
            log.debug(" [x] RECEIVED '{}' ON {}", message.getType(), serviceQueueName);
            switch (message.getType()) {
                case CREATE_NEW_GAME_CHARACTER_REQUEST:
                    manageCreateGameCharacterRequest(message);
                    break;
                case PUT_GAME_CHARACTER_FROM_STOCK_TO_TEAM_REQUEST:
                    managePutGameCharacterFromStockToTeam(message);
                    break;
                case PUT_GAME_CHARACTER_FROM_TEAM_TO_STOCK_REQUEST:
                    managePutGameCharacterFromTeamToStockRequest(message);
                    break;
                case DELETE_GAME_CHARACTER_FROM_STOCK_REQUEST:
                    manageDeleteGameCharacterFromStockRequest(message);
                    break;
                case DELETE_GAME_CHARACTER_FROM_TEAM_REQUEST:
                    manageDeleteGameCharacterFromTeamRequest(message);
                    break;
                case CHARACTER_UNLOCK_CAPACITY:
                    manageUnlockCapacityForGameCharacterRequest(message);
                    break;
                case CHARACTER_CHANGE_JOB:
                    manageChangeJobRequest(message);
                    break;
                case BUY_ITEM_REQUEST:
                    manageBuyItem(message);
                    break;
                case EQUIP_ITEM:
                    manageEquipItem(message);
                    break;
                case UNEQUIP_ITEM:
                    manageUnequipItem(message);
                    break;

                default:
                    log.error(" [X] UNEXPECTED MESSAGE : {}", message.getType());
                    break;
            }
        }
    }

    private void manageEquipItem(Message message) throws IOException {
        MessageEquipItemOnGameCharacter messageEquipItemOnGameCharacter = (MessageEquipItemOnGameCharacter) message;

        String tokenKey = messageEquipItemOnGameCharacter.getKeyToken();
        ObjectId gameCharId = messageEquipItemOnGameCharacter.getGameCharId();
        String itemId = messageEquipItemOnGameCharacter.getItemId();

        log.debug("Received a EquipItem");
        log.debug("TokenKey : '{}'", tokenKey);
        log.debug("GameCharId : '{}'", gameCharId);
        log.debug("ItemId : '{}'", itemId);

        if (null != tokenKey && null != gameCharId && null != itemId) {
            User user = userController.equipItem(tokenKey, gameCharId, itemId);
            Player player = user.toPlayer();
            MessagePlayerUpdate messagePlayerUpdate = new MessagePlayerUpdate(player);
            String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(tokenKey).toString();
            channelServiceOut.queueDeclare(queueName, false, false, false, null);
            channelServiceOut.basicPublish("", queueName, null, messagePlayerUpdate.getBytes());
        }
    }

    private void manageUnequipItem(Message message) throws IOException {
        MessageUnequipItemOnGameCharacter messageUnequipItemOnGameCharacter = (MessageUnequipItemOnGameCharacter) message;

        String tokenKey = messageUnequipItemOnGameCharacter.getKeyToken();
        ObjectId gameCharId = messageUnequipItemOnGameCharacter.getGameCharId();
        ItemType itemType = messageUnequipItemOnGameCharacter.getItemType();

        log.debug("Received a UnequipItemRequest");
        log.debug("TokenKey : '{}'", tokenKey);
        log.debug("GameCharId : '{}'", gameCharId);
        log.debug("ItemType : '{}'", itemType);
        if (null != tokenKey && null != gameCharId && null != itemType) {
            User user = userController.unequipItem(tokenKey, gameCharId, itemType);
            Player player = user.toPlayer();
            MessagePlayerUpdate messagePlayerUpdate = new MessagePlayerUpdate(player);
            String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(tokenKey).toString();
            channelServiceOut.queueDeclare(queueName, false, false, false, null);
            channelServiceOut.basicPublish("", queueName, null, messagePlayerUpdate.getBytes());
        }
    }

    private void manageBuyItem(Message message) throws IOException {
        MessageBuyItem messageBuyItem = (MessageBuyItem) message;

        String tokenKey = messageBuyItem.getKeyToken();
        String itemId = messageBuyItem.getItemId();

        log.debug("Received a BuyItemRequest");
        log.debug("TokenKey : '{}'", tokenKey);
        log.debug("ItemId : '{}'", itemId);
        if (null != tokenKey && null != itemId) {
            User user = userController.buyItem(tokenKey, itemId);
            Player player = user.toPlayer();
            MessagePlayerUpdate messagePlayerUpdate = new MessagePlayerUpdate(player);
            String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(tokenKey).toString();
            channelServiceOut.queueDeclare(queueName, false, false, false, null);
            channelServiceOut.basicPublish("", queueName, null, messagePlayerUpdate.getBytes());
        }
    }

    private void manageChangeJobRequest(Message message) throws IOException {
        MessageChangeJob messageChangeJob = (MessageChangeJob) message;

        String tokenKey = messageChangeJob.getKeyToken();
        ObjectId gameCharId = messageChangeJob.getGameCharId();
        JobType newJob = messageChangeJob.getNewJob();

        log.debug("Received a DeleteGameChararacterFromTeamRequest");
        log.debug("TokenKey : '{}'", tokenKey);
        log.debug("GameCharId : '{}'", gameCharId);
        log.debug("NewJob : '{}'", newJob);
        if (null != tokenKey && null != gameCharId && null != newJob) {
            User user = userController.setNewJobForGameCharacterWithId(tokenKey, gameCharId, newJob);
            Player player = user.toPlayer();
            MessagePlayerUpdate messagePlayerUpdate = new MessagePlayerUpdate(player);
            String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(tokenKey).toString();
            channelServiceOut.queueDeclare(queueName, false, false, false, null);
            channelServiceOut.basicPublish("", queueName, null, messagePlayerUpdate.getBytes());
        }
    }

    private void manageDeleteGameCharacterFromTeamRequest(Message message) throws IOException {
        MessageDeleteGameCharacterFromTeam messageDeleteGameCharacterFromTeam = (MessageDeleteGameCharacterFromTeam) message;

        String username = messageDeleteGameCharacterFromTeam.getUsername();
        String tokenKey = messageDeleteGameCharacterFromTeam.getKeyToken();
        ObjectId gameCharId = messageDeleteGameCharacterFromTeam.getGameCharId();

        log.debug("Received a DeleteGameChararacterFromTeamRequest");
        log.debug("Username : '{}'", username);
        log.debug("TokenKey : '{}'", tokenKey);
        log.debug("GameCharId : '{}'", gameCharId);

        if (null != tokenKey && null != username && null != gameCharId) {
            if (!tokenKey.isEmpty() && !username.isEmpty()) {
                User user = userController.removeGameCharFromTeam(username, tokenKey, gameCharId);
                Player player = user.toPlayer();
                MessagePlayerUpdate messagePlayerUpdate = new MessagePlayerUpdate(player);
                String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(tokenKey).toString();
                channelServiceOut.queueDeclare(queueName, false, false, false, null);
                channelServiceOut.basicPublish("", queueName, null, messagePlayerUpdate.getBytes());
            }
        }
    }

    private void manageDeleteGameCharacterFromStockRequest(Message message) throws IOException {
        MessageDeleteGameCharacterFromStock messageDeleteGameCharacterFromStock = (MessageDeleteGameCharacterFromStock) message;

        String username = messageDeleteGameCharacterFromStock.getUsername();
        String tokenKey = messageDeleteGameCharacterFromStock.getKeyToken();
        ObjectId gameCharId = messageDeleteGameCharacterFromStock.getGameCharId();

        log.debug("Received a DeleteGameChararacterFromStockRequest");
        log.debug("Username : '{}'", username);
        log.debug("TokenKey : '{}'", tokenKey);
        log.debug("GameCharName : '{}'", gameCharId);

        if (null != tokenKey && null != username && null != gameCharId) {
            if (!tokenKey.isEmpty() && !username.isEmpty()) {
                User user = userController.removeGameCharFromStock(username, tokenKey, gameCharId);
                Player player = user.toPlayer();
                MessagePlayerUpdate messagePlayerUpdate = new MessagePlayerUpdate(player);
                String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(tokenKey).toString();
                channelServiceOut.queueDeclare(queueName, false, false, false, null);
                channelServiceOut.basicPublish("", queueName, null, messagePlayerUpdate.getBytes());
            }
        }
    }

    private void managePutGameCharacterFromTeamToStockRequest(Message message) throws IOException {
        MessagePutGameCharacterFromTeamToStock messagePutGameCharacterFromTeamToStock = (MessagePutGameCharacterFromTeamToStock) message;

        String username = messagePutGameCharacterFromTeamToStock.getUsername();
        String tokenKey = messagePutGameCharacterFromTeamToStock.getKeyToken();
        ObjectId gameCharId = messagePutGameCharacterFromTeamToStock.getGameCharId();

        log.debug("Received a PutGameCharacterFromTeamToStockRequest");
        log.debug("Username : '{}'", username);
        log.debug("TokenKey : '{}'", tokenKey);
        log.debug("GameCharName : '{}'", gameCharId);

        if (null != tokenKey && null != username && null != gameCharId) {
            if (!tokenKey.isEmpty() && !username.isEmpty()) {
                User user = userController.putGameCharFromTeamToStock(username, tokenKey, gameCharId);
                Player player = user.toPlayer();
                MessagePlayerUpdate messagePlayerUpdate = new MessagePlayerUpdate(player);
                String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(tokenKey).toString();
                channelServiceOut.queueDeclare(queueName, false, false, false, null);
                channelServiceOut.basicPublish("", queueName, null, messagePlayerUpdate.getBytes());
            }
        }
    }

    private void managePutGameCharacterFromStockToTeam(Message message) throws IOException {
        MessagePutGameCharacterFromStockToTeam messagePutGameCharacterFromStockToTeam = (MessagePutGameCharacterFromStockToTeam) message;

        String username = messagePutGameCharacterFromStockToTeam.getUsername();
        String tokenKey = messagePutGameCharacterFromStockToTeam.getKeyToken();
        ObjectId gameCharId = messagePutGameCharacterFromStockToTeam.getGameCharId();

        log.debug("Received a PutGameCharacterFromStockToTeamRequest");
        log.debug("Username : '{}'", username);
        log.debug("TokenKey : '{}'", tokenKey);
        log.debug("GameCharName : '{}'", gameCharId);

        if (null != tokenKey && null != username && null != gameCharId) {
            if (!tokenKey.isEmpty() && !username.isEmpty()) {
                User user = userController.putGameCharFromStockToTeam(username, tokenKey, gameCharId);
                Player player = user.toPlayer();
                MessagePlayerUpdate messagePlayerUpdate = new MessagePlayerUpdate(player);
                String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(tokenKey).toString();
                channelServiceOut.queueDeclare(queueName, false, false, false, null);
                channelServiceOut.basicPublish("", queueName, null, messagePlayerUpdate.getBytes());
            }
        }
    }

    private void manageCreateGameCharacterRequest(Message message) throws IOException {
        MessageCreateNewGameCharacter messageCreateNewGameCharacter = (MessageCreateNewGameCharacter) message;

        String tokenKey = messageCreateNewGameCharacter.getKeyToken();
        String name = messageCreateNewGameCharacter.getName();
        Gender gender = messageCreateNewGameCharacter.getGender();
        ClanType clanType = messageCreateNewGameCharacter.getClanType();
        JobType currentJob = JobType.WARRIOR;

        if (null != name && null != gender && null != clanType && !name.isEmpty()) {
            log.debug("Received a CreateGameCharacterRequest from [{}]", tokenKey);
            log.debug("Name : '{}'", name);
            log.debug("Gender : '{}'", gender);
            log.debug("ClanType : '{}'", clanType);

            GameCharacterDB gameCharacterDB = new GameCharacterDB(name, gender, clanType, 1, 0, currentJob, new ArrayList<JobStatusDB>());
            User user = userController.addGameCharToUser(messageCreateNewGameCharacter.getUsername(), tokenKey, gameCharacterDB);
            Player player = user.toPlayer();

            MessagePlayerUpdate messagePlayerUpdate = new MessagePlayerUpdate(player);

            String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(tokenKey).toString();
            channelServiceOut.queueDeclare(queueName, false, false, false, null);
            channelServiceOut.basicPublish("", queueName, null, messagePlayerUpdate.getBytes());
        } else {
            log.error("Received a bugged CreateGameCharacterRequest from [{}]", tokenKey);
            log.error("Name : '{}'", name);
            log.error("Gender : '{}'", gender);
            log.error("ClanType : '{}'", clanType);
        }
    }

    private void manageUnlockCapacityForGameCharacterRequest(Message message) throws IOException {
        MessageUnlockCapacity messageUnlockCapacity = (MessageUnlockCapacity) message;
        String tokenKey = messageUnlockCapacity.getKeyToken();
        ObjectId gameCharId = messageUnlockCapacity.getGameCharId();
        JobType job = messageUnlockCapacity.getJob();
        String capacityName = messageUnlockCapacity.getCapacityName();
        if (null != tokenKey && null != gameCharId && null != job && null != capacityName && !tokenKey.isEmpty() && !capacityName.isEmpty()) {
            log.debug("Received an UnlockCapacityRequest from [{}]", tokenKey);
            log.debug("GC Name : '{}'", gameCharId);
            log.debug("Job : '{}'", job);
            log.debug("Capacity : '{}'", capacityName);

            User user = userController.unlockCapacityForJobForGameCharacter(tokenKey, gameCharId, job, capacityName);
            Player player = user.toPlayer();

            MessagePlayerUpdate messagePlayerUpdate = new MessagePlayerUpdate(player);
            String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(tokenKey).toString();
            channelServiceOut.queueDeclare(queueName, false, false, false, null);
            channelServiceOut.basicPublish("", queueName, null, messagePlayerUpdate.getBytes());
        } else {
            log.error("Received a bugged UnlockCapacityRequest from [{}]", tokenKey);
            log.debug("GC Name : '{}'", gameCharId);
            log.debug("Job : '{}'", job);
            log.debug("Capacity : '{}'", capacityName);
        }

    }

    // THREAD ROUTINE
    public void run() {
        while (!requestClose) {
            try {
                receiveMessage();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            channelServiceIn.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // SPRING
    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public void setHostPort(Integer hostPort) {
        this.hostPort = hostPort;
    }

    public void setAuthKeySize(Integer authKeySize) {
        this.authKeySize = authKeySize;
    }

    public void setRequestClose(boolean requestClose) {
        this.requestClose = requestClose;
    }
}
