package com.ghostofpq.kulkan.server.database.model;

import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.character.Team;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document
public class User {
    @Id
    private ObjectId id;
    @Indexed
    private String username;
    private String password;
    @Indexed
    private String authKey;
    private String passwordSalt;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private List<GameCharacterDB> team;
    private List<GameCharacterDB> stock;
    private Integer grade;

    public User() {
        this.id = new ObjectId();
    }

    public User(String username, String password) {
        this.id = new ObjectId();
        this.username = username;
        this.passwordSalt = RandomStringUtils.randomAscii(20);
        this.password = DigestUtils.shaHex(password + passwordSalt);
        team = new ArrayList<GameCharacterDB>();
        stock = new ArrayList<GameCharacterDB>();
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<GameCharacterDB> getTeam() {
        return team;
    }

    public List<GameCharacterDB> getStock() {
        return stock;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Player toPlayer() {
        Player result = new Player();
        result.setPseudo(username);
        result.setGrade(grade);
        Team pTeam = new Team();
        for (GameCharacterDB gameCharacterDB : this.team) {
            GameCharacter gameCharacter = new GameCharacter(
                    result,
                    gameCharacterDB.getName(),
                    gameCharacterDB.getRaceType(),
                    gameCharacterDB.getGender(),
                    gameCharacterDB.getLvl(),
                    gameCharacterDB.getCurrentXp()
            );
            pTeam.getTeam().add(gameCharacter);
        }
        result.setTeam(pTeam);
        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("User{")
                .append("username='").append(username).append("', ")
                .append("authKey='").append(authKey).append("'} ")
                .append("password='").append(authKey).append("'} ")
                .append("passwordSalt='").append(authKey).append("'} ")
                .toString();
    }

}
