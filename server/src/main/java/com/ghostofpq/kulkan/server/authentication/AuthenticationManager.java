package com.ghostofpq.kulkan.server.authentication;


import com.ghostofpq.kulkan.server.Server;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class AuthenticationManager {
    private Server server;

    private AuthenticationManager() {
    }

    public boolean authenticate(String pseudo, String password) {
        boolean result = false;
        DBCollection coll = server.getDb().getCollection("users");
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
        DBCollection coll = server.getDb().getCollection("users");
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
        DBCollection coll = server.getDb().getCollection("users");
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

    public void setServer(Server server) {
        this.server = server;
    }
}
