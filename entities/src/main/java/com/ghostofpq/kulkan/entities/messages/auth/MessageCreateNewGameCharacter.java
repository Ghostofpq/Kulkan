package com.ghostofpq.kulkan.entities.messages.auth;

import com.ghostofpq.kulkan.entities.character.Gender;
import com.ghostofpq.kulkan.entities.clan.Clan;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

public class MessageCreateNewGameCharacter extends ClientMessage {
    private Clan clan;
    private Gender gender;
    private String name;
    private String username;

    public MessageCreateNewGameCharacter(String keyToken, String username, Clan clan, Gender gender, String name) {
        type = MessageType.CREATE_NEW_GAME_CHARACTER_REQUEST;
        this.keyToken = keyToken;
        this.username = username;
        this.clan = clan;
        this.gender = gender;
        this.name = name;
    }

    public Clan getClan() {
        return clan;
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
