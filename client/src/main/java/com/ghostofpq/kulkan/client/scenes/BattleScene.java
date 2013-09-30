package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.*;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.client.utils.HighlightColor;
import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.commons.PositionAbsolute;
import com.ghostofpq.kulkan.entities.battlefield.Battlefield;
import com.ghostofpq.kulkan.entities.battlefield.BattlefieldElement;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.rabbitmq.client.Channel;
import org.newdawn.slick.Color;

import java.io.IOException;
import java.util.*;

public class BattleScene implements Scene {

    private static volatile BattleScene instance = null;
    private final String GAME_SERVER_QUEUE_NAME_BASE = "/server/game";
    private String gameQueueName;
    private Channel channelGameOut;
    private BattleSceneState currentState;
    private String gameId;
    private Battlefield battlefield;
    private PositionAbsolute southPointOfView;
    private PositionAbsolute northPointOfView;
    private PositionAbsolute eastPointOfView;
    private PositionAbsolute westPointOfView;
    private boolean engineIsBusy;
    private Position cursor;
    private List<Position> possiblePositionsToMove;
    private List<Position> possiblePositionsToAttack;
    private PointOfView currentPointOfView;
    private CharacterRender characterRenderLeft;
    private CharacterRender characterRenderRight;
    private Map<Position, Cube> battlefieldRepresentation;
    private List<DrawableObject> drawableObjectList;
    private List<Position> positionsToSelect;
    private GameCharacter currentGameCharacter;
    private GameCharacterRepresentation currentGameCharacterRepresentation;
    private GameCharacterRepresentation targetGameCharacterRepresentation;
    private MenuSelectAction menuSelectAction;
    private List<Player> playerList;
    private List<GameCharacterRepresentation> characterRepresentationList;

    private BattleScene() {
    }

    public static BattleScene getInstance() {
        if (instance == null) {
            synchronized (BattleScene.class) {
                if (instance == null) {
                    instance = new BattleScene();
                }
            }
        }
        return instance;
    }

    @Override
    public void init() {
        setEngineIsBusy(true);

        currentState = BattleSceneState.DEPLOY;
        currentPointOfView = GraphicsManager.getInstance().getCurrentPointOfView();

        possiblePositionsToMove = new ArrayList<Position>();
        possiblePositionsToAttack = new ArrayList<Position>();
        characterRepresentationList = new ArrayList<GameCharacterRepresentation>();

        List<String> options = new ArrayList<String>();
        options.add("Move");
        options.add("Attack");
        options.add("End Turn");
        menuSelectAction = new MenuSelectAction(300, 0, 200, 100, 2);

        cursor = new Position(4, 0, 4);
        GraphicsManager.getInstance().setupLights();
        GraphicsManager.getInstance().ready3D();
        GraphicsManager.getInstance().requestCenterPosition(cursor);
    }

    @Override
    public void update(long deltaTime) {
        boolean busy = false;

        for (DrawableObject drawableObject : drawableObjectList) {
            if (drawableObject.isMoving()) {
                busy = true;
                sortToDrawList();
                break;
            }
        }
        if (pointOfViewHasChanged()) {
            sortToDrawList();
        }
        if (GraphicsManager.getInstance().update3DMovement()) {
            busy = true;
        }

        setEngineIsBusy(busy);


        for (DrawableObject drawableObject : drawableObjectList) {
            drawableObject.update(deltaTime);
        }

        battlefieldRepresentation.get(cursor).setHighlight(HighlightColor.BLUE);

    }

    @Override
    public void render() {
        render3D();
        render2D();
    }

    @Override
    public void manageInput() {
    }

    @Override
    public void closeConnections() {
    }

    public boolean engineIsBusy() {
        return engineIsBusy;
    }

    public void setEngineIsBusy(boolean engineIsBusy) {
        this.engineIsBusy = engineIsBusy;
    }

