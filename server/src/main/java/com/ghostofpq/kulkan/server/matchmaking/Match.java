package com.ghostofpq.kulkan.server.matchmaking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Match {
    private Map<String, ClientState> playersMap;
    private String gameID;

    public Match(List<String> playersList) {
        playersMap = new HashMap<String, ClientState>();
        for (String player : playersList) {
            playersMap.put(player, ClientState.PENDING);
        }
    }

    public void clientAccept(String clientKey) {
        playersMap.put(clientKey, ClientState.ACCEPT);
    }

    public void clientRefuse(String clientKey) {
        playersMap.put(clientKey, ClientState.REFUSE);
    }

    public enum ClientState {
        PENDING, ACCEPT, REFUSE
    }
}
