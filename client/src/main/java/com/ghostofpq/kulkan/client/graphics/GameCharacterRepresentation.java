package com.ghostofpq.kulkan.client.graphics;

import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.client.utils.SpritesheetManager;
import com.ghostofpq.kulkan.client.utils.TextureKey;
import com.ghostofpq.kulkan.client.utils.TextureManager;
import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.commons.PositionAbsolute;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.opengl.Texture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class GameCharacterRepresentation extends DrawableObject {

    private GameCharacter character;
    private Map<PointOfView, Animation> animationWalk;
    private Texture chara;
    private PositionAbsolute positionToGo;
    private List<PositionAbsolute> positionsToGo;
    private boolean isJumping;
    private int playerNumber;
    // private BarRender3D healthBarRender;

    public GameCharacterRepresentation(GameCharacter character, Position position, int playerNumber) {
        this.character = character;
        setMoving(false);
        this.playerNumber = playerNumber;
        setHourglass(100);
        setJumping(false);
        setHasActed(false);
        setHasMoved(false);

        setHeight(1.5f);

        //healthBarRender = new BarRender3D(character.getCurrentHealthPoint(), character.getMaxHealthPoint(),
        //        position.toAbsolute().getX(), position.toAbsolute().getY() + 1.8f, position.toAbsolute().getZ(),
        //        0.8f, 0.2f,
        //        Color.green, Color.red);

        setPosition(position);
        setPositionAbsolute(position.toAbsolute());
        positionToGo = position.toAbsolute();
        positionsToGo = new ArrayList<PositionAbsolute>();

        SpriteSheet spriteSheet = SpritesheetManager.getInstance().getSpriteSheet("Arthur");
        animationWalk = new HashMap<PointOfView, Animation>();


        Animation animation = new Animation();
        animation.setAutoUpdate(true);
        animation.addFrame(spriteSheet.getSubImage(10, 300, 32, 36), 100);
        animation.addFrame(spriteSheet.getSubImage(42, 300, 32, 36), 100);
        animation.addFrame(spriteSheet.getSubImage(74, 300, 32, 36), 100);
        animation.addFrame(spriteSheet.getSubImage(106, 300, 32, 36), 100);
        animation.addFrame(spriteSheet.getSubImage(74, 300, 32, 36), 100);
        animation.addFrame(spriteSheet.getSubImage(42, 300, 32, 36), 100);
        animationWalk.put(PointOfView.SOUTH, animation);

        animation = new Animation();
        animation.setAutoUpdate(true);
        animation.addFrame(spriteSheet.getSubImage(138, 300, 32, 36), 100);
        animation.addFrame(spriteSheet.getSubImage(170, 300, 32, 36), 100);
        animation.addFrame(spriteSheet.getSubImage(202, 300, 32, 36), 100);
        animation.addFrame(spriteSheet.getSubImage(234, 300, 32, 36), 100);
        animation.addFrame(spriteSheet.getSubImage(202, 300, 32, 36), 100);
        animation.addFrame(spriteSheet.getSubImage(170, 300, 32, 36), 100);
        animationWalk.put(PointOfView.EAST, animation);

        animation = new Animation();
        animation.setAutoUpdate(true);
        animation.addFrame(spriteSheet.getSubImage(292, 251, 32, 36), 100);
        animation.addFrame(spriteSheet.getSubImage(324, 251, 32, 36), 100);
        animation.addFrame(spriteSheet.getSubImage(356, 251, 32, 36), 100);
        animation.addFrame(spriteSheet.getSubImage(388, 251, 32, 36), 100);
        animation.addFrame(spriteSheet.getSubImage(356, 251, 32, 36), 100);
        animation.addFrame(spriteSheet.getSubImage(324, 251, 32, 36), 100);
        animationWalk.put(PointOfView.WEST, animation);

        animation = new Animation();
        animation.setAutoUpdate(true);
        animation.addFrame(spriteSheet.getSubImage(274, 300, 32, 36), 100);
        animation.addFrame(spriteSheet.getSubImage(306, 300, 32, 36), 100);
        animation.addFrame(spriteSheet.getSubImage(338, 300, 32, 36), 100);
        animation.addFrame(spriteSheet.getSubImage(370, 300, 32, 36), 100);
        animation.addFrame(spriteSheet.getSubImage(338, 300, 32, 36), 100);
        animation.addFrame(spriteSheet.getSubImage(306, 300, 32, 36), 100);
        animationWalk.put(PointOfView.NORTH, animation);

        chara = TextureManager.getInstance().getTexture(TextureKey.CHAR);

    }

    public void update(long deltaTime) {
        if (getCharacter().isAlive()) {
            for (Animation animation : animationWalk.values()) {
                animation.update(deltaTime);
            }

            boolean xDifferent = getPositionAbsolute().getX() != getPositionToGo().getX();
            boolean yDifferent = getPositionAbsolute().getY() != getPositionToGo().getY();
            boolean zDifferent = getPositionAbsolute().getZ() != getPositionToGo().getZ();

            if (xDifferent || yDifferent || zDifferent) {
                this.setMoving(true);
                //log.debug("to go : {}/{}/{} ({}/{}/{}) => {}/{}/{} ",
                //        getPositionAbsolute().getX(), getPositionAbsolute().getY(), getPositionAbsolute().getZ(),
                //        getPosition().getX(), getPosition().getY(), getPosition().getZ(),
                //        getPositionToGo().getX(), getPositionToGo().getY(), getPositionToGo().getZ());
                setHeadingAngle(getHeadingAngleFor(getPositionToGo()));

                if (!isJumping()) {
                    if (yDifferent) {
                        if (positionsToGo.size() > 1) {
                            setHeadingAngle(getHeadingAngleFor(positionsToGo.get(1)));
                        }
                        setJumping(true);
                    }
                    updatePosition(xDifferent, yDifferent, zDifferent);
                } else {
                    if (yDifferent) {
                        updatePosition(xDifferent, yDifferent, zDifferent);
                    } else {
                        // check if landing animation is over
                        setJumping(false);
                    }
                }
            } else if (!positionsToGo.isEmpty()) {
                positionsToGo.remove(0);
                if (!positionsToGo.isEmpty()) {
                    setPositionToGo(positionsToGo.get(0));
                } else {
                    this.setMoving(false);
                    setPositionToGo(null);
                }
            }
        }
        //healthBarRender.setValue(character.getCurrentHealthPoint());
        //healthBarRender.setMaxValue(character.getMaxHealthPoint());
    }

    private void updatePosition(boolean xDifferent, boolean yDifferent, boolean zDifferent) {
        float x = getPositionAbsolute().getX();
        float y = getPositionAbsolute().getY();
        float z = getPositionAbsolute().getZ();

        float step = 0.1f;

        if (xDifferent) {
            if (getPositionAbsolute().getX() > getPositionToGo().getX()) {
                if (getPositionAbsolute().getX() - getPositionToGo().getX() < step) {
                    x = getPositionToGo().getX();
                } else {
                    x = getPositionAbsolute().getX() - step;
                }
            } else if (getPositionAbsolute().getX() < getPositionToGo().getX()) {
                if (getPositionToGo().getX() - getPositionAbsolute().getX() < step) {
                    x = getPositionToGo().getX();
                } else {
                    x = getPositionAbsolute().getX() + step;
                }
            }
        }

        if (yDifferent) {
            if (getPositionAbsolute().getY() > getPositionToGo().getY()) {
                if (getPositionAbsolute().getY() - getPositionToGo().getY() < step) {
                    y = getPositionToGo().getY();
                } else {
                    y = getPositionAbsolute().getY() - step;
                }
            } else if (getPositionAbsolute().getY() < getPositionToGo().getY()) {
                if (getPositionToGo().getY() - getPositionAbsolute().getY() < step) {
                    y = getPositionToGo().getY();
                } else {
                    y = getPositionAbsolute().getY() + step;
                }
            }
        }

        if (zDifferent) {
            if (getPositionAbsolute().getZ() > getPositionToGo().getZ()) {
                if (getPositionAbsolute().getZ() - getPositionToGo().getZ() < step) {
                    z = getPositionToGo().getZ();
                } else {
                    z = getPositionAbsolute().getZ() - step;
                }
            } else if (getPositionAbsolute().getZ() < getPositionToGo().getZ()) {
                if (getPositionToGo().getZ() - getPositionAbsolute().getZ() < step) {
                    z = getPositionToGo().getZ();
                } else {
                    z = getPositionAbsolute().getZ() + step;
                }
            }
        }
        PositionAbsolute updatedPosition = new PositionAbsolute(x, y, z);
        setPositionAbsolute(updatedPosition);
        //healthBarRender.updatePosition(updatedPosition.plusYNew(1.8f));
    }

    public void draw() {
        PointOfView pointOfView = GraphicsManager.getInstance().getCurrentPointOfView();
        GL11.glColor4f(1f, 1f, 1f, 1f);
        PositionAbsolute corner1;
        PositionAbsolute corner2;
        PositionAbsolute corner3;
        PositionAbsolute corner4;
        if (pointOfView.equals(PointOfView.SOUTH)) {
            corner1 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.1f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.9f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner2 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.9f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.1f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner3 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.9f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.1f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner4 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.1f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.9f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
        } else if (pointOfView.equals(PointOfView.NORTH)) {
            corner1 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.9f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.1f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner2 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.1f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.9f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner3 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.1f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.9f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner4 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.9f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.1f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
        } else if (pointOfView.equals(PointOfView.WEST)) {
            corner1 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.1f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.1f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner2 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.9f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.9f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner3 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.9f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.9f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner4 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.1f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.1f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
        } else {  //if(pointOfView.equals(PointOfView.EAST))
            corner1 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.9f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.9f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner2 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.1f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() + 1.5f - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.1f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner3 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.1f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.1f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
            corner4 = new PositionAbsolute(
                    (this.getPositionAbsolute().getX() + 0.9f - GraphicsManager.getInstance().getOriginX()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getY() - GraphicsManager.getInstance().getOriginY()) * GraphicsManager.getInstance().getScale(),
                    (this.getPositionAbsolute().getZ() + 0.9f - GraphicsManager.getInstance().getOriginZ()) * GraphicsManager.getInstance().getScale());
        }

        Animation animation = animationWalk.get(getEquivalentPointOfView(pointOfView, getHeadingAngle()));
        animation.getCurrentFrame().bind();

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(animation.getCurrentFrame().getTextureOffsetX(), animation.getCurrentFrame().getTextureOffsetY());
        GL11.glVertex3d(corner1.getX(), corner1.getY(), corner1.getZ());
        GL11.glTexCoord2d(animation.getCurrentFrame().getTextureOffsetX() + animation.getCurrentFrame().getTextureWidth(), animation.getCurrentFrame().getTextureOffsetY());
        GL11.glVertex3d(corner2.getX(), corner2.getY(), corner2.getZ());
        GL11.glTexCoord2d(animation.getCurrentFrame().getTextureOffsetX() + animation.getCurrentFrame().getTextureWidth(), animation.getCurrentFrame().getTextureOffsetY() + animation.getCurrentFrame().getTextureHeight());
        GL11.glVertex3d(corner3.getX(), corner3.getY(), corner3.getZ());
        GL11.glTexCoord2d(animation.getCurrentFrame().getTextureOffsetX(), animation.getCurrentFrame().getTextureOffsetY() + animation.getCurrentFrame().getTextureHeight());
        GL11.glVertex3d(corner4.getX(), corner4.getY(), corner4.getZ());
        GL11.glEnd();

        //healthBarRender.render();
    }

    public String toString() {
        return character.getName();
    }

    public PositionAbsolute getPositionAbsolute() {
        return positionAbsolute;
    }

    public void setPositionAbsolute(PositionAbsolute positionAbsolute) {
        this.positionAbsolute = positionAbsolute;

        int xFromPositionAbsolute = (int) Math.floor(this.getPositionAbsolute().getX());
        int yFromPositionAbsolute = (int) Math.floor(this.getPositionAbsolute().getY());
        int zFromPositionAbsolute = (int) Math.floor(this.getPositionAbsolute().getZ());

        //log.debug("{}/{}/{} // {}/{}/{}",
        //        xFromPositionAbsolute, yFromPositionAbsolute, zFromPositionAbsolute,
        //        getPosition().getX(), getPosition().getY(), getPosition().getZ());

        if (xFromPositionAbsolute != this.getPosition().getX()) {
            this.getPosition().setX(xFromPositionAbsolute);
        }
        if (yFromPositionAbsolute != this.getPosition().getY()) {
            this.getPosition().setY(yFromPositionAbsolute);
        }
        if (zFromPositionAbsolute != this.getPosition().getZ()) {
            this.getPosition().setZ(zFromPositionAbsolute);
        }
        //healthBarRender.updatePosition(positionAbsolute.plusYNew(1.8f));
    }

    private PointOfView getEquivalentPointOfView(PointOfView boardPointOfView, PointOfView charPointOfView) {
        PointOfView result = PointOfView.SOUTH;

        switch (boardPointOfView) {
            case NORTH:
                switch (charPointOfView) {
                    case NORTH:
                        result = PointOfView.SOUTH;
                        break;
                    case SOUTH:
                        result = PointOfView.NORTH;
                        break;
                    case EAST:
                        result = PointOfView.EAST;
                        break;
                    case WEST:
                        result = PointOfView.WEST;
                        break;
                }
                break;
            case SOUTH:
                switch (charPointOfView) {
                    case NORTH:
                        result = PointOfView.NORTH;
                        break;
                    case SOUTH:
                        result = PointOfView.SOUTH;
                        break;
                    case EAST:
                        result = PointOfView.WEST;
                        break;
                    case WEST:
                        result = PointOfView.EAST;
                        break;
                }
                break;
            case EAST:
                switch (charPointOfView) {
                    case NORTH:
                        result = PointOfView.WEST;
                        break;
                    case SOUTH:
                        result = PointOfView.EAST;
                        break;
                    case EAST:
                        result = PointOfView.SOUTH;
                        break;
                    case WEST:
                        result = PointOfView.NORTH;
                        break;
                }
                break;
            case WEST:
                switch (charPointOfView) {
                    case NORTH:
                        result = PointOfView.EAST;
                        break;
                    case SOUTH:
                        result = PointOfView.WEST;
                        break;
                    case EAST:
                        result = PointOfView.NORTH;
                        break;
                    case WEST:
                        result = PointOfView.SOUTH;
                        break;
                }
                break;
        }
        return result;
    }

    public PositionAbsolute getPositionToGo() {
        PositionAbsolute result;
        if (!positionsToGo.isEmpty()) {
            positionToGo = positionsToGo.get(0);
            result = positionToGo;
        } else {
            result = getPositionAbsolute();
        }
        return result;
    }

    public void setPositionToGo(PositionAbsolute positionToGo) {
        this.positionToGo = positionToGo;
    }

    public Position getDestination() {
        Position result = null;
        if (!positionsToGo.isEmpty()) {
            result = positionsToGo.get(positionsToGo.size()).toPosition();
        }
        return result;
    }

    public void setPositionsToGo(List<Position> positionsRaw) {
        // We add 1 in Y because we are computing ground positions.
        List<Position> positions = new ArrayList<Position>();
        positions.add(getPosition());
        for (int i = 0; i < positionsRaw.size(); i++) {
            GameCharacterRepresentation.log.debug("adding pos {} to path", positionsRaw.get(i).toString());
            positions.add(positionsRaw.get(i).plusYNew(1));
        }
        positionsToGo.add(positions.get(0).toAbsolute());
        for (int i = 1; i < positions.size(); i++) {
            if (positions.get(i).getY() == positions.get(i - 1).getY()) {
                positionsToGo.add(positions.get(i).toAbsolute());
            } else {
                int delta = positions.get(i).getY() - positions.get(i - 1).getY();

                PositionAbsolute step1 = positions.get(i - 1).toAbsolute();
                PositionAbsolute step2 = positions.get(i - 1).toAbsolute();
                PositionAbsolute step3 = positions.get(i - 1).toAbsolute();
                PositionAbsolute step4 = positions.get(i - 1).toAbsolute();
                PositionAbsolute step5 = positions.get(i - 1).toAbsolute();
                PositionAbsolute step6 = positions.get(i - 1).toAbsolute();
                PositionAbsolute step7 = positions.get(i - 1).toAbsolute();
                PositionAbsolute step8 = positions.get(i - 1).toAbsolute();
                PositionAbsolute step9 = positions.get(i - 1).toAbsolute();

                if (delta > 0) {
                    positionsToGo.add(positions.get(i - 1).toAbsolute().plusYNew(delta));
                    step1.plusY(delta);
                    step2.plusY(delta);
                    step3.plusY(delta);
                    step4.plusY(delta);
                    step5.plusY(delta);
                    step6.plusY(delta);
                    step7.plusY(delta);
                    step8.plusY(delta);
                    step9.plusY(delta);
                }

                step1.plusY(0.13f);
                step2.plusY(0.25f);
                step3.plusY(0.35f);
                step4.plusY(0.44f);
                step5.plusY(0.48f);
                step6.plusY(0.44f);
                step7.plusY(0.35f);
                step8.plusY(0.25f);
                step9.plusY(0.13f);

                if (positions.get(i).getX() != positions.get(i - 1).getX()) {
                    if (positions.get(i).getX() < positions.get(i - 1).getX()) {
                        step1.plusX(-0.1f);
                        step2.plusX(-0.2f);
                        step3.plusX(-0.3f);
                        step4.plusX(-0.4f);
                        step5.plusX(-0.5f);
                        step6.plusX(-0.6f);
                        step7.plusX(-0.7f);
                        step8.plusX(-0.8f);
                        step9.plusX(-0.9f);
                    } else if (positions.get(i).getX() > positions.get(i - 1).getX()) {
                        step1.plusX(0.1f);
                        step2.plusX(0.2f);
                        step3.plusX(0.3f);
                        step4.plusX(0.4f);
                        step5.plusX(0.5f);
                        step6.plusX(0.6f);
                        step7.plusX(0.7f);
                        step8.plusX(0.8f);
                        step9.plusX(0.9f);
                    }
                }

                if (positions.get(i).getZ() != positions.get(i - 1).getZ()) {
                    if (positions.get(i).getZ() < positions.get(i - 1).getZ()) {
                        step1.plusZ(-0.1f);
                        step2.plusZ(-0.2f);
                        step3.plusZ(-0.3f);
                        step4.plusZ(-0.4f);
                        step5.plusZ(-0.5f);
                        step6.plusZ(-0.6f);
                        step7.plusZ(-0.7f);
                        step8.plusZ(-0.8f);
                        step9.plusZ(-0.9f);
                    } else if (positions.get(i).getZ() > positions.get(i - 1).getZ()) {
                        step1.plusZ(0.1f);
                        step2.plusZ(0.2f);
                        step3.plusZ(0.3f);
                        step4.plusZ(0.4f);
                        step5.plusZ(0.5f);
                        step6.plusZ(0.6f);
                        step7.plusZ(0.7f);
                        step8.plusZ(0.8f);
                        step9.plusZ(0.9f);
                    }
                }

                positionsToGo.add(step1);
                positionsToGo.add(step2);
                positionsToGo.add(step3);

                if (delta < 0) {
                    positionsToGo.add(positions.get(i).toAbsolute().plusYNew(Math.abs(delta)));
                }

                positionsToGo.add(positions.get(i).toAbsolute());
            }
        }
        for (PositionAbsolute positionAbs : positionsToGo) {
            GameCharacterRepresentation.log.debug("{}", positionAbs.toString());
        }
    }

    public PointOfView getHeadingAngle() {
        return character.getHeadingAngle();
    }

    public void setHeadingAngle(PointOfView headingAngle) {
        character.setHeadingAngle(headingAngle);
    }

    public Position getFootPosition() {
        Position result = new Position(getPosition());
        result.plusY(-1);
        return result;
    }

    public PointOfView getHeadingAngleFor(PositionAbsolute positionToGo) {
        PointOfView result = getHeadingAngle();

        if (Math.abs(positionToGo.getX() - getPositionAbsolute().getX()) >= Math.abs(positionToGo.getZ() - getPositionAbsolute().getZ())) {
            if (positionToGo.getX() > getPositionAbsolute().getX()) {
                result = PointOfView.EAST;
            } else if (positionToGo.getX() == getPositionAbsolute().getX()) {
                if (positionToGo.getZ() > getPositionAbsolute().getZ()) {
                    result = PointOfView.SOUTH;
                } else if (positionToGo.getZ() < getPositionAbsolute().getZ()) {
                    result = PointOfView.NORTH;
                }
            } else {
                result = PointOfView.WEST;
            }
        } else {
            if (positionToGo.getZ() > getPositionAbsolute().getZ()) {
                result = PointOfView.SOUTH;
            } else if (positionToGo.getZ() == getPositionAbsolute().getZ()) {
                if (positionToGo.getX() > getPositionAbsolute().getX()) {
                    result = PointOfView.EAST;
                } else if (positionToGo.getX() < getPositionAbsolute().getX()) {
                    result = PointOfView.WEST;
                }
            } else {
                result = PointOfView.NORTH;
            }
        }
        return result;
    }

    public PositionAbsolute getPositionToCompare(PointOfView pointOfView) {
        return getPositionAbsolute().plusNew(0.5f, height / 2, 0.5f);
    }

    public GameCharacter getCharacter() {
        return character;
    }

    public void updateCharacter(GameCharacter character) {
        this.character.setHourglass(character.getHourglass());
        this.character.setHeadingAngle(character.getHeadingAngle());
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setJumping(boolean jumping) {
        isJumping = jumping;
    }

    public int getHourglass() {
        return getCharacter().getHourglass();
    }

    public void setHourglass(int hourglass) {
        getCharacter().setHourglass(hourglass);
    }

    public boolean hasMoved() {
        return character.hasMoved();
    }

    public void setHasMoved(boolean hadMoved) {
        character.setHasMoved(hadMoved);
    }

    public boolean hasActed() {
        return character.hasActed();
    }

    public void setHasActed(boolean hasActed) {
        character.setHasActed(hasActed);
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public Position getPosition() {
        return character.getPosition();
    }

    public void setPosition(Position position) {
        this.character.setPosition(position);
    }
}