    public void setBattlefield(Battlefield battlefield) {
        this.battlefield = battlefield;

        southPointOfView = new PositionAbsolute(battlefield.getLength(), battlefield.getHeight(), battlefield.getDepth());
        northPointOfView = new PositionAbsolute(0, battlefield.getHeight(), 0);
        eastPointOfView = new PositionAbsolute(battlefield.getLength(), battlefield.getHeight(), 0);
        westPointOfView = new PositionAbsolute(0, battlefield.getHeight(), battlefield.getDepth());

        extractBattlefieldRepresentation(battlefield);
    }

    public void postMessage(ClientMessage message) {
        try {
            channelGameOut.basicPublish("", gameQueueName, null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage() {
        Message message = Client.getInstance().receiveMessage();
        if (null != message) {
            switch (message.getType()) {
                default:
                    break;
            }
        }
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
        gameQueueName = new StringBuilder().append(GAME_SERVER_QUEUE_NAME_BASE).append(gameId).toString();
        try {
            channelGameOut = Client.getInstance().getConnection().createChannel();
            channelGameOut.queueDeclare(gameQueueName, false, false, false, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void render3D() {
        GraphicsManager.getInstance().make3D();
        for (int i = 0; i < drawableObjectList.size(); i++) {
            drawableObjectList.get(i).draw();
        }
    }

    private void render2D() {
        GraphicsManager.getInstance().make2D();
        if (null != characterRenderLeft) {
            characterRenderLeft.render(Color.white);
        }
        if (null != targetGameCharacterRepresentation && !targetGameCharacterRepresentation.equals(currentGameCharacterRepresentation)) {
            characterRenderRight.render(Color.white);
        }
        if (currentState.equals(BattleSceneState.ACTION)) {
            menuSelectAction.render(Color.white);
        }
    }

    private void extractBattlefieldRepresentation(Battlefield battlefield) {
        battlefieldRepresentation = new HashMap<Position, Cube>();
        for (Position position : battlefield.getBattlefieldElementMap().keySet()) {
            BattlefieldElement element = battlefield.getBattlefieldElementMap().get(position);
            if (element != null) {
                if (element.getType().equals(BattlefieldElement.BattlefieldElementType.BLOC)) {
                    Cube cube = new Cube(position);
                    battlefieldRepresentation.put(position, cube);
                }
            }
        }
        for (Position position : battlefieldRepresentation.keySet()) {
            Cube cube = battlefieldRepresentation.get(position);
            Position positionAbove = new Position(position.getX(), position.getY() + 1, position.getZ());
            Position positionLeft = new Position(position.getX() - 1, position.getY(), position.getZ());
            Position positionRight = new Position(position.getX() + 1, position.getY(), position.getZ());
            Position positionUp = new Position(position.getX(), position.getY(), position.getZ() - 1);
            Position positionDown = new Position(position.getX(), position.getY(), position.getZ() + 1);

            if (battlefieldRepresentation.keySet().contains(positionAbove)) {
                cube.setSelectable(false);
                if (battlefieldRepresentation.keySet().contains(positionLeft) && battlefieldRepresentation.keySet().contains(positionRight) && battlefieldRepresentation.keySet().contains(positionUp) && battlefieldRepresentation.keySet().contains(positionDown)) {
                    cube.setVisible(false);
                }
            }

            if (battlefieldRepresentation.keySet().contains(positionLeft)) {
                cube.getFacetWest().setVisible(false);
            }
            if (battlefieldRepresentation.keySet().contains(positionRight)) {
                cube.getFacetEast().setVisible(false);
            }
            if (battlefieldRepresentation.keySet().contains(positionUp)) {
                cube.getFacetNorth().setVisible(false);
            }
            if (battlefieldRepresentation.keySet().contains(positionDown)) {
                cube.getFacetSouth().setVisible(false);
            }
            if (battlefieldRepresentation.keySet().contains(positionAbove)) {
                cube.getFacetZenith().setVisible(false);
            }
        }

        drawableObjectList = new ArrayList<DrawableObject>();
        List<Position> positionsToDraw = new ArrayList<Position>();
        for (Position position : battlefieldRepresentation.keySet()) {
            if (battlefieldRepresentation.get(position).isVisible()) {
                positionsToDraw.add(position);
                drawableObjectList.add(battlefieldRepresentation.get(position));
            }
        }
        Collections.sort(positionsToDraw);
        positionsToSelect = new ArrayList<Position>();
        for (Position position : positionsToDraw) {
            if (battlefieldRepresentation.get(position).isSelectable()) {
                positionsToSelect.add(position);
            }
        }
        Collections.sort(positionsToSelect);
    }

    private int comparePositionForNorthPointOfView(PositionAbsolute thisPosition, PositionAbsolute otherPosition) {
        int res;
        if (thisPosition.distanceWith(northPointOfView) > otherPosition.distanceWith(northPointOfView)) {
            res = -1;
        } else if (thisPosition.distanceWith(northPointOfView) < otherPosition.distanceWith(northPointOfView)) {
            res = 1;
        } else {
            res = 0;
        }
        return res;
    }

    private int comparePositionForSouthPointOfView(PositionAbsolute thisPosition, PositionAbsolute otherPosition) {
        int res;
        if (thisPosition.distanceWith(southPointOfView) > otherPosition.distanceWith(southPointOfView)) {
            res = -1;
        } else if (thisPosition.distanceWith(southPointOfView) < otherPosition.distanceWith(southPointOfView)) {
            res = 1;
        } else {
            res = 0;
        }
        return res;
    }

    private int comparePositionForEastPointOfView(PositionAbsolute thisPosition, PositionAbsolute otherPosition) {
        int res;
        if (thisPosition.distanceWith(eastPointOfView) > otherPosition.distanceWith(eastPointOfView)) {
            res = -1;
        } else if (thisPosition.distanceWith(eastPointOfView) < otherPosition.distanceWith(eastPointOfView)) {
            res = 1;
        } else {
            res = 0;
        }
        return res;
    }

    private int comparePositionForWestPointOfView(PositionAbsolute thisPosition, PositionAbsolute otherPosition) {
        int res;
        if (thisPosition.distanceWith(westPointOfView) > otherPosition.distanceWith(westPointOfView)) {
            res = -1;
        } else if (thisPosition.distanceWith(westPointOfView) < otherPosition.distanceWith(westPointOfView)) {
            res = 1;
        } else {
            res = 0;
        }
        return res;
    }

    private int comparePosition(PositionAbsolute thisPosition, PositionAbsolute otherPosition) {
        int res = 0;
        switch (GraphicsManager.getInstance().getCurrentPointOfView()) {
            case EAST:
                res = comparePositionForEastPointOfView(thisPosition, otherPosition);
                break;
            case NORTH:
                res = comparePositionForNorthPointOfView(thisPosition, otherPosition);
                break;
            case SOUTH:
                res = comparePositionForSouthPointOfView(thisPosition, otherPosition);
                break;
            case WEST:
                res = comparePositionForWestPointOfView(thisPosition, otherPosition);
                break;
        }

        return res;
    }

    private void sortToDrawList() {
        DrawableObject temp;
        if (drawableObjectList.size() > 1) {
            for (int x = 0; x < drawableObjectList.size(); x++) {
                for (int i = 0; i < drawableObjectList.size() - x - 1; i++) {
                    int compare = comparePosition(drawableObjectList.get(i).getPositionToCompare(currentPointOfView), drawableObjectList.get(i + 1).getPositionToCompare(currentPointOfView));
                    if (compare > 0) {
                        temp = drawableObjectList.get(i);
                        drawableObjectList.set(i, drawableObjectList.get(i + 1));
                        drawableObjectList.set(i + 1, temp);
                    }
                }
            }
        }
    }

    private boolean pointOfViewHasChanged() {
        boolean result = false;
        if (!currentPointOfView.equals(GraphicsManager.getInstance().getCurrentPointOfView())) {
            currentPointOfView = GraphicsManager.getInstance().getCurrentPointOfView();
            result = true;
        }
        return result;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    private enum BattleSceneState {
        DEPLOY, PENDING, ACTION, MOVE, ATTACK, END_TURN
    }
}
