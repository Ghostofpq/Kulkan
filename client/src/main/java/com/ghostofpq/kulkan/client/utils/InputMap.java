package com.ghostofpq.kulkan.client.utils;

import org.lwjgl.input.Keyboard;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class InputMap implements Serializable {

    public enum Input {
        UP,
        DOWN,
        LEFT,
        RIGHT,

        VALIDATE,
        CANCEL,

        ROTATE_LEFT,
        ROTATE_RIGHT,

        ZOOM_IN,
        ZOOM_OUT,

        SWITCH
    }

    private Map<Integer, Input> inputMap;

    public InputMap() {
        inputMap = new HashMap<Integer, Input>();

        inputMap.put(Keyboard.KEY_UP, Input.UP);
        inputMap.put(Keyboard.KEY_DOWN, Input.DOWN);
        inputMap.put(Keyboard.KEY_LEFT, Input.LEFT);
        inputMap.put(Keyboard.KEY_RIGHT, Input.RIGHT);
        inputMap.put(Keyboard.KEY_RETURN, Input.VALIDATE);
        inputMap.put(Keyboard.KEY_BACK, Input.CANCEL);
        inputMap.put(Keyboard.KEY_A, Input.ROTATE_LEFT);
        inputMap.put(Keyboard.KEY_Z, Input.ROTATE_RIGHT);
        inputMap.put(Keyboard.KEY_Q, Input.ZOOM_IN);
        inputMap.put(Keyboard.KEY_S, Input.ZOOM_OUT);
        inputMap.put(Keyboard.KEY_TAB, Input.SWITCH);
    }
}
