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
    }
}
