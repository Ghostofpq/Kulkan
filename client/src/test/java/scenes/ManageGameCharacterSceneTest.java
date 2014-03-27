package scenes;


import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.ClientContext;
import com.ghostofpq.kulkan.client.scenes.ManageGameCharacterScene;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.character.Gender;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.clan.ClanType;
import com.ghostofpq.kulkan.entities.inventory.ItemFactory;
import com.ghostofpq.kulkan.entities.inventory.item.Item;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.junit.Ignore;
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
    @Ignore
    public void manageGameCharacterSceneTest() {
        System.setProperty("org.lwjgl.librarypath", new File("natives").getAbsolutePath());
        ApplicationContext context = new ClassPathXmlApplicationContext("client-context.xml");
        Client g = ((Client) context.getBean("client"));
        ClientContext ctx = ((ClientContext) context.getBean("clientContext"));

        ObjectId id = new ObjectId();
        g.init();
        Player player = new Player("Marcel");
        player.getInventory().add("001", 1);
        player.getInventory().add("002", 1);
        player.getInventory().add("003", 1);
        player.getInventory().add("004", 1);
        player.getInventory().add("005", 1);
        player.getInventory().add("006", 1);
        player.getInventory().add("007", 1);
        player.getInventory().add("008", 1);
        player.getInventory().add("009", 1);
        GameCharacter gameCharacter = new GameCharacter("Johny boy le killeur de zoulettes", ClanType.GORILLA, Gender.MALE);
        gameCharacter.setId(id);
        gameCharacter.setPlayer(player);
        gameCharacter.gainXp(20);

        Item helm = ItemFactory.createItem("001");
        gameCharacter.equip(helm);
        player.getTeam().add(gameCharacter);
        ctx.setPlayer(player);
        ctx.setSelectedCharacterId(id);

        ManageGameCharacterScene scene = ((ManageGameCharacterScene) context.getBean("manageGameCharacterScene"));
        g.setCurrentScene(scene);
        try {
            g.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
