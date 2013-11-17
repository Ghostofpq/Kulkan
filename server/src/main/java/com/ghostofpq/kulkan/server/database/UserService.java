package com.ghostofpq.kulkan.server.database;

import com.ghostofpq.kulkan.entities.character.Gender;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.clan.ClanType;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.auth.*;
import com.ghostofpq.kulkan.server.database.controller.UserController;
import com.ghostofpq.kulkan.server.database.model.GameCharacterDB;
import com.ghostofpq.kulkan.server.database.model.User;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Slf4j
public class UserService implements Runnable {
    private static final String CLIENT_QUEUE_NAME_BASE = "/client/";
    private final String serviceQueueName = "users";
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
        QueueingConsumer.Delivery delivery = consumer.nextDelivery(0);
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

                default:
                    log.error(" [X] UNEXPECTED MESSAGE : {}", message.getType());
                    break;
            }
        }
    }

    private void manageDeleteGameCharacterFromTeamRequest(Message message) throws IOException {
        MessageDeleteGameChararacterFromTeam messageDeleteGameChararacterFromTeam = (MessageDeleteGameChararacterFromTeam) message;

        String username = messageDeleteGameChararacterFromTeam.getUsername();
        String tokenKey = messageDeleteGameChararacterFromTeam.getKeyToken();
        String gameCharName = messageDeleteGameChararacterFromTeam.getGameCharName();

        log.debug("Received a DeleteGameChararacterFromTeamRequest");
        log.debug("Username : '{}'", username);
        log.debug("TokenKey : '{}'", tokenKey);
        log.debug("GameCharName : '{}'", gameCharName);

        if (null != tokenKey && null != username && null != gameCharName) {
            if (!tokenKey.isEmpty() && !username.isEmpty() && !gameCharName.isEmpty()) {
                User user = userController.removeGameCharFromTeam(username, tokenKey, gameCharName);
                Player player = user.toPlayer();
                MessagePlayerUpdate messagePlayerUpdate = new MessagePlayerUpdate(player);
                String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(tokenKey).toString();
                channelServiceOut.queueDeclare(queueName, false, false, false, null);
                channelServiceOut.basicPublish("", queueName, null, messagePlayerUpdate.getBytes());
            }
        }
    }

    private void manageDeleteGameCharacterFromStockRequest(Message message) throws IOException {
        MessageDeleteGameChararacterFromStock messageDeleteGameChararacterFromStock = (MessageDeleteGameChararacterFromStock) message;

        String username = messageDeleteGameChararacterFromStock.getUsername();
        String tokenKey = messageDeleteGameChararacterFromStock.getKeyToken();
        String gameCharName = messageDeleteGameChararacterFromStock.getGameCharName();

        log.debug("Received a DeleteGameChararacterFromStockRequest");
        log.debug("Username : '{}'", username);
        log.debug("TokenKey : '{}'", tokenKey);
        log.debug("GameCharName : '{}'", gameCharName);

        if (null != tokenKey && null != username && null != gameCharName) {
            if (!tokenKey.isEmpty() && !username.isEmpty() && !gameCharName.isEmpty()) {
                User user = userController.removeGameCharFromStock(username, tokenKey, gameCharName);
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
        String gameCharName = messagePutGameCharacterFromTeamToStock.getGameCharName();

        log.debug("Received a PutGameCharacterFromTeamToStockRequest");
        log.debug("Username : '{}'", username);
        log.debug("TokenKey : '{}'", tokenKey);
        log.debug("GameCharName : '{}'", gameCharName);

        if (null != tokenKey && null != username && null != gameCharName) {
            if (!tokenKey.isEmpty() && !username.isEmpty() && !gameCharName.isEmpty()) {
                User user = userController.putGameCharFromTeamToStock(username, tokenKey, gameCharName);
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
        String gameCharName = messagePutGameCharacterFromStockToTeam.getGameCharName();

        log.debug("Received a PutGameCharacterFromStockToTeamRequest");
        log.debug("Username : '{}'", username);
        log.debug("TokenKey : '{}'", tokenKey);
        log.debug("GameCharName : '{}'", gameCharName);

        if (null != tokenKey && null != username && null != gameCharName) {
            if (!tokenKey.isEmpty() && !username.isEmpty() && !gameCharName.isEmpty()) {
                User user = userController.putGameCharFromStockToTeam(username, tokenKey, gameCharName);
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

        if (null != name && null != gender && null != clanType && !name.isEmpty()) {
            log.debug("Received a CreateGameCharacterRequest from [{}]", tokenKey);
            log.debug("Name : '{}'", name);
            log.debug("Gender : '{}'", gender);
            log.debug("ClanType : '{}'", clanType);

            GameCharacterDB gameCharacterDB = new GameCharacterDB(name, gender, clanType, 1, 0);
            User user = userController.addGameCharToUser(messageCreateNewGameCharacter.getUsername(), tokenKey, gameCharacterDB);
            Player player = user.toPlayer();

            MessagePlayerUpdate messagePlayerUpdate = new MessagePlayerUpdate(player);

            String queueName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(tokenKey).toString();
            log.debug(" [-] OPENING QUEUE : {}", queueName);
            channelServiceOut.queueDeclare(queueName, false, false, false, null);
            channelServiceOut.basicPublish("", queueName, null, messagePlayerUpdate.getBytes());
        } else {
            log.error("Received a bugged CreateGameCharacterRequest from [{}]", tokenKey);
            log.error("Name : '{}'", name);
            log.error("Gender : '{}'", gender);
            log.error("ClanType : '{}'", clanType);
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
