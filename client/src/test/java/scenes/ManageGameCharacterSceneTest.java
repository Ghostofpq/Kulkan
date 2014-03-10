package scenes;


import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.ClientContext;
import com.ghostofpq.kulkan.client.scenes.ManageGameCharacterScene;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.character.Gender;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.clan.ClanType;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

@Slf4j
@RunWith(JUnit4.class)
public class ManageGameCharacterSceneTest {

    @Test
    public void test() {
        System.setProperty("org.lwjgl.librarypath", new File("natives").getAbsolutePath());
        ApplicationContext context = new ClassPathXmlApplicationContext("client-context.xml");
        Client g = ((Client) context.getBean("client"));
        ManageGameCharacterScene scene = ((ManageGameCharacterScene) context.getBean("manageGameCharacterScene"));
        ClientContext ctx = ((ClientContext) context.getBean("clientContext"));

        ObjectId id = new ObjectId();
        g.init();
        Player player = new Player("Marcel");
        GameCharacter gameCharacter = new GameCharacter("John", ClanType.GORILLA, Gender.MALE);
        gameCharacter.setId(id);
        gameCharacter.setPlayer(player);
        gameCharacter.gainXp(20);
        player.getTeam().add(gameCharacter);
        ctx.setPlayer(player);
        ctx.setSelectedCharacterId(id);

        g.setCurrentScene(scene);
        try {
            g.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
