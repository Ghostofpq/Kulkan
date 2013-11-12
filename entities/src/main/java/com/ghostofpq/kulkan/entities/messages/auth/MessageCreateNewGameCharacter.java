package com.ghostofpq.kulkan.entities.messages.auth;

import com.ghostofpq.kulkan.entities.character.Gender;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;
import com.ghostofpq.kulkan.entities.race.RaceType;

public class MessageCreateNewGameCharacter extends ClientMessage {
    private RaceType raceType;
    private Gender gender;
    private String name;
    private String username;

    public MessageCreateNewGameCharacter(String keyToken, String username, RaceType raceType, Gender gender, String name) {
        type = MessageType.CHARACTER_ACTION_ATTACK;
        this.keyToken = keyToken;
        this.username = username;
        this.raceType = raceType;
        this.gender = gender;
        this.name = name;
    }

    public RaceType getRaceType() {
        return raceType;
    }

    public Gender getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }
}
