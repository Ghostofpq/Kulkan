package com.ghostofpq.kulkan.server.authentication;

import com.ghostofpq.kulkan.entities.character.Gender;
import com.ghostofpq.kulkan.entities.clan.ClanType;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.auth.*;
import com.ghostofpq.kulkan.server.database.controller.UserController;
import com.ghostofpq.kulkan.server.database.model.GameCharacterDB;
import com.ghostofpq.kulkan.server.database.model.JobStatusDB;
import com.ghostofpq.kulkan.server.database.model.User;
import com.ghostofpq.kulkan.server.database.repository.UserRepository;
import com.ghostofpq.kulkan.server.lobby.LobbyManager;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class AuthenticationManager implements Runnable {
    private final String authenticationQueueName = "authentication";
    // PARAMETERS - SPRING
    private String hostIp;
    private Integer hostPort;
    private Integer authKeySize;
    // OTHER BEANS - SPRING
    @Autowired
    private LobbyManager lobbyManager;
    @Autowired
    private UserRepository userRepositoryRepository;
    @Autowired
    private UserController userController;
    // MESSAGING
    private QueueingConsumer consumer;
    private Connection connection;
    private Channel channelAuthenticating;
    // THREAD ROUTINE
    private boolean requestClose;

    private AuthenticationManager() {
        requestClose = false;
    }

    public void initConnection() throws IOException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostIp);
        factory.setPort(hostPort);
        log.debug("{}:{}", hostIp, hostPort);
        connection = factory.newConnection();
        channelAuthenticating = connection.createChannel();
        channelAuthenticating.queueDeclare(authenticationQueueName, false, false, false, null);
        channelAuthenticating.basicQos(1);
        consumer = new QueueingConsumer(channelAuthenticating);
        channelAuthenticating.basicConsume(authenticationQueueName, false, consumer);
        //addusers();
    }

    public User authenticate(String username, String password) {
        log.debug("Authenticate [{}]/[{}]", username, password);
        User user = userController.getUserForUsername(username);
        if (null != user) {
            String hashedPassword = DigestUtils.shaHex(password + user.getPasswordSalt());
            if (user.getPassword().equals(hashedPassword)) {
                user = userController.generateTokenKeyForUser(user);
            } else {
                log.warn("Invalid Password");
                user = null;
            }
        } else {
            log.warn("Invalid Username");
            user = null;
        }
        return user;
    }

    private String generateKey() {
        String result = RandomStringUtils.randomNumeric(authKeySize);
        while (!userController.getNameForTokenKey(result).equals("")) {
            log.error("Key [{}] is already in use", result);
            result = RandomStringUtils.randomNumeric(authKeySize);
        }
        return result;
    }

    private void receiveMessage() throws InterruptedException, IOException {
        QueueingConsumer.Delivery delivery = consumer.nextDelivery(0);
        if (null != delivery) {
            AMQP.BasicProperties props = delivery.getProperties();
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
                    .correlationId(props.getCorrelationId())
                    .build();
            Message message = Message.loadFromBytes(delivery.getBody());
            log.debug(" [x] RECEIVED '{}' ON {}", message.getType(), authenticationQueueName);

            switch (message.getType()) {
                case AUTHENTICATION_REQUEST:
                    manageAuthenticationRequestMessage(message, props, replyProps);
                    break;
                case CREATE_ACCOUT:
                    manageCreateAccountMessage(message, props, replyProps);
                    break;
                default:
                    log.error(" [X] UNEXPECTED MESSAGE : {}", message.getType());
                    break;
            }
            channelAuthenticating.basicAck(delivery.getEnvelope().getDeliveryTag(), true);
        }
    }

    private void manageAuthenticationRequestMessage(Message message, AMQP.BasicProperties props, AMQP.BasicProperties replyProps) throws IOException {
        MessageAuthenticationRequest authenticationRequest = (MessageAuthenticationRequest) message;

        User user = authenticate(authenticationRequest.getPseudo(),
                authenticationRequest.getPassword());

        if (null != user) {
            MessageErrorCode code = MessageErrorCode.OK;
            lobbyManager.addClient(user.getTokenKey());
            MessageAuthenticationResponse authenticationResponse = new MessageAuthenticationResponse(
                    authenticationRequest.getPseudo(),
                    authenticationRequest.getPassword(),
                    user.getTokenKey(),
                    user.toPlayer(),
                    code);

            channelAuthenticating.basicPublish("", props.getReplyTo(), replyProps, authenticationResponse.getBytes());
        }
    }

    private void manageCreateAccountMessage(Message message, AMQP.BasicProperties props, AMQP.BasicProperties replyProps) throws IOException {
        MessageCreateAccount messageCreateAccount = (MessageCreateAccount) message;
        MessageErrorCode code;
        if (userRepositoryRepository.findByUsername(messageCreateAccount.getUserName()).isEmpty()) {
            User user = new User(messageCreateAccount.getUserName(), messageCreateAccount.getPassword());
            userRepositoryRepository.save(user);
            code = MessageErrorCode.OK;
            log.debug("user [{}] is created", user.getUsername());
        } else {
            code = MessageErrorCode.USER_NAME_ALREADY_USED;
            log.error("user [{}] is already in base", messageCreateAccount.getUserName());
        }

        MessageCreateAccountResponse createAccountResponse = new MessageCreateAccountResponse(
                code);
        channelAuthenticating.basicPublish("", props.getReplyTo(), replyProps, createAccountResponse.getBytes());
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
            channelAuthenticating.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRequestClose(boolean requestClose) {
        this.requestClose = requestClose;
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

    // POPULATE
    private void addusers() {
        User user1 = new User("azerty", "123456");

        GameCharacterDB char1 = new GameCharacterDB("azerty1Human", Gender.MALE, ClanType.HUMAN, 1, 0, new ArrayList<JobStatusDB>());
        GameCharacterDB char2 = new GameCharacterDB("azerty2Elve", Gender.FEMALE, ClanType.ELVE, 1, 0, new ArrayList<JobStatusDB>());
        GameCharacterDB char3 = new GameCharacterDB("azerty3Dwarf", Gender.MALE, ClanType.DWARF, 1, 0, new ArrayList<JobStatusDB>());

        user1.addGameCharToTeam(char1);
        user1.addGameCharToTeam(char2);
        user1.addGameCharToTeam(char3);

        if (userRepositoryRepository.findByUsername("azerty").isEmpty()) {
            userRepositoryRepository.save(user1);
        }
        User user2 = new User("ghostofpq", "123456");

        GameCharacterDB char4 = new GameCharacterDB("ghostofpq1Human", Gender.MALE, ClanType.HUMAN, 1, 0, new ArrayList<JobStatusDB>());
        GameCharacterDB char5 = new GameCharacterDB("ghostofpq2Elve", Gender.FEMALE, ClanType.ELVE, 1, 0, new ArrayList<JobStatusDB>());
        GameCharacterDB char6 = new GameCharacterDB("ghostofpq3Dwarf", Gender.MALE, ClanType.DWARF, 1, 0, new ArrayList<JobStatusDB>());

        user2.addGameCharToTeam(char4);
        user2.addGameCharToTeam(char5);
        user2.addGameCharToTeam(char6);

        if (userRepositoryRepository.findByUsername("ghostofpq").isEmpty()) {
            userRepositoryRepository.save(user2);
        }
    }
}
