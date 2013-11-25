package com.ghostofpq.kulkan.entities.messages.user;

import com.ghostofpq.kulkan.entities.character.Gender;
import com.ghostofpq.kulkan.entities.clan.ClanType;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

public class MessageCreateNewGameCharacter extends ClientMessage {
    private ClanType clanType;
    private Gender gender;
    private String name;
    private String username;

    public MessageCreateNewGameCharacter(String keyToken, String username, ClanType clanType, Gender gender, String name) {
        type = MessageType.CREATE_NEW_GAME_CHARACTER_REQUEST;
        this.keyToken = keyToken;
        this.username = username;
        this.clanType = clanType;
        this.gender = gender;
        this.name = name;
    }

    public ClanType getClanType() {
        return clanType;
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
