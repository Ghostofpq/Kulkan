package com.ghostofpq.kulkan.client;

import com.ghostofpq.kulkan.entities.character.Player;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.io.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

@Slf4j
public class ClientContext {
    // WINDOW
    public static int height;
    public static int width;
    public static DisplayRatio displayRatio;
    private Set<DisplayMode> displayModes43 = new HashSet<DisplayMode>();
    private Set<DisplayMode> displayModes169 = new HashSet<DisplayMode>();
    private DisplayMode currentDisplay;
    private String pathOfClientPropertiesFile;
    // USER INFO
    private String pseudo;
    private Player player;
    private String tokenKey;

    public static DisplayRatio getDisplayRatio() {
        return displayRatio;
    }

    public static void setDisplayRatio(DisplayRatio displayRatio) {
        ClientContext.displayRatio = displayRatio;
    }

    public void init() {
        determineAvailableDisplayModes();
        loadClientProperties();
        selectDisplayMode();
        saveClientProperties();
    }

    private void selectDisplayMode() {
        currentDisplay = null;
        if (null != displayRatio) {
            switch (displayRatio) {
                case DISPLAY_RATIO_16_9:
                    if (!displayModes169.isEmpty()) {
                        for (DisplayMode displayMode : displayModes169) {
                            if (displayMode.getWidth() == width && displayMode.getHeight() == height) {
                                log.debug("Setting display {}x{} (16/9) from client preferences", width, height);
                                setCurrentDisplay(displayMode);
                            }
                        }
                        if (null == currentDisplay) {
                            selectDefaultDisplayMode();
                        }
                    } else {
                        selectDefaultDisplayMode();
                    }
                    break;
                case DISPLAY_RATIO_4_3:
                    if (!displayModes43.isEmpty()) {
                        for (DisplayMode displayMode : displayModes43) {
                            if (displayMode.getWidth() == width && displayMode.getHeight() == height) {
                                log.debug("Setting display {}x{} (4/3) from client preferences", width, height);
                                setCurrentDisplay(displayMode);
                            }
                        }
                        if (null == currentDisplay) {
                            selectDefaultDisplayMode();
                        }
                    } else {
                        selectDefaultDisplayMode();
                    }
                    break;
                default:
                    selectDefaultDisplayMode();
                    break;
            }
        } else {
            selectDefaultDisplayMode();
        }
    }

