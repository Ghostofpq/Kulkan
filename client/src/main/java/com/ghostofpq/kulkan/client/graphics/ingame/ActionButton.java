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
    private TextureKey textureKeyUsed;
    private boolean hovered;
    private boolean used;
    private float r;
    private float g;
    private float b;

    private PositionAbsolute corner1;
    private PositionAbsolute corner2;
    private PositionAbsolute corner3;
    private PositionAbsolute corner4;

    private PositionAbsolute typePosition;

    public ActionButton(Position position, ActionButtonType actionButtonType) {
        this.position = position;
        this.positionAbsolute = this.position.toAbsolute();
        this.actionButtonType = actionButtonType;
        switch (this.actionButtonType) {
            case MOVE:
                textureKey = TextureKey.ACTION_1;
                textureKeyForMouseDetection = TextureKey.ACTION_1_BACK;
                textureKeyHovered = TextureKey.ACTION_1_HOVERED;
                textureKeyUsed = TextureKey.ACTION_1_USED;
                typePosition = new PositionAbsolute(0.0f, 0.0f, 0.0f);
                break;

            case ATTACK:
                textureKey = TextureKey.ACTION_1;
                textureKeyForMouseDetection = TextureKey.ACTION_1_BACK;
                textureKeyHovered = TextureKey.ACTION_1_HOVERED;
                typePosition = new PositionAbsolute(0.25f, 0.2f, 0.25f);
                textureKeyUsed = TextureKey.ACTION_1_USED;
                break;

            case CAPACITY:
                textureKey = TextureKey.ACTION_1;
                textureKeyForMouseDetection = TextureKey.ACTION_1_BACK;
                textureKeyHovered = TextureKey.ACTION_1_HOVERED;
                typePosition = new PositionAbsolute(0.5f, 0.2f, 0.5f);
                textureKeyUsed = TextureKey.ACTION_1_USED;
                break;

            case END_TURN:
                textureKey = TextureKey.ACTION_1;
                textureKeyForMouseDetection = TextureKey.ACTION_1_BACK;
                textureKeyHovered = TextureKey.ACTION_1_HOVERED;
                typePosition = new PositionAbsolute(0.75f, 0.0f, 0.75f);
                textureKeyUsed = TextureKey.ACTION_1_USED;
                break;
        }
        this.hovered = false;
        this.used = false;
        calculateCorners();
    }

    private void calculateCorners() {
        PointOfView pointOfView = GraphicsManager.getInstance().getCurrentPointOfView();

        if (pointOfView.equals(PointOfView.SOUTH)) {
            corner1 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + typePosition.getX() - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + typePosition.getY() + 2f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() - typePosition.getZ() + 1.0f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner2 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + typePosition.getX() + 0.25f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + typePosition.getY() + 2f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() - typePosition.getZ() + 0.75f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner3 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + typePosition.getX() + 0.25f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + typePosition.getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() - typePosition.getZ() + 0.75f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner4 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + typePosition.getX() - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + typePosition.getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() - typePosition.getZ() + 1.0f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
        } else if (pointOfView.equals(PointOfView.NORTH)) {
            corner1 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() - typePosition.getX() + 0.75f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + typePosition.getY() + 2f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + typePosition.getZ() + 0.25f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner2 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() - typePosition.getX() + 1.0f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + typePosition.getY() + 2f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + typePosition.getZ() - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner3 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() - typePosition.getX() + 1.0f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + typePosition.getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + typePosition.getZ() - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner4 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() - typePosition.getX() + 0.75f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + typePosition.getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + typePosition.getZ() + 0.25f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
        } else if (pointOfView.equals(PointOfView.WEST)) {
            corner1 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + typePosition.getX() - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + typePosition.getY() + 2f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + typePosition.getZ() - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner2 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + typePosition.getX() + 0.25f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + typePosition.getY() + 2f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + typePosition.getZ() + 0.25f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner3 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + typePosition.getX() + 0.25f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + typePosition.getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + typePosition.getZ() + 0.25f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner4 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + typePosition.getX() - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + typePosition.getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + typePosition.getZ() - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
        } else if (pointOfView.equals(PointOfView.EAST)) {
            corner1 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() - typePosition.getX() + 0.75f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + typePosition.getY() + 2f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() - typePosition.getZ() + 0.75f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner2 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() - typePosition.getX() + 1f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + typePosition.getY() + 2f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() - typePosition.getZ() + 1f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner3 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() - typePosition.getX() + 1f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + typePosition.getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() - typePosition.getZ() + 1f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner4 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() - typePosition.getX() + 0.75f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + typePosition.getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() - typePosition.getZ() + 0.75f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
        }
    }

    @Override
    public void draw() {
        GL11.glColor4f(1f, 1f, 1f, 1f);
        Texture texture;

        if (used) {
            texture = TextureManager.getInstance().getTexture(textureKeyUsed);
        } else {
            if (isHovered()) {
                texture = TextureManager.getInstance().getTexture(textureKeyHovered);
            } else {
                texture = TextureManager.getInstance().getTexture(textureKey);
            }
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
        GL11.glEnable(GL11.GL_TEXTURE_2D);
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
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    @Override
    public void update(long deltaTime) {
        calculateCorners();
    }

    @Override
    public String toString() {
        return "ActionButton";
    }

    @Override
    public PositionAbsolute getPositionToCompare(PointOfView pointOfView) {
        return positionAbsolute.plusNew(0.5f, 0.5f, 0.5f);
    }

    public ActionButtonType getActionButtonType() {
        return actionButtonType;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
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
