package com.ghostofpq.kulkan.server.database.model;

import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.character.Gender;
import com.ghostofpq.kulkan.entities.race.Race;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class GameCharacterDocument {
    @Id
    private ObjectId id;
    private String name;
    private Gender gender;
    private Race race;
    private Integer lvl;
    private Integer currentXp;

    public GameCharacterDocument(String name, Gender gender, Race race, Integer lvl, Integer currentXp) {
        this.id = new ObjectId();
        this.name = name;
        this.gender = gender;
        this.race = race;
        this.lvl = lvl;
        this.currentXp = currentXp;
    }

    public GameCharacterDocument(GameCharacter gameCharacter) {
        this.name = gameCharacter.getName();
        this.gender = gameCharacter.getGender();
        this.race = gameCharacter.getRace();
        this.lvl = gameCharacter.getLevel();
        this.currentXp = gameCharacter.getExperience();
    }

    public ObjectId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Integer getLvl() {
        return lvl;
    }

    public void setLvl(Integer lvl) {
        this.lvl = lvl;
    }

    public Integer getCurrentXp() {
        return currentXp;
    }

    public void setCurrentXp(Integer currentXp) {
        this.currentXp = currentXp;
    }
}
