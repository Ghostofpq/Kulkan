package com.ghostofpq.kulkan.client.utils;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

@Slf4j
public class GraphicsManager {
    private static volatile GraphicsManager instance = null;
    private final float STEP_ROTATION = 2f;
    private final float STEP_TRANSLATION = 0.2f;
    private float originX;
    private float originY;
    private float originZ;
    private PointOfView currentPointOfView;
    private float scale;
    private float zscale;
    private float scaleToGo;
    private float focusXToGo;
    private float focusYToGo;
    private float focusZToGo;
    private float rotationToGo;
    private float theta;
    private boolean isBusy;

    private GraphicsManager() {
        originZ = 0;
        originX = 0;
        originY = 0;
        scaleToGo = 0;
        rotationToGo = 0f;
        scale = 0.2f;
        currentPointOfView = PointOfView.SOUTH;
        theta = 0;
        isBusy = false;
    }

    public static GraphicsManager getInstance() {
        if (instance == null) {
            synchronized (GraphicsManager.class) {
                if (instance == null) {
                    instance = new GraphicsManager();
                }
            }
        }
        return instance;
    }

    public void setupLights() {
        FloatBuffer matSpecular;
        FloatBuffer lightPosition;
        FloatBuffer whiteLight;
        FloatBuffer lModelAmbient;

        matSpecular = BufferUtils.createFloatBuffer(4);
        matSpecular.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();

        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.5f).put(0.5f).put(5.0f).put(0.2f).flip();

        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();

        lModelAmbient = BufferUtils.createFloatBuffer(4);
        lModelAmbient.put(0.5f).put(0.5f).put(0.5f).put(1.0f).flip();

