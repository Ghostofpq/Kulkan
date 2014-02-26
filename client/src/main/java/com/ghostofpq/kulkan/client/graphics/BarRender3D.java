package com.ghostofpq.kulkan.client.graphics;

import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.PositionAbsolute;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

public class BarRender3D {
    private Color colorBack;
    private Color color;
    private float posX;
    private float posY;
    private float posZ;
    private float value;
    private float maxValue;
    private float width;
    private float height;

    private PositionAbsolute corner1;
    private PositionAbsolute corner2;
    private PositionAbsolute corner3;
    private PositionAbsolute corner4;
    private PositionAbsolute separationUp;
    private PositionAbsolute separationDown;

    private PointOfView currentPointOfView;

    public BarRender3D(float value, float maxValue, float posX, float posY, float posZ, float width, float height, Color color, Color colorBack) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.color = color;
        this.colorBack = colorBack;
        this.value = value;
        this.maxValue = maxValue;
        this.width = width;
        this.height = height;


        updateDrawingVariable();
    }

    private void updateDrawingVariable() {
        float highValue = (0.5f + width / 2);
        float lowValue = (0.5f - width / 2);
        PointOfView pointOfView = GraphicsManager.getInstance().getCurrentPointOfView();
        float ratio = 1 - (value / maxValue);
        if (pointOfView.equals(PointOfView.SOUTH)) {
            corner1 = new PositionAbsolute(
                    (posX + highValue - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + lowValue - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner2 = new PositionAbsolute(
                    (posX + highValue - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY + height - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + lowValue - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            separationUp = new PositionAbsolute(
                    (posX + (lowValue + ratio / 2) - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY + height - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + (highValue - ratio / 2) - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            separationDown = new PositionAbsolute(
                    (posX + (lowValue + ratio / 2) - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + (highValue - ratio / 2) - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner3 = new PositionAbsolute(
                    (posX + lowValue - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY + height - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + highValue - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner4 = new PositionAbsolute(
                    (posX + lowValue - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + highValue - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
        } else if (pointOfView.equals(PointOfView.NORTH)) {
            corner1 = new PositionAbsolute(
                    (posX + lowValue - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + highValue - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner2 = new PositionAbsolute(
                    (posX + lowValue - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY + height - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + highValue - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            separationUp = new PositionAbsolute(
                    (posX + (highValue - ratio / 2) - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY + height - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + (lowValue + ratio / 2) - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            separationDown = new PositionAbsolute(
                    (posX + (highValue - ratio / 2) - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + (lowValue + ratio / 2) - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner3 = new PositionAbsolute(
                    (posX + highValue - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY + height - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + lowValue - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner4 = new PositionAbsolute(
                    (posX + highValue - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + lowValue - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
        } else if (pointOfView.equals(PointOfView.WEST)) {
            corner1 = new PositionAbsolute(
                    (posX + lowValue - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + lowValue - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner2 = new PositionAbsolute(
                    (posX + lowValue - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY + height - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + lowValue - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            separationUp = new PositionAbsolute(
                    (posX + (highValue - ratio / 2) - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY + height - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + (highValue - ratio / 2) - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            separationDown = new PositionAbsolute(
                    (posX + (highValue - ratio / 2) - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + (highValue - ratio / 2) - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner3 = new PositionAbsolute(
                    (posX + highValue - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY + height - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + highValue - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner4 = new PositionAbsolute(
                    (posX + highValue - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + highValue - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
        } else {  //if(pointOfView.equals(PointOfView.EAST))
            corner1 = new PositionAbsolute(
                    (posX + highValue - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + highValue - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner2 = new PositionAbsolute(
                    (posX + highValue - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY + height - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + highValue - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            separationUp = new PositionAbsolute(
                    (posX + (lowValue + ratio / 2) - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY + height - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + (lowValue + ratio / 2) - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            separationDown = new PositionAbsolute(
                    (posX + (lowValue + ratio / 2) - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + (lowValue + ratio / 2) - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner3 = new PositionAbsolute(
                    (posX + lowValue - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY + height - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + lowValue - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner4 = new PositionAbsolute(
                    (posX + lowValue - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (posY - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (posZ + lowValue - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
        }

    }


    public void render() {
        updateDrawingVariable();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), 1f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(corner1.getX(), corner1.getY(), corner1.getZ());
        GL11.glVertex3d(corner2.getX(), corner2.getY(), corner2.getZ());
        GL11.glVertex3d(separationUp.getX(), separationUp.getY(), separationUp.getZ());
        GL11.glVertex3d(separationDown.getX(), separationDown.getY(), separationDown.getZ());
        GL11.glEnd();

        GL11.glColor4f(colorBack.getRed(), colorBack.getGreen(), colorBack.getBlue(), 1f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(separationUp.getX(), separationUp.getY(), separationUp.getZ());
        GL11.glVertex3d(corner3.getX(), corner3.getY(), corner3.getZ());
        GL11.glVertex3d(corner4.getX(), corner4.getY(), corner4.getZ());
        GL11.glVertex3d(separationDown.getX(), separationDown.getY(), separationDown.getZ());
        GL11.glEnd();

        GL11.glColor4f(0, 0, 0, 1);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(separationUp.getX(), separationUp.getY(), separationUp.getZ());
        GL11.glVertex3d(separationDown.getX(), separationDown.getY(), separationDown.getZ());
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public void updatePosition(PositionAbsolute positionAbsolute) {
        this.posX = positionAbsolute.getX();
        this.posY = positionAbsolute.getY();
        this.posZ = positionAbsolute.getZ();
    }
}
