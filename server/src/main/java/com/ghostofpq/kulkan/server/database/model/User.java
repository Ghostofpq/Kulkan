package com.ghostofpq.kulkan.server.database.model;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class User {
    @Id
    private ObjectId id;
    private String username;
    private String password;
    private String authKey;
    private String passwordSalt;

    public User() {
    }

    public User(String username, String password) {
        this.id = new ObjectId();
        this.username = username;
        this.passwordSalt = RandomStringUtils.randomAscii(20);
        this.password = DigestUtils.shaHex(password + passwordSalt);
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("User{")
                .append("username='").append(username).append("', ")
                .append("password='").append(password).append("'} ")
                .toString();
    }

}
