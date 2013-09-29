package com.ghostofpq.kulkan.server.matchmaking;

import java.util.ArrayList;
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

    public ClientState getGlobalClientState() {
        ClientState result = ClientState.PENDING;
        boolean allAccept = true;
        for (ClientState clientState : playersMap.values()) {
            if (clientState.equals(ClientState.REFUSE)) {
                result = ClientState.REFUSE;
                allAccept = false;
                break;
            } else if (clientState.equals(ClientState.PENDING)) {
                result = ClientState.PENDING;
                allAccept = false;
            }
        }
        if (allAccept) {
            result = ClientState.ACCEPT;
        }
        return result;
    }

    public List<String> getClientsToReinject() {
        List<String> result = new ArrayList<String>();
        for (String client : getAllClients()) {
            if (playersMap.get(client).equals(ClientState.ACCEPT) || playersMap.get(client).equals(ClientState.PENDING)) {
                result.add(client);
            }
        }
        return result;
    }

    public List<String> getAllClients() {
        return new ArrayList<String>(playersMap.keySet());
    }


    public enum ClientState {
        PENDING, ACCEPT, REFUSE
    }


}
