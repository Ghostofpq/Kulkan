package com.ghostofpq.kulkan.client;

import com.ghostofpq.kulkan.client.utils.Resolution;
import com.ghostofpq.kulkan.client.utils.ResolutionRatio;
import com.ghostofpq.kulkan.entities.character.Player;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.io.*;
import java.util.*;

@Slf4j
public class ClientContext {
    public static Resolution currentResolution;
    // WINDOW
    private List<Resolution> resolutions43;
    private List<Resolution> resolutions169;
    private String pathOfClientPropertiesFile;
    // USER INFO
    private String pseudo;
    private Player player;
    private String tokenKey;

    public void init() {
        determineAvailableDisplayModes();
        loadClientProperties();
        selectDisplayMode();
        saveClientProperties();
    }

    private void selectDisplayMode() {
        if (null != currentResolution.getResolutionRatio()) {
            switch (currentResolution.getResolutionRatio()) {
                case RATIO_16_9:
                    if (!resolutions169.isEmpty()) {
                        for (Resolution resolution : resolutions169) {
                            if (resolution.getWidth() == currentResolution.getWidth() && resolution.getHeight() == currentResolution.getWidth()) {
                                log.debug("Setting display {}x{} (16/9) from client preferences", currentResolution.getWidth(), currentResolution.getWidth());
                                setCurrentResolution(resolution);
                            }
                        }
                        if (null == currentResolution) {
                            selectDefaultResolution();
                        }
                    } else {
                        selectDefaultResolution();
                    }
                    break;
                case RATIO_4_3:
                    if (!resolutions43.isEmpty()) {
                        for (Resolution resolution : resolutions43) {
                            if (resolution.getWidth() == currentResolution.getWidth() && resolution.getHeight() == currentResolution.getWidth()) {
                                log.debug("Setting display {}x{} (4/3) from client preferences", currentResolution.getWidth(), currentResolution.getWidth());
                                setCurrentResolution(resolution);
                            }
                        }
                        if (null == currentResolution) {
                            selectDefaultResolution();
                        }
                    } else {
                        selectDefaultResolution();
                    }
                    break;
                default:
                    selectDefaultResolution();
                    break;
            }
        } else {
            selectDefaultResolution();
        }
    }

