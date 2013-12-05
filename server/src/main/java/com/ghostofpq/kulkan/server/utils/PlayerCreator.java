package com.ghostofpq.kulkan.server.utils;

import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.character.Gender;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.clan.ClanType;
import org.bson.types.ObjectId;

public class PlayerCreator {
    public static void main(String[] argv) {
        Player player1 = new Player("1");

        GameCharacter char1 = new GameCharacter(new ObjectId(), player1, "ghostofpq1Human", ClanType.HUMAN, Gender.MALE);
        GameCharacter char2 = new GameCharacter(new ObjectId(), player1, "ghostofpq2Elve", ClanType.ELVE, Gender.FEMALE);
        GameCharacter char3 = new GameCharacter(new ObjectId(), player1, "ghostofpq3Dwarf", ClanType.DWARF, Gender.MALE);

        player1.getTeam().add(char1);
        player1.getTeam().add(char2);
        player1.getTeam().add(char3);

        SaveManager saveManager = SaveManager.getInstance();
        saveManager.savePlayer(player1);

        Player player2 = new Player("2");

        GameCharacter char4 = new GameCharacter(new ObjectId(), player2, "azerty1Human", ClanType.HUMAN, Gender.MALE);
        GameCharacter char5 = new GameCharacter(new ObjectId(), player2, "azerty2Elve", ClanType.ELVE, Gender.FEMALE);
        GameCharacter char6 = new GameCharacter(new ObjectId(), player2, "azerty3Dwarf", ClanType.DWARF, Gender.MALE);

        player2.getTeam().add(char4);
        player2.getTeam().add(char5);
        player2.getTeam().add(char6);


        saveManager.savePlayer(player2);
    }
}
