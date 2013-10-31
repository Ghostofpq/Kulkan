package com.ghostofpq.kulkan.server.database.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class User {
    @Id
    private ObjectId id;
    private String username;
    private String password;

    public User() {
    }

    public User(String username, String password) {
        this.id = new ObjectId();
        this.username = username;
        this.password = password;
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

    @Override
    public String toString() {
        return new StringBuilder().append("User{")
                .append("id='").append(id).append("', ")
                .append("username='").append(username).append("', ")
                .append("password='").append(password).append("'} ")
                .toString();
    }
}