    private void determineAvailableDisplayModes() {
        try {
            DisplayMode[] availableDisplayModes = Display.getAvailableDisplayModes();

            Set<Resolution> tempDisplayModes43 = new HashSet<Resolution>();
            Set<Resolution> tempDisplayModes169 = new HashSet<Resolution>();

            for (int i = 0; i < availableDisplayModes.length; i++) {
                // 4:3
                if (availableDisplayModes[i].getWidth() == 800 && availableDisplayModes[i].getHeight() == 600) {
                    tempDisplayModes43.add(new Resolution(800, 600, 0, 0, ResolutionRatio.RATIO_4_3));
                } else if (availableDisplayModes[i].getWidth() == 1024 && availableDisplayModes[i].getHeight() == 768) {
                    tempDisplayModes43.add(new Resolution(1024, 768, 0, 0, ResolutionRatio.RATIO_4_3));
                } else if (availableDisplayModes[i].getWidth() == 1280 && availableDisplayModes[i].getHeight() == 960) {
                    tempDisplayModes43.add(new Resolution(1280, 960, 0, 0, ResolutionRatio.RATIO_4_3));
                    // 16:9
                } else if (availableDisplayModes[i].getWidth() == 1024 && availableDisplayModes[i].getHeight() == 576) {
                    tempDisplayModes169.add(new Resolution(1024, 576, 0, 0, ResolutionRatio.RATIO_16_9));
                } else if (availableDisplayModes[i].getWidth() == 1280 && availableDisplayModes[i].getHeight() == 720) {
                    tempDisplayModes169.add(new Resolution(1280, 720, 0, 0, ResolutionRatio.RATIO_16_9));
                } else if (availableDisplayModes[i].getWidth() == 1600 && availableDisplayModes[i].getHeight() == 900) {
                    tempDisplayModes169.add(new Resolution(1600, 900, 0, 0, ResolutionRatio.RATIO_16_9));
                } else if (availableDisplayModes[i].getWidth() == 1920 && availableDisplayModes[i].getHeight() == 1080) {
                    tempDisplayModes169.add(new Resolution(1920, 1080, 0, 0, ResolutionRatio.RATIO_16_9));
                } else {
                    log.warn("ignore {}x{}", availableDisplayModes[i].getWidth(), availableDisplayModes[i].getHeight());
                }
            }


            resolutions169 = new ArrayList<Resolution>();
            resolutions169.addAll(tempDisplayModes169);
            Collections.sort(resolutions169);
            for (int i = 0; i < resolutions169.size(); i++) {
                log.debug("[{}] 16/9 [{}]", i, resolutions169.get(i));
            }

            resolutions43 = new ArrayList<Resolution>();
            resolutions43.addAll(tempDisplayModes43);
            Collections.sort(resolutions43);
            for (int i = 0; i < resolutions43.size(); i++) {
                log.debug("[{}] 4/3 [{}]", i, resolutions43.get(i));
            }

        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }

    private void selectDefaultResolution() {
        if (!resolutions43.isEmpty()) {
            setCurrentResolution(getSmallestResolution43());
            log.debug("Setting default display {}x{} (4/3)", currentResolution.getWidth(), currentResolution.getHeight());
        } else if (!resolutions169.isEmpty()) {
            setCurrentResolution(getSmallestResolution169());
            log.debug("Setting default display {}x{} (16/9)", currentResolution.getWidth(), currentResolution.getHeight());
        }
    }

    private Resolution getSmallestResolution169() {
        return resolutions169.get(0);
    }

    private Resolution getSmallestResolution43() {
        return resolutions43.get(0);
    }

    private void loadClientProperties() {
        FileInputStream in = null;
        try {
            in = new FileInputStream(pathOfClientPropertiesFile);
            Properties props = new Properties();
            props.load(in);
            int width;
            int height;
            int offsetX;
            int offsetY;
            ResolutionRatio resolutionRatio;
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
                offsetX = Integer.valueOf(props.getProperty("window.display.offsetX"));
            } catch (IllegalArgumentException e) {
                offsetX = 0;
            }
            try {
                offsetY = Integer.valueOf(props.getProperty("window.display.offsetY"));
            } catch (IllegalArgumentException e) {
                offsetY = 0;
            }
            try {
                resolutionRatio = ResolutionRatio.valueOf(props.getProperty("window.display.ratio"));
            } catch (IllegalArgumentException e) {
                resolutionRatio = null;
            }
            currentResolution = new Resolution(width, height, offsetX, offsetY, resolutionRatio);

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
            props.setProperty("window.display.width", String.valueOf(currentResolution.getWidth()));
            props.setProperty("window.display.height", String.valueOf(currentResolution.getHeight()));
            props.setProperty("window.display.offsetX", String.valueOf(currentResolution.getOffsetX()));
            props.setProperty("window.display.offsetY", String.valueOf(currentResolution.getOffsetY()));
            props.setProperty("window.display.ratio", String.valueOf(currentResolution.getResolutionRatio()));
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
          /*
    public void setFullscreen() {
        DisplayMode desktopDisplayMode = Display.getDesktopDisplayMode();
        log.debug("Setting fullscreen display mode to {}x{}   ({})", desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight(), desktopDisplayMode);
        boolean displayModeChanged = false;
        for (DisplayMode availableDisplayMode : displayModes169) {
            if (desktopDisplayMode.getHeight() == availableDisplayMode.getHeight() && desktopDisplayMode.getWidth() == availableDisplayMode.getWidth()) {
                setCurrentDisplayMode(desktopDisplayMode, DisplayRatio.DISPLAY_RATIO_16_9);
                xOffset = 0;
                yOffset = 0;
                displayModeChanged = true;
            }
        }
        if (!displayModeChanged) {
            for (DisplayMode availableDisplayMode : displayModes43) {
                if (desktopDisplayMode.getHeight() == availableDisplayMode.getHeight() && desktopDisplayMode.getWidth() == availableDisplayMode.getWidth()) {
                    setCurrentDisplayMode(desktopDisplayMode, DisplayRatio.DISPLAY_RATIO_4_3);
                    xOffset = 0;
                    yOffset = 0;
                    displayModeChanged = true;
                }
            }
            if (!displayModeChanged) {
                log.error("Display mode to {}x{} is not available", desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight());
            }
        }
    }         */

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

    public Resolution getCurrentResolution() {
        return currentResolution;
    }

    public void setCurrentResolution(Resolution resolution) {
        this.currentResolution = resolution;
        //setDisplayMode(width, height, false);
        log.debug("width :{}", resolution.getWidth());
        log.debug("height :{}", resolution.getHeight());
        log.debug("ratio :{}", resolution.getResolutionRatio());
    }

    public List<Resolution> getResolutions43() {
        return resolutions43;
    }

    public List<Resolution> getResolutions169() {
        return resolutions169;
    }

    public String getPathOfClientPropertiesFile() {
        return pathOfClientPropertiesFile;
    }

    public void setPathOfClientPropertiesFile(String pathOfClientPropertiesFile) {
        this.pathOfClientPropertiesFile = pathOfClientPropertiesFile;
    }
}
