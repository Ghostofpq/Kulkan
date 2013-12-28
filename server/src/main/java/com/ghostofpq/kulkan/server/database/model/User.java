package com.ghostofpq.kulkan.server.database.model;

import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.inventory.Inventory;
import com.ghostofpq.kulkan.entities.job.Job;
import com.ghostofpq.kulkan.entities.job.Mage;
import com.ghostofpq.kulkan.entities.job.Warrior;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class User {
    @Id
    private ObjectId id;
    @Indexed
    private String username;
    private String password;
    @Indexed
    private String tokenKey;
    private String passwordSalt;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private List<GameCharacterDB> team;
    private List<GameCharacterDB> stock;
    private Integer grade;
    private Integer money;
    private Inventory inventory;

    public User() {
    }

    public User(String username, String password) {
        this.id = new ObjectId();
        this.username = username;
        this.passwordSalt = RandomStringUtils.randomAscii(20);
        this.password = DigestUtils.shaHex(password + passwordSalt);
        this.team = new ArrayList<GameCharacterDB>();
        this.stock = new ArrayList<GameCharacterDB>();
        this.grade = 0;
        this.money = 100;
        this.inventory = new Inventory();
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

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
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

    public void addGameCharToStock(GameCharacterDB gameCharacterDB) {
        stock.add(gameCharacterDB);
    }

    public void addGameCharToStock(GameCharacter gameCharacter) {
        stock.add(new GameCharacterDB(gameCharacter));
    }

    public void addGameCharToTeam(GameCharacterDB gameCharacterDB) {
        team.add(gameCharacterDB);
    }

    public void addGameCharToTeam(GameCharacter gameCharacter) {
        team.add(new GameCharacterDB(gameCharacter));
    }

    public void updateTeam(List<GameCharacter> gameCharacterList) {
        team = new ArrayList<GameCharacterDB>();
        for (GameCharacter gameCharacter : gameCharacterList) {
            team.add(new GameCharacterDB(gameCharacter));
        }
    }

    public void updateGameChar(GameCharacter gameCharacter) {
        for (GameCharacterDB gameCharacterDB : team) {
            if (gameCharacterDB.getId().equals(gameCharacter.getId())) {
                log.debug("Updating TeamMember : {}", gameCharacter.getName());
                team.set(team.indexOf(gameCharacterDB), new GameCharacterDB(gameCharacter));
                break;
            }
        }
        for (GameCharacterDB gameCharacterDB : stock) {
            if (gameCharacterDB.getId().equals(gameCharacter.getId())) {
                log.debug("Updating StockMember : {}", gameCharacter.getName());
                stock.set(stock.indexOf(gameCharacterDB), new GameCharacterDB(gameCharacter));
                break;
            }
        }
    }

    public Player toPlayer() {
        Player player = new Player(username);
        player.setTeam(new ArrayList<GameCharacter>());
        player.setStock(new ArrayList<GameCharacter>());

        for (GameCharacterDB gameCharacterDB : this.team) {
            GameCharacter gameCharacter = new GameCharacter(
                    gameCharacterDB.getId(),
                    player,
                    gameCharacterDB.getName(),
                    gameCharacterDB.getClanType(),
                    gameCharacterDB.getGender(),
                    gameCharacterDB.getLvl(),
                    gameCharacterDB.getCurrentXp()
            );
            if (null != gameCharacterDB.getJobStatusDBs()) {
                for (JobStatusDB jobStatusDB : gameCharacterDB.getJobStatusDBs()) {
                    Job job = jobStatusDB.toJob();
                    switch (job.getJobType()) {
                        case WARRIOR:
                            gameCharacter.setJobWarrior((Warrior) job);
                            break;
                        case MAGE:
                            gameCharacter.setJobMage((Mage) job);
                            break;
                    }
                }
            }
            gameCharacter.setCurrentJob(gameCharacterDB.getCurrentJob());
            gameCharacter.calculateAggregatedCharacteristics();
            player.getTeam().add(gameCharacter);
        }
        for (GameCharacterDB gameCharacterDB : this.stock) {
            GameCharacter gameCharacter = new GameCharacter(
                    gameCharacterDB.getId(),
                    player,
                    gameCharacterDB.getName(),
                    gameCharacterDB.getClanType(),
                    gameCharacterDB.getGender(),
                    gameCharacterDB.getLvl(),
                    gameCharacterDB.getCurrentXp()
            );
            if (null != gameCharacterDB.getJobStatusDBs()) {
                for (JobStatusDB jobStatusDB : gameCharacterDB.getJobStatusDBs()) {
                    Job job = jobStatusDB.toJob();
                    switch (job.getJobType()) {
                        case WARRIOR:
                            gameCharacter.setJobWarrior((Warrior) job);
                            break;
                        case MAGE:
                            gameCharacter.setJobMage((Mage) job);
                            break;
                    }
                }
            }
            gameCharacter.setCurrentJob(gameCharacterDB.getCurrentJob());
            gameCharacter.calculateAggregatedCharacteristics();
            player.getStock().add(gameCharacter);
        }
        if (null != inventory) {
            player.setInventory(inventory);
        }
        if (null != money) {
            player.setMoney(money);
        }
        return player;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("User{")
                .append("username='").append(username).append("', ")
                .append("tokenKey='").append(tokenKey).append("'} ")
                .append("password='").append(tokenKey).append("'} ")
                .append("passwordSalt='").append(tokenKey).append("'} ")
                .toString();
    }

    public boolean equals(User obj) {
        return this.getId().equals(obj.getId());
    }
}
