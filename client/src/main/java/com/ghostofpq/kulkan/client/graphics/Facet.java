package com.ghostofpq.kulkan.client.graphics;

import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.client.utils.TextureKey;
import com.ghostofpq.kulkan.client.utils.TextureManager;
import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.commons.PositionAbsolute;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

import java.io.Serializable;

public class Facet extends DrawableObject implements Serializable {

    private static final long serialVersionUID = 5436385396379038962L;
    private PositionAbsolute corner1;
    private PositionAbsolute corner2;
    private PositionAbsolute corner3;
    private PositionAbsolute corner4;
    private boolean visible;
    private TextureKey textureKey;

    public Facet(Position position, PositionAbsolute corner1, PositionAbsolute corner2, PositionAbsolute corner3, PositionAbsolute corner4, TextureKey textureKey) {
        this.setPosition(position);
        this.setPositionAbsolute(position.toAbsolute());
        this.setCorner1(corner1);
        this.setCorner2(corner2);
        this.setCorner3(corner3);
        this.setCorner4(corner4);
        this.setTextureKey(textureKey);
        this.setVisible(true);
        this.setMoving(false);
    }

    public void update(long deltaTime) {

    }

    public void draw() {
        if (isVisible()) {
            GL11.glColor4f(1f, 1f, 1f, 1f);
            Texture texture = TextureManager.getInstance().getTexture(textureKey);
            texture.bind();
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d((corner1.getX() - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale()
                    , (corner1.getY() - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale()
                    , (corner1.getZ() - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            GL11.glTexCoord2d(texture.getWidth(), 0);
            GL11.glVertex3d((corner2.getX() - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale()
                    , (corner2.getY() - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale()
                    , (corner2.getZ() - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            GL11.glTexCoord2d(texture.getWidth(), texture.getHeight());
            GL11.glVertex3d((corner3.getX() - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale()
                    , (corner3.getY() - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale()
                    , (corner3.getZ() - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            GL11.glTexCoord2d(0, texture.getHeight());
            GL11.glVertex3d((corner4.getX() - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale()
                    , (corner4.getY() - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale()
                    , (corner4.getZ() - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            GL11.glEnd();
        }
    }

    public String toString() {
        return "Facet";
    }

    /**
     * Getters and Setters
     */

    public PositionAbsolute getCorner1() {
        return corner1;
    }

    public void setCorner1(PositionAbsolute corner1) {
        this.corner1 = corner1;
    }

    public PositionAbsolute getCorner2() {
        return corner2;
    }

    public void setCorner2(PositionAbsolute corner2) {
        this.corner2 = corner2;
    }

    public PositionAbsolute getCorner3() {
        return corner3;
    }

    public void setCorner3(PositionAbsolute corner3) {
        this.corner3 = corner3;
    }

    public PositionAbsolute getCorner4() {
        return corner4;
    }

    public void setCorner4(PositionAbsolute corner4) {
        this.corner4 = corner4;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public TextureKey getTextureKey() {
        return textureKey;
    }

    public void setTextureKey(TextureKey textureKey) {
        this.textureKey = textureKey;
    }

    public PositionAbsolute getPositionToCompare(PointOfView pointOfView) {
        return getPositionAbsolute();
    }
}
