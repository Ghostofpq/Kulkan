package com.ghostofpq.kulkan.client.utils;

import java.io.*;

public class InputManager {

    private static InputManager instance = new InputManager();
    private InputMap inputMap;

    private InputManager() {
        checkDirectoriesExist();
    }

    public static InputManager getInstance() {
        return instance;
    }

    public InputMap.Input getInput(int inputKey) {
        return inputMap.getInput(inputKey);
    }

    public void loadInputMap() {
        String path = new StringBuilder().append("./conf/inputs.map").toString();
        try {
            FileInputStream fileIn =
                    new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            inputMap = (InputMap) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
    }

    public void saveInputMap() {
        try {
            String path = new StringBuilder().append("./conf/inputs.map").toString();
            File confFile = new File(path);
            confFile.createNewFile();
            FileOutputStream fileOut =
                    new FileOutputStream(confFile);
            ObjectOutputStream out =
                    new ObjectOutputStream(fileOut);
            out.writeObject(inputMap);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public void checkDirectoriesExist() {
        File inputMapFile = new File("./conf/inputs.map");
        if (!inputMapFile.exists()) {
            inputMap = new InputMap();
            saveInputMap();
        } else {
            loadInputMap();
        }
    }
}
