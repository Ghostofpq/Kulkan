package com.ghostofpq.kulkan.server.matchmaking;

public class MatchmakingManager {
    private static volatile MatchmakingManager instance = new MatchmakingManager();

    private MatchmakingManager() {
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
}