    private void determineAvailableDisplayModes() {
        try {
            DisplayMode[] availableDisplayModes = Display.getAvailableDisplayModes();

            for (int i = 0; i < availableDisplayModes.length; i++) {
                // 4:3
                if (availableDisplayModes[i].getWidth() == 800 && availableDisplayModes[i].getHeight() == 600) {
                    displayModes43.add(new DisplayMode(800, 600));
                } else if (availableDisplayModes[i].getWidth() == 1024 && availableDisplayModes[i].getHeight() == 768) {
                    displayModes43.add(new DisplayMode(1024, 768));
                } else if (availableDisplayModes[i].getWidth() == 1280 && availableDisplayModes[i].getHeight() == 960) {
                    displayModes43.add(new DisplayMode(1280, 960));
                    // 16:9
                } else if (availableDisplayModes[i].getWidth() == 1024 && availableDisplayModes[i].getHeight() == 576) {
                    displayModes169.add(new DisplayMode(1024, 576));
                } else if (availableDisplayModes[i].getWidth() == 1280 && availableDisplayModes[i].getHeight() == 720) {
                    displayModes169.add(new DisplayMode(1280, 720));
                } else if (availableDisplayModes[i].getWidth() == 1600 && availableDisplayModes[i].getHeight() == 900) {
                    displayModes169.add(new DisplayMode(1600, 900));
                } else if (availableDisplayModes[i].getWidth() == 1920 && availableDisplayModes[i].getHeight() == 1080) {
                    displayModes169.add(new DisplayMode(1920, 1080));
                }
            }

            for (DisplayMode dm : displayModes43) {
                log.debug("4/3 [{}]", dm);
            }
            for (DisplayMode dm : displayModes169) {
                log.debug("16/9 [{}]", dm);
            }
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }

    private void selectDefaultDisplayMode() {
        if (!displayModes169.isEmpty()) {
            setCurrentDisplay(getSmallestDisplay169());
            displayRatio = DisplayRatio.DISPLAY_RATIO_16_9;
            log.debug("Setting default display {}x{} (16/9)", currentDisplay.getWidth(), currentDisplay.getHeight());
        } else if (!displayModes43.isEmpty()) {
            setCurrentDisplay(getSmallestDisplay43());
            displayRatio = DisplayRatio.DISPLAY_RATIO_4_3;
            log.debug("Setting default display {}x{} (4/3)", currentDisplay.getWidth(), currentDisplay.getHeight());
        }
    }

    private DisplayMode getSmallestDisplay169() {
        DisplayMode smallestDisplayMode169 = null;
        for (DisplayMode displayMode : displayModes169) {
            if (null == smallestDisplayMode169) {
                smallestDisplayMode169 = displayMode;
            } else if (smallestDisplayMode169.getHeight() > displayMode.getHeight()) {
                smallestDisplayMode169 = displayMode;
            }
        }
        return smallestDisplayMode169;
    }

    private DisplayMode getSmallestDisplay43() {
        DisplayMode smallestDisplayMode43 = null;
        for (DisplayMode displayMode : displayModes43) {
            if (null == smallestDisplayMode43) {
                smallestDisplayMode43 = displayMode;
            } else if (smallestDisplayMode43.getHeight() > displayMode.getHeight()) {
                smallestDisplayMode43 = displayMode;
            }
        }
        return smallestDisplayMode43;
    }

    private void loadClientProperties() {
        FileInputStream in = null;
        try {
            in = new FileInputStream(pathOfClientPropertiesFile);
            Properties props = new Properties();
            props.load(in);

            try {
                width = Integer.valueOf(props.getProperty("window.display.width"));
            } catch (IllegalArgumentException e) {
                width = 0;
            }
            try {
                height = Integer.valueOf(props.getProperty("window.display.height"));
            } catch (IllegalArgumentException e) {
                height = 0;
            }
            try {
                displayRatio = DisplayRatio.valueOf(props.getProperty("window.display.ratio"));
            } catch (IllegalArgumentException e) {
                displayRatio = null;
            }
            pseudo = props.getProperty("client.pseudo");

            log.debug("Loading properties {}", props);
        } catch (FileNotFoundException e) {
            log.warn("No client.properties file.");
            File clientPropertyFile = new File(pathOfClientPropertiesFile);
            try {
                clientPropertyFile.createNewFile();
            } catch (IOException e1) {
                log.error("Could not create file : {}", pathOfClientPropertiesFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveClientProperties() {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(pathOfClientPropertiesFile);
            Properties props = new Properties();
            props.setProperty("window.display.width", String.valueOf(width));
            props.setProperty("window.display.height", String.valueOf(height));
            props.setProperty("window.display.ratio", String.valueOf(displayRatio));
            props.setProperty("client.pseudo", String.valueOf(pseudo));
            log.debug("Saving properties {}", props);

            props.store(out, null);
        } catch (FileNotFoundException e) {
            log.error("No client.properties file to save conf.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public Set<DisplayMode> getDisplayModes43() {
        return displayModes43;
    }

    public void setDisplayModes43(Set<DisplayMode> displayModes43) {
        this.displayModes43 = displayModes43;
    }

    public Set<DisplayMode> getDisplayModes169() {
        return displayModes169;
    }

    public void setDisplayModes169(Set<DisplayMode> displayModes169) {
        this.displayModes169 = displayModes169;
    }

    public String getPathOfClientPropertiesFile() {
        return pathOfClientPropertiesFile;
    }

    public void setPathOfClientPropertiesFile(String pathOfClientPropertiesFile) {
        this.pathOfClientPropertiesFile = pathOfClientPropertiesFile;
    }

    public DisplayMode getCurrentDisplay() {
        return currentDisplay;
    }

    public void setCurrentDisplay(DisplayMode currentDisplay) {
        this.currentDisplay = currentDisplay;
        width = currentDisplay.getWidth();
        height = currentDisplay.getHeight();
        log.debug("Setting display mode to {}x{}", currentDisplay.getWidth(), currentDisplay.getHeight());
    }

    public enum DisplayRatio {
        DISPLAY_RATIO_4_3, DISPLAY_RATIO_16_9;
    }
}
