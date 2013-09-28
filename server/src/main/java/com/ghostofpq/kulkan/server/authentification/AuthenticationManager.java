package com.ghostofpq.kulkan.server.authentification;


public class AuthenticationManager {
    private static volatile AuthenticationManager instance = new AuthenticationManager();

    private AuthenticationManager() {
    }

    public static AuthenticationManager getInstance() {
        if (instance == null) {
            synchronized (AuthenticationManager.class) {
                if (instance == null) {
                    instance = new AuthenticationManager();
                }
            }
        }
        return instance;
    }

    public boolean authenticate(String pseudo, String password) {
        return true;
    }

    public String getTokenKeyFor(String pseudo) {
        String result = new StringBuilder().append(pseudo).append("key").toString();
        return result;
    }
}
