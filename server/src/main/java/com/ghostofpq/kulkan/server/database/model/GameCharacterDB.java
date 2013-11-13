package com.ghostofpq.kulkan.server.database.model;

import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.character.Gender;
import com.ghostofpq.kulkan.entities.clan.ClanType;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class GameCharacterDB {
    @Id
    private ObjectId id;
    private String name;
    private Gender gender;
    private ClanType clanType;
    private Integer lvl;
    private Integer currentXp;

    public GameCharacterDB() {
    }

    public GameCharacterDB(String name, Gender gender, ClanType clanType, Integer lvl, Integer currentXp) {
        this.id = new ObjectId();
        this.name = name;
        this.gender = gender;
        this.clanType = clanType;
        this.lvl = lvl;
        this.currentXp = currentXp;
    }

    public GameCharacterDB(GameCharacter gameCharacter) {
        this.name = gameCharacter.getName();
        this.gender = gameCharacter.getGender();
        this.clanType = gameCharacter.getClan().getRaceType();
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

    public ClanType getClanType() {
        return clanType;
    }

    public void setClanType(ClanType clanType) {
        this.clanType = clanType;
    }
}
