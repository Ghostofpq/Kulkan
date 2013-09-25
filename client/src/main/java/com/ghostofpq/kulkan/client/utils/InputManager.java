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
            FileOutputStream fileOut =
                    new FileOutputStream(path.toString());
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
        File confFolder = new File("./conf/");
        if (!confFolder.exists()) {
            confFolder.mkdir();
            inputMap = new InputMap();
            saveInputMap();
        } else {
            loadInputMap();
        }
    }
}
