package com.ghostofpq.kulkan.client.graphics.ingame;

import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.client.utils.HighlightColor;
import com.ghostofpq.kulkan.client.utils.TextureKey;
import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.commons.PositionAbsolute;

import java.io.Serializable;

public class Cube extends DrawableObject implements Serializable {
    private static final long serialVersionUID = 4804104249115278769L;
    private Facet facetZenith;
    private Facet facetNorth;
    private Facet facetEast;
    private Facet facetWest;
    private Facet facetSouth;
    private TextureKey textureTop;
    private TextureKey side;
    private boolean visible;
    private boolean selectable;
    private HighlightColor highlight;

    public Cube(Position position) {
        this.setHeight(1.0f);
        this.setPosition(position);
        this.setPositionAbsolute(position.toAbsolute());
        this.setVisible(true);
        this.setSelectable(true);
        this.setMoving(false);

        int randomTop = (int) (Math.random() * 10);
        if (randomTop == 0) {
            textureTop = TextureKey.EARTH_TOP_00;
        } else if (randomTop == 1) {
            textureTop = TextureKey.EARTH_TOP_01;
        } else if (randomTop == 2) {
            textureTop = TextureKey.EARTH_TOP_02;
        } else if (randomTop == 3) {
            textureTop = TextureKey.EARTH_TOP_03;
        } else if (randomTop == 4) {
            textureTop = TextureKey.EARTH_TOP_04;
        } else if (randomTop == 5) {
            textureTop = TextureKey.EARTH_TOP_05;
        } else if (randomTop == 6) {
            textureTop = TextureKey.EARTH_TOP_06;
        } else if (randomTop == 7) {
            textureTop = TextureKey.EARTH_TOP_07;
        } else if (randomTop == 8) {
            textureTop = TextureKey.EARTH_TOP_08;
        } else {
            textureTop = TextureKey.EARTH_TOP_09;
        }


        highlight = HighlightColor.NONE;

        // Creating the facets
        PositionAbsolute positionAbsolute = position.toAbsolute();
        PositionAbsolute p1 = positionAbsolute;
        PositionAbsolute p2 = positionAbsolute.plus(0, 0, 1f);
        PositionAbsolute p3 = positionAbsolute.plus(1f, 0, 1f);
        PositionAbsolute p4 = positionAbsolute.plus(1f, 0, 0);
        PositionAbsolute p5 = positionAbsolute.plus(0, 1f, 0);
        PositionAbsolute p6 = positionAbsolute.plus(0, 1f, 1f);
        PositionAbsolute p7 = positionAbsolute.plus(1f, 1f, 1f);
        PositionAbsolute p8 = positionAbsolute.plus(1f, 1f, 0);

        facetZenith = new Facet(position, p5, p8, p7, p6, textureTop);
        facetSouth = new Facet(position, p6, p7, p3, p2, randomTextureSide());
        facetWest = new Facet(position, p5, p6, p2, p1, randomTextureSide());
        facetNorth = new Facet(position, p8, p5, p1, p4, randomTextureSide());
        facetEast = new Facet(position, p7, p8, p4, p3, randomTextureSide());
    }

    private TextureKey randomTextureSide() {
        TextureKey result;
        int randomSide = (int) (Math.random() * 4);
        if (randomSide == 0) {
            result = TextureKey.EARTH_SIDE_00;
        } else if (randomSide == 1) {
            result = TextureKey.EARTH_SIDE_01;
        } else if (randomSide == 2) {
            result = TextureKey.EARTH_SIDE_02;
        } else {
            result = TextureKey.EARTH_SIDE_03;
        }
        return result;
    }

    public void update(long deltaTime) {

    }

    public void draw() {
        PointOfView pointOfView = GraphicsManager.getInstance().getCurrentPointOfView();
        if (isVisible()) {
            if (!highlight.equals(HighlightColor.NONE)) {
                TextureKey texture = TextureKey.HIGHLIGHT_BLUE;
                if (highlight.equals(HighlightColor.GREEN)) {
                    texture = TextureKey.HIGHLIGHT_GREEN;
                } else if (highlight.equals(HighlightColor.RED)) {
                    texture = TextureKey.HIGHLIGHT_RED;
                }
                facetZenith.setTextureKey(texture);
            } else {
                facetZenith.setTextureKey(textureTop);
            }

            switch (pointOfView) {
                case SOUTH:
                    facetSouth.draw();
                    facetEast.draw();
                    facetZenith.draw();
                    break;
                case WEST:
                    facetWest.draw();
                    facetSouth.draw();
                    facetZenith.draw();
                    break;
                case NORTH:
                    facetNorth.draw();
                    facetWest.draw();
                    facetZenith.draw();
                    break;
                case EAST:
                    facetEast.draw();
                    facetNorth.draw();
                    facetZenith.draw();
                    break;
            }
        }
    }

    public void renderForMousePosition() {
        PointOfView pointOfView = GraphicsManager.getInstance().getCurrentPointOfView();
        if (isVisible()) {
            float colorX = ((float) position.getX() + 10f) / 255f;
            float colorY = ((float) position.getY() + 10f) / 255f;
            float colorZ = ((float) position.getZ() + 10f) / 255f;

            switch (pointOfView) {
                case SOUTH:
                    facetSouth.renderBlackForMousePosition();
                    facetEast.renderBlackForMousePosition();
                    facetZenith.renderForMousePosition(colorX, colorY, colorZ);
                    break;
                case WEST:
                    facetWest.renderBlackForMousePosition();
                    facetSouth.renderBlackForMousePosition();
                    facetZenith.renderForMousePosition(colorX, colorY, colorZ);
                    break;
                case NORTH:
                    facetNorth.renderBlackForMousePosition();
                    facetWest.renderBlackForMousePosition();
                    facetZenith.renderForMousePosition(colorX, colorY, colorZ);
                    break;
                case EAST:
                    facetEast.renderBlackForMousePosition();
                    facetNorth.renderBlackForMousePosition();
                    facetZenith.renderForMousePosition(colorX, colorY, colorZ);
                    break;
            }
        }
    }

    public String toString() {
        return "Cube";
    }

    /**
     * Getters and Setters
     */

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Facet getFacetZenith() {
        return facetZenith;
    }

    public void setFacetZenith(Facet facetZenith) {
        this.facetZenith = facetZenith;
    }

    public Facet getFacetNorth() {
        return facetNorth;
    }

    public void setFacetNorth(Facet facetNorth) {
        this.facetNorth = facetNorth;
    }

    public Facet getFacetEast() {
        return facetEast;
    }

    public void setFacetEast(Facet facetEast) {
        this.facetEast = facetEast;
    }

    public Facet getFacetWest() {
        return facetWest;
    }

    public void setFacetWest(Facet facetWest) {
        this.facetWest = facetWest;
    }

    public Facet getFacetSouth() {
        return facetSouth;
    }

    public void setFacetSouth(Facet facetSouth) {
        this.facetSouth = facetSouth;
    }

    public TextureKey getTextureTop() {
        return textureTop;
    }

    public void setTextureTop(TextureKey textureTop) {
        this.textureTop = textureTop;
    }

    public TextureKey getSide() {
        return side;
    }

    public void setSide(TextureKey side) {
        this.side = side;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public HighlightColor getHighlight() {
        return highlight;
    }

    public void setHighlight(HighlightColor highlight) {
        this.highlight = highlight;
    }

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
}
