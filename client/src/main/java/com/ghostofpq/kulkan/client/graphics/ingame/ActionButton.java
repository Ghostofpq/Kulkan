package com.ghostofpq.kulkan.client.graphics.ingame;

import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.client.utils.TextureKey;
import com.ghostofpq.kulkan.client.utils.TextureManager;
import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.commons.PositionAbsolute;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

public class ActionButton extends DrawableObject {

    private ActionButtonType actionButtonType;
    private TextureKey textureKey;
    private TextureKey textureKeyForMouseDetection;
    private TextureKey textureKeyHovered;
    private boolean hovered;

    private float r;
    private float g;
    private float b;

    private PositionAbsolute corner1;
    private PositionAbsolute corner2;
    private PositionAbsolute corner3;
    private PositionAbsolute corner4;

    public ActionButton(Position position, ActionButtonType actionButtonType) {
        this.position = position;
        this.positionAbsolute = this.position.toAbsolute();
        this.actionButtonType = actionButtonType;
        switch (this.actionButtonType) {
            case MOVE:
                textureKey = TextureKey.ACTION_1;
                textureKeyForMouseDetection = TextureKey.ACTION_1_BACK;
                textureKeyHovered = TextureKey.ACTION_1_HOVERED;
                break;
        }
        this.hovered = false;
    }

    private void calculateCorners() {
        PointOfView pointOfView = GraphicsManager.getInstance().getCurrentPointOfView();

        if (pointOfView.equals(PointOfView.SOUTH)) {
            corner1 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.1f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 3f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.9f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner2 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.9f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 3f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.1f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner3 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.9f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.1f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner4 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.1f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.9f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
        } else if (pointOfView.equals(PointOfView.NORTH)) {
            corner1 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.9f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 3f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.1f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner2 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.1f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 3f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.9f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner3 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.1f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.9f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner4 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.9f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.1f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
        } else if (pointOfView.equals(PointOfView.WEST)) {
            corner1 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.1f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 3f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.1f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner2 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.9f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 3f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.9f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner3 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.9f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.9f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner4 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.1f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.1f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
        } else if (pointOfView.equals(PointOfView.EAST)) {
            corner1 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.9f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 3f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.9f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner2 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.1f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 3f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.1f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner3 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.1f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.1f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner4 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.9f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.9f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
        }
    }

    @Override
    public void draw() {
        calculateCorners();

        GL11.glColor4f(1f, 1f, 1f, 1f);
        Texture texture;
        if (isHovered()) {
            texture = TextureManager.getInstance().getTexture(textureKeyHovered);
        } else {
            texture = TextureManager.getInstance().getTexture(textureKey);
        }
        texture.bind();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex3d(corner1.getX(), corner1.getY(), corner1.getZ());
        GL11.glTexCoord2d(texture.getWidth(), 0);
        GL11.glVertex3d(corner2.getX(), corner2.getY(), corner2.getZ());
        GL11.glTexCoord2d(texture.getWidth(), texture.getHeight());
        GL11.glVertex3d(corner3.getX(), corner3.getY(), corner3.getZ());
        GL11.glTexCoord2d(0, texture.getHeight());
        GL11.glVertex3d(corner4.getX(), corner4.getY(), corner4.getZ());
        GL11.glEnd();
    }

    public void renderForMousePosition(float r, float g, float b) {
        calculateCorners();

        this.r = r;
        this.g = g;
        this.b = b;

        GL11.glColor4f(r / 255f, g / 255f, b / 255f, 1f);
        Texture texture = TextureManager.getInstance().getTexture(textureKeyForMouseDetection);
        texture.bind();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex3d(corner1.getX(), corner1.getY(), corner1.getZ());
        GL11.glTexCoord2d(texture.getWidth(), 0);
        GL11.glVertex3d(corner2.getX(), corner2.getY(), corner2.getZ());
        GL11.glTexCoord2d(texture.getWidth(), texture.getHeight());
        GL11.glVertex3d(corner3.getX(), corner3.getY(), corner3.getZ());
        GL11.glTexCoord2d(0, texture.getHeight());
        GL11.glVertex3d(corner4.getX(), corner4.getY(), corner4.getZ());
        GL11.glEnd();
    }

    @Override
    public void update(long deltaTime) {

    }

    @Override
    public String toString() {
        return "ActionButton";
    }

    @Override
    public PositionAbsolute getPositionToCompare(PointOfView pointOfView) {
        PositionAbsolute result = null;

        switch (pointOfView) {
            case NORTH:
                result = getPositionAbsolute().plusNew(0.5f, 0, 0.5f);
                break;
            case SOUTH:
                result = getPositionAbsolute().plusNew(0, 0, 0);
                break;
            case EAST:
                result = getPositionAbsolute().plusNew(0, 0, 0.5f);
                break;
            case WEST:
                result = getPositionAbsolute().plusNew(0.5f, 0, 0);
                break;
        }
        return result;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public float getR() {
        return r;
    }

    public float getG() {
        return g;
    }

    public float getB() {
        return b;
    }
}
