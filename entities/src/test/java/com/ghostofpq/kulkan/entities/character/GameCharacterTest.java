package com.ghostofpq.kulkan.entities.character;

import com.ghostofpq.kulkan.entities.clan.ClanType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@Slf4j
@RunWith(JUnit4.class)
public class GameCharacterTest {

    @Test
    public void gameCharacterCreationTest() {
        String playerName = "Chan";
        ClanType clanType = ClanType.GORILLA;
        Gender gender = Gender.MALE;

        GameCharacter gameCharacter = new GameCharacter(playerName, clanType, gender);

        assert (gameCharacter.getName().equals(playerName));
        assert (gameCharacter.getClan().getClanType().equals(clanType));
        assert (gameCharacter.getGender().equals(gender));

        assert (gameCharacter.getJobs().size() == 1);
        assert (gameCharacter.getJobs().get(0).getJobType().equals(gameCharacter.getClan().getBaseJob()));

        assert (null == gameCharacter.getPlayer());
        assert (null == gameCharacter.getId());
    }

    @Test
    public void xpTest() {
        String playerName = "Chan";
        ClanType clanType = ClanType.GORILLA;
        Gender gender = Gender.MALE;

        GameCharacter gameCharacter = new GameCharacter(playerName, clanType, gender);

        gameCharacter.gainXp(1);

        assert (gameCharacter.getLevel() == 1);
    }

    @Test
    public void xpTestLvlUp() {
        String playerName = "Chan";
        ClanType clanType = ClanType.GORILLA;
        Gender gender = Gender.MALE;

        GameCharacter gameCharacter = new GameCharacter(playerName, clanType, gender);

        gameCharacter.gainXp(100);

        assert (gameCharacter.getLevel() == 2);
    }

    @Test
    public void addHealthTestOverTheLimit() {
        String playerName = "Chan";
        ClanType clanType = ClanType.GORILLA;
        Gender gender = Gender.MALE;

        GameCharacter gameCharacter = new GameCharacter(playerName, clanType, gender);

        gameCharacter.addHealthPoint(100);

        assert (gameCharacter.getCurrentHealthPoint() == gameCharacter.getMaxHealthPoint());
    }

    @Test
    public void addHealthTestUnderTheLimit() {
        String playerName = "Chan";
        ClanType clanType = ClanType.GORILLA;
        Gender gender = Gender.MALE;

        GameCharacter gameCharacter = new GameCharacter(playerName, clanType, gender);

        gameCharacter.addHealthPoint(-200);

        assert (gameCharacter.getCurrentHealthPoint() == 0);
        assert (!gameCharacter.isAlive());
    }

    @Test
    public void addManaTestOverTheLimit() {
        String playerName = "Chan";
        ClanType clanType = ClanType.GORILLA;
        Gender gender = Gender.MALE;

        GameCharacter gameCharacter = new GameCharacter(playerName, clanType, gender);

        gameCharacter.addManaPoint(100);

        assert (gameCharacter.getCurrentManaPoint() == gameCharacter.getMaxManaPoint());
    }

    @Test
    public void addManaTestUnderTheLimit() {
        String playerName = "Chan";
        ClanType clanType = ClanType.GORILLA;
        Gender gender = Gender.MALE;

        GameCharacter gameCharacter = new GameCharacter(playerName, clanType, gender);

        gameCharacter.addManaPoint(-100);

        assert (gameCharacter.getCurrentManaPoint() == 0);
    }
}
