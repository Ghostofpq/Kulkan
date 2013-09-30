package com.ghostofpq.kulkan.server.authentification;


import com.ghostofpq.kulkan.server.Server;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

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
        boolean result = false;
        DBCollection coll = Server.getInstance().getDb().getCollection("users");
        BasicDBObject query = new BasicDBObject("pseudo", pseudo);
        DBCursor cursor = coll.find(query);
        if (cursor.hasNext()) {
            DBObject obj = cursor.next();
            if (obj.get("password").equals(password)) ;
            {
                obj.put("key", generateKey());
                coll.update(new BasicDBObject("_id", obj.get("_id")), obj);
                result = true;
            }
        }
        return result;
    }

    public String getTokenKeyFor(String pseudo) {
        String result = "";
        DBCollection coll = Server.getInstance().getDb().getCollection("users");
        BasicDBObject query = new BasicDBObject("pseudo", pseudo);
        DBCursor cursor = coll.find(query);
        if (cursor.hasNext()) {
            DBObject obj = cursor.next();
            result = (String) obj.get("key");
        }
        return result;
    }

    public String getNameForKey(String key) {
        String result = "";
        DBCollection coll = Server.getInstance().getDb().getCollection("users");
        BasicDBObject query = new BasicDBObject("key", key);
        DBCursor cursor = coll.find(query);
        if (cursor.hasNext()) {
            DBObject obj = cursor.next();
            result = (String) obj.get("pseudo");
        }
        return result;
    }

    private String generateKey() {
        return String.valueOf((int) Math.floor(Math.random() * 10000000));
    }
}
