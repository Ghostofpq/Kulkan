package com.ghostofpq.kulkan.server.authentication;

import com.ghostofpq.kulkan.server.Server;
import com.ghostofpq.kulkan.server.database.model.User;
import com.ghostofpq.kulkan.server.database.repository.UserRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class AuthenticationManager {
    private Server server;
    @Autowired
    private UserRepository userRepositoryRepository;

    private AuthenticationManager() {
    }

    public boolean authenticate(String username, String password) {
        log.debug("authenticate [{}]/[{}]", username, password);
        boolean result;
        List<User> userList = userRepositoryRepository.findByUsername(username);
        if (userList.size() == 1) {
            User user = userList.get(0);
            if (user.getPassword().equals(password)) {

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
