package com.ghostofpq.kulkan.game.utils;

import com.ghostofpq.kulkan.entities.battlefield.Battlefield;
import com.ghostofpq.kulkan.entities.character.Player;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class SaveManager {
    private static SaveManager instance = new SaveManager();

    private SaveManager() {
        checkDirectoriesExist();
    }

    public static SaveManager getInstance() {
        return instance;
    }

    public List<String> checkFolderForPlayers() throws IOException {
        File players = new File("./saves/players/");
        return Arrays.asList(players.list());
    }

    public void savePlayer(Player player) {
        try {
            StringBuilder path = new StringBuilder();
            path.append("./saves/players/");
            path.append(player.getPseudo());
            path.append(".pla");
            FileOutputStream fileOut =
                    new FileOutputStream(path.toString());
            ObjectOutputStream out =
                    new ObjectOutputStream(fileOut);
            out.writeObject(player);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public Player loadPlayer(String playerPseudo) {
        StringBuilder path = new StringBuilder();
        path.append("./saves/players/");
        path.append(playerPseudo);
        if (!playerPseudo.endsWith(".pla")) {
            path.append(".pla");
        }
        Player player = null;
        try {
            FileInputStream fileIn =
                    new FileInputStream(path.toString());
            ObjectInputStream in = new ObjectInputStream(fileIn);
            player = (Player) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
        return player;
    }

    public void saveMap(Battlefield battlefield, String mapName) {
        try {
            StringBuilder path = new StringBuilder();
            path.append("./saves/maps/");
            path.append(mapName);
            if (!mapName.endsWith(".map")) {
                path.append(".map");
            }
            FileOutputStream fileOut =
                    new FileOutputStream(path.toString());
            ObjectOutputStream out =
                    new ObjectOutputStream(fileOut);
            out.writeObject(battlefield);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public Battlefield loadMap(String mapName) {
        StringBuilder path = new StringBuilder();
        path.append("./saves/maps/");
        path.append(mapName);
        if (!mapName.endsWith(".map")) {
            path.append(".map");
        }
        Battlefield battlefield = null;
        try {
            FileInputStream fileIn =
                    new FileInputStream(path.toString());
            ObjectInputStream in = new ObjectInputStream(fileIn);
            battlefield = (Battlefield) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
        return battlefield;
    }

    public void checkDirectoriesExist() {
        File main = new File("./saves/");
        if (!main.exists()) {
            main.mkdir();
        }
        File players = new File("./saves/players/");
        if (!players.exists()) {
            players.mkdir();
        }
        File maps = new File("./saves/maps/");
        if (!maps.exists()) {
            maps.mkdir();
        }
    }
}