        GL11.glShadeModel(GL11.GL_SMOOTH);

        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, lightPosition);
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, whiteLight);
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, whiteLight);
        GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, lModelAmbient);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);

    }

    public void updateLights() {
        FloatBuffer lightPosition;
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.5f).put(0.5f).put(5.0f).put(0.2f).flip();
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, lightPosition);
    }

    public void make2D() {
        // Remove the Z axis

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0, Client.getInstance().getWidth(), Client.getInstance().getHeight(), 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
    }

    public void make3D() {

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    public void ready3D() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();

        GL11.glOrtho(-1, 1, -1, 1, -10, 1000);

        GL11.glRotatef((float) Math.toDegrees(Math.atan(0.5)), 1, 0, 0);
        GL11.glRotatef(-45.0f, 0, 1, 0);
        GL11.glTranslatef(0, -0.5f, 0);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
    }

    public void zoomIn() {
        scale += 0.1 * scale;
    }

    public void zoomOut() {
        scale -= 0.1 * scale;
    }

    public void requestCenterPosition(Position position) {
        focusXToGo = position.getX() - originX;
        focusYToGo = position.getY() - originY;
        focusZToGo = position.getZ() - originZ;
    }

    public void requestPointOfView(PointOfView v) {
        if (!v.equals(currentPointOfView)) {
            theta = -45f;
            switch (currentPointOfView) {
                case SOUTH:
                    switch (v) {
                        case WEST:
                            rotationToGo = -90f;
                            break;
                        case NORTH:
                            rotationToGo = -180f;
                            break;
                        case EAST:
                            rotationToGo = 90f;
                            break;
                    }
                    break;
                case WEST:
                    switch (v) {
                        case SOUTH:
                            rotationToGo = 90f;
                            break;
                        case NORTH:
                            rotationToGo = -90f;
                            break;
                        case EAST:
                            rotationToGo = -180f;
                            break;
                    }
                    break;
                case NORTH:
                    switch (v) {
                        case SOUTH:
                            rotationToGo = -180f;
                            break;
                        case WEST:
                            rotationToGo = 90f;
                            break;
                        case EAST:
                            rotationToGo = -90f;
                            break;
                    }
                    break;
                case EAST:
                    switch (v) {
                        case SOUTH:
                            rotationToGo = -90f;
                            break;
                        case WEST:
                            rotationToGo = -180f;
                            break;
                        case NORTH:
                            rotationToGo = 90f;
                            break;
                    }
                    break;
            }
        }
    }

    public boolean update3DMovement() {
        make3D();
        focusXToGo = (float) Math.round(focusXToGo * 100) / 100;
        focusYToGo = (float) Math.round(focusYToGo * 100) / 100;
        focusZToGo = (float) Math.round(focusZToGo * 100) / 100;
        rotationToGo = (float) Math.round(rotationToGo * 100) / 100;
        boolean result = false;
        if (focusXToGo != 0) {
            if (focusXToGo < 0) {
                originX -= STEP_TRANSLATION;
                focusXToGo += STEP_TRANSLATION;
            } else if (focusXToGo > 0) {
                originX += STEP_TRANSLATION;
                focusXToGo -= STEP_TRANSLATION;
            }
            result = true;
        }
        if (focusYToGo != 0) {
            if (focusYToGo < 0) {
                originY -= STEP_TRANSLATION;
                focusYToGo += STEP_TRANSLATION;
            } else if (focusYToGo > 0) {
                originY += STEP_TRANSLATION;
                focusYToGo -= STEP_TRANSLATION;
            }
            result = true;
        }
        if (focusZToGo != 0) {
            if (focusZToGo < 0) {
                originZ -= STEP_TRANSLATION;
                focusZToGo += STEP_TRANSLATION;
            } else if (focusZToGo > 0) {
                originZ += STEP_TRANSLATION;
                focusZToGo -= STEP_TRANSLATION;
            }
            result = true;
        }

        if (rotationToGo != 0) {

            GL11.glTranslated(scale / 2, 0, scale / 2);
            if (rotationToGo < 0) {
                GL11.glRotatef(STEP_ROTATION, 0, 1, 0);
                updateLights();
                if (rotationToGo + STEP_ROTATION > 0) {
                    rotationToGo = 0;
                } else {
                    rotationToGo += STEP_ROTATION;
                }
            }
            if (rotationToGo > 0) {
                GL11.glRotatef(-STEP_ROTATION, 0, 1, 0);
                updateLights();
                if (rotationToGo - STEP_ROTATION < 0) {
                    rotationToGo = 0;
                } else {
                    rotationToGo -= STEP_ROTATION;
                }
            }
            GL11.glTranslated(-scale / 2, 0, -scale / 2);
            theta += STEP_ROTATION;
            if (theta >= 0) {
                theta -= 90f;
                if (rotationToGo < 0) {
                    switch (currentPointOfView) {
                        case SOUTH:
                            currentPointOfView = PointOfView.WEST;
                            break;
                        case WEST:
                            currentPointOfView = PointOfView.NORTH;
                            break;
                        case NORTH:
                            currentPointOfView = PointOfView.EAST;
                            break;
                        case EAST:
                            currentPointOfView = PointOfView.SOUTH;
                            break;
                    }
                } else if (rotationToGo > 0) {
                    switch (currentPointOfView) {
                        case SOUTH:
                            currentPointOfView = PointOfView.EAST;
                            break;
                        case WEST:
                            currentPointOfView = PointOfView.SOUTH;
                            break;
                        case NORTH:
                            currentPointOfView = PointOfView.WEST;
                            break;
                        case EAST:
                            currentPointOfView = PointOfView.NORTH;
                            break;
                    }
                }
            }
            result = true;
        }
        if (result) {
            //log.debug("[{}] [{}] [{}] / [{}]", focusXToGo, focusYToGo, focusZToGo, rotationToGo);
        }
        isBusy = result;
        return result;
    }

    /**
     * Getters and Setters
     */

    public float getOriginX() {
        return originX;
    }

    public float getOriginY() {
        return originY;
    }

    public float getOriginZ() {
        return originZ;
    }

    public PointOfView getCurrentPointOfView() {
        return currentPointOfView;
    }

    public float getScale() {
        return scale;
    }

    public boolean isBusy() {
        return isBusy;
    }
}

