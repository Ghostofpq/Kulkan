package com.ghostofpq.kulkan.server.authentication;

import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.auth.MessageAuthenticationRequest;
import com.ghostofpq.kulkan.entities.messages.auth.MessageAuthenticationResponse;
import com.ghostofpq.kulkan.entities.messages.auth.MessageCreateAccount;
import com.ghostofpq.kulkan.entities.messages.auth.MessageErrorCode;
import com.ghostofpq.kulkan.server.database.model.User;
import com.ghostofpq.kulkan.server.database.repository.UserRepository;
import com.ghostofpq.kulkan.server.lobby.LobbyManager;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

@Slf4j
public class AuthenticationManager implements Runnable {
    private final String authenticationQueueName = "authentication";
    private String hostIp;
    private Integer hostPort;
    private Integer authKeySize;
    @Autowired
    private LobbyManager lobbyManager;
    private QueueingConsumer consumer;
    private Connection connection;
    private Channel channelAuthenticating;
    private boolean requestClose;
    @Autowired
    private UserRepository userRepositoryRepository;

    private AuthenticationManager() {
    }

    public void initConnection() throws IOException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostIp);
        factory.setPort(hostPort);
        connection = factory.newConnection();
        channelAuthenticating = connection.createChannel();
        channelAuthenticating.queueDeclare(authenticationQueueName, false, false, false, null);
        channelAuthenticating.basicQos(1);
        consumer = new QueueingConsumer(channelAuthenticating);
        channelAuthenticating.basicConsume(authenticationQueueName, false, consumer);
    }

    public boolean authenticate(String username, String password) {
        log.debug("Authenticate [{}]/[{}]", username, password);
        boolean result;
        List<User> userList = userRepositoryRepository.findByUsername(username);
        if (userList.size() == 1) {
            User user = userList.get(0);
            String hashedPassword = DigestUtils.shaHex(password + user.getPasswordSalt());
            if (user.getPassword().equals(hashedPassword)) {
                String authKey = generateKey();
                user.setAuthKey(authKey);
                userRepositoryRepository.save(user);
                log.debug("user [{}] is authenticated with key [{}]", username, authKey);
                result = true;
            } else {
                log.warn("bad password");
                result = false;
            }
        } else if (userList.size() > 1) {
            log.error("multiple results for username : [{}]", username);
            result = false;
        } else {
            log.error("no result for username : [{}]", username);
            result = false;
        }
        return result;
    }

    public String getTokenKeyFor(String username) {
        String result = "";
        List<User> userList = userRepositoryRepository.findByUsername(username);
        if (userList.size() == 1) {
            User user = userList.get(0);
            result = user.getAuthKey();
        } else if (userList.size() > 1) {
            log.error("multiple results for username : [{}]", username);
        } else {
            log.error("no result for username : [{}]", username);
        }
        return result;
    }

    public String getNameForKey(String authKey) {
        String result = "";
        List<User> userList = userRepositoryRepository.findByAuthKey(authKey);
        if (userList.size() == 1) {
            User user = userList.get(0);
            result = user.getUsername();
        } else if (userList.size() > 1) {
            log.error("multiple results for authKey : [{}]", authKey);
        } else {
            log.error("no result for authKey : [{}]", authKey);
        }
        return result;
    }

    private String generateKey() {
        String result = RandomStringUtils.randomNumeric(authKeySize);
        while (!getNameForKey(result).equals("")) {
            log.error("Key [{}] is already in use", result);
            result = RandomStringUtils.randomNumeric(authKeySize);
        }
        return result;
    }

    private void receiveMessage() throws InterruptedException, IOException {
        QueueingConsumer.Delivery delivery = consumer.nextDelivery(0);
        if (null != delivery) {
            log.debug(" [-] RECEIVED MESSAGE ON : {}", authenticationQueueName);
            AMQP.BasicProperties props = delivery.getProperties();
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
                    .correlationId(props.getCorrelationId())
                    .build();

            Message message = Message.loadFromBytes(delivery.getBody());
            log.debug(" [x] Received '{}'", message.getType());

            switch (message.getType()) {
                case AUTHENTICATION_REQUEST:
                    MessageAuthenticationRequest authenticationRequest = (MessageAuthenticationRequest) message;
                    boolean authenticationResult = authenticate(authenticationRequest.getPseudo(),
                            authenticationRequest.getPassword());
                    String tokenKey = getTokenKeyFor(authenticationRequest.getPseudo());

                    MessageErrorCode code;
                    if (authenticationResult) {
                        code = MessageErrorCode.OK;
                        lobbyManager.addClient(tokenKey);
                    } else {
                        code = MessageErrorCode.BAD_LOGIN_INFORMATIONS;
                    }

                    MessageAuthenticationResponse authenticationResponse = new MessageAuthenticationResponse(
                            authenticationRequest.getPseudo(),
                            authenticationRequest.getPassword(),
                            tokenKey,
                            code);

                    channelAuthenticating.basicPublish("", props.getReplyTo(), replyProps, authenticationResponse.getBytes());
                    channelAuthenticating.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                    log.debug(" [x] Sent '{}'", authenticationResponse.getType());
                    break;
                case CREATE_ACCOUT:
                    MessageCreateAccount messageCreateAccount = (MessageCreateAccount) message;
                    User user = new User(messageCreateAccount.getUserName(), messageCreateAccount.getPassword());
                    userRepositoryRepository.save(user);
                    log.debug("user [{}] is created", user.getUsername());
                    break;
                default:
                    log.error(" [X] UNEXPECTED MESSAGE : {}", message.getType());
                    break;
            }
        }
    }

    public void setRequestClose(boolean requestClose) {
        this.requestClose = requestClose;
    }

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

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public void setHostPort(Integer hostPort) {
        this.hostPort = hostPort;
    }

    public void setAuthKeySize(Integer authKeySize) {
        this.authKeySize = authKeySize;
    }
}
