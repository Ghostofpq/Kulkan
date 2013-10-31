package com.ghostofpq.kulkan.server.utils;

import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.character.Gender;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.race.RaceType;

public class PlayerCreator {
    public static void main(String[] argv) {
        Player player1 = new Player("1");

        GameCharacter char1 = new GameCharacter(player1, "ghostofpq1Human", RaceType.HUMAN, Gender.MALE);
        GameCharacter char2 = new GameCharacter(player1, "ghostofpq2Elve", RaceType.ELVE, Gender.FEMALE);
        GameCharacter char3 = new GameCharacter(player1, "ghostofpq3Dwarf", RaceType.DWARF, Gender.MALE);

        player1.getTeam().getTeam().add(char1);
        player1.getTeam().getTeam().add(char2);
        player1.getTeam().getTeam().add(char3);

        SaveManager saveManager = SaveManager.getInstance();
        saveManager.savePlayer(player1);

        Player player2 = new Player("2");

        GameCharacter char4 = new GameCharacter(player2, "azerty1Human", RaceType.HUMAN, Gender.MALE);
        GameCharacter char5 = new GameCharacter(player2, "azerty2Elve", RaceType.ELVE, Gender.FEMALE);
        GameCharacter char6 = new GameCharacter(player2, "azerty3Dwarf", RaceType.DWARF, Gender.MALE);

        player2.getTeam().getTeam().add(char4);
        player2.getTeam().getTeam().add(char5);
        player2.getTeam().getTeam().add(char6);


        saveManager.savePlayer(player2);
    }
}