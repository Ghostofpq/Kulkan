package com.ghostofpq.kulkan.server.matchmaking;

import com.ghostofpq.kulkan.entities.messages.*;
import com.ghostofpq.kulkan.server.Server;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MatchmakingManager {
    private static volatile MatchmakingManager instance = new MatchmakingManager();
    private final String CLIENT_QUEUE_NAME_BASE = "/client/";
    private final String MATCHMAKING_SERVER_QUEUE_NAME_BASE = "/server/matchmaking";
    private List<String> subscribedClients;
    private Map<String, Match> matchMap;
    private Channel channelOut;
    private Channel channelMatchmakingIn;
    private QueueingConsumer matchmakingConsumer;
    private int matchmapIncrementor;

    private MatchmakingManager() {
        matchmapIncrementor = 0;
        subscribedClients = new ArrayList<String>();
        matchMap = new HashMap<String, Match>();
        try {
            channelOut = Server.getInstance().getConnection().createChannel();
            channelMatchmakingIn = Server.getInstance().getConnection().createChannel();
            channelMatchmakingIn.queueDeclare(MATCHMAKING_SERVER_QUEUE_NAME_BASE, false, false, false, null);
            matchmakingConsumer = new QueueingConsumer(channelMatchmakingIn);
            channelMatchmakingIn.basicConsume(MATCHMAKING_SERVER_QUEUE_NAME_BASE, true, matchmakingConsumer);
            log.debug(" [-] OPENING QUEUE : {}", MATCHMAKING_SERVER_QUEUE_NAME_BASE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MatchmakingManager getInstance() {
        if (instance == null) {
            synchronized (MatchmakingManager.class) {
                if (instance == null) {
                    instance = new MatchmakingManager();
                }
            }
        }
        return instance;
    }

    public void run() throws IOException, InterruptedException {
        receiveMessage();
        match();
        checkMatchPropositions();
    }

    public void receiveMessage() throws IOException, InterruptedException {
        QueueingConsumer.Delivery delivery = matchmakingConsumer.nextDelivery(1);
        if (null != delivery) {
            Message message = Message.loadFromBytes(delivery.getBody());
            if (null != message) {
                switch (message.getType()) {
                    case MATCHMAKING_SUBSCRIBE:
                        MessageMatchmakingSubscribe messageMatchmakingSubscribe = (MessageMatchmakingSubscribe) message;
                        addClient(messageMatchmakingSubscribe.getKeyToken());
                        break;
                    case MATCHMAKING_UNSUBSCRIBE:
                        MessageMatchmakingUnsubscribe messageMatchmakingUnsubscribe = (MessageMatchmakingUnsubscribe) message;
                        removeClient(messageMatchmakingUnsubscribe.getKeyToken());
                        break;
                    case MATCHMAKING_ACCEPT:
                        MessageMatchmakingAccept messageMatchmakingAccept = (MessageMatchmakingAccept) message;
                        matchMap.get(messageMatchmakingAccept.getMatchKey()).clientAccept(messageMatchmakingAccept.getKeyToken());
                        break;
                    case MATCHMAKING_REFUSE:
                        MessageMatchmakingRefuse messageMatchmakingRefuse = (MessageMatchmakingRefuse) message;
                        matchMap.get(messageMatchmakingRefuse.getMatchKey()).clientRefuse(messageMatchmakingRefuse.getKeyToken());
                        break;
                    default:
                        log.error(" [X] UNEXPECTED MESSAGE : {}", message.getType());
                        break;
                }
            }
        }
    }

    public void addClient(String clientKey) {
        try {
            log.debug(" [-] ADDING CLIENT KEY : {}", clientKey);
            String clientChannelName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(clientKey).toString();
            channelOut.queueDeclare(clientChannelName, false, false, false, null);
            log.debug(" [-] OPENING QUEUE : {}", clientChannelName);
            if (!subscribedClients.contains(clientKey)) {
                subscribedClients.add(clientKey);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeClient(String clientKey) {
        log.debug(" [-] REMOVING CLIENT KEY : {}", clientKey);
        subscribedClients.remove(clientKey);
        for (String matchKey : matchMap.keySet()) {
            Match match = matchMap.get(matchKey);
            if (match.getAllClients().contains(clientKey)) {
                match.clientRefuse(clientKey);
            }
        }
    }

    public void checkMatchPropositions() {
        List<String> matchesToRemove = new ArrayList<String>();
        for (String matchKey : matchMap.keySet()) {
            Match match = matchMap.get(matchKey);
            Match.ClientState globalClientState = match.getGlobalClientState();
            switch (globalClientState) {
                case ACCEPT:
                    //CREATE GAME
                    break;
                case REFUSE:
                    List<String> clientsToReinject = match.getClientsToReinject();
                    for (String client : clientsToReinject) {
                        if (!subscribedClients.contains(client)) {
                            subscribedClients.add(client);
                        }
                        sendAbortMessage(client);
                    }
                    matchesToRemove.add(matchKey);
                    break;
                case PENDING:
                    break;
            }
        }
        for (String match : matchesToRemove) {
            matchMap.remove(match);
        }
    }

    public void sendAbortMessage(String clientKey) {
        MessageMatchAbort messageMatchAbort = new MessageMatchAbort();
        String clientChannelName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(clientKey).toString();
        log.debug(" [-] MATCH ABORT FOR {}", clientKey);
        try {
            channelOut.basicPublish("", clientChannelName, null, messageMatchAbort.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void match() {
        if (subscribedClients.size() >= 2) {
            List<String> matchedPlayers = new ArrayList<String>();
            matchedPlayers.add(subscribedClients.get(0));
            matchedPlayers.add(subscribedClients.get(1));

            Match match = new Match(matchedPlayers);
            String matchKey = insertInMatchMap(match);

            for (String matchedPlayer : matchedPlayers) {
                MessageMatchFound messageMatchFound = new MessageMatchFound(matchKey);
                String clientChannelName = new StringBuilder().append(CLIENT_QUEUE_NAME_BASE).append(matchedPlayer).toString();
                log.debug(" [-] MATCH FOUND FOR {}", matchedPlayer);
                try {
                    channelOut.basicPublish("", clientChannelName, null, messageMatchFound.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                subscribedClients.remove(matchedPlayer);
            }

        }
    }

    public String insertInMatchMap(Match match) {
        String matchKey = String.valueOf(matchmapIncrementor);
        matchMap.put(matchKey, match);
        matchmapIncrementor++;
        return matchKey;
    }
}
