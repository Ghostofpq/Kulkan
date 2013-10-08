package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.*;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.client.utils.HighlightColor;
import com.ghostofpq.kulkan.client.utils.InputManager;
import com.ghostofpq.kulkan.client.utils.InputMap;
import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.commons.PositionAbsolute;
import com.ghostofpq.kulkan.entities.battlefield.BattleSceneState;
import com.ghostofpq.kulkan.entities.battlefield.Battlefield;
import com.ghostofpq.kulkan.entities.battlefield.BattlefieldElement;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.messages.*;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;

import java.io.IOException;
import java.util.*;

@Slf4j
public class BattleScene implements Scene {

    private static volatile BattleScene instance = null;
    private final String GAME_SERVER_QUEUE_NAME_BASE = "/server/game/";
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
    private int playerNumber;
    private List<GameCharacter> characterListToDeploy;
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
        currentState = BattleSceneState.PENDING;
        currentPointOfView = GraphicsManager.getInstance().getCurrentPointOfView();

        possiblePositionsToMove = new ArrayList<Position>();
        possiblePositionsToAttack = new ArrayList<Position>();
        characterRepresentationList = new ArrayList<GameCharacterRepresentation>();

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
        receiveMessage();
    }

    @Override
    public void manageInput() {
        if (!engineIsBusy()) {
            while (Keyboard.next()) {
                if (Keyboard.getEventKeyState()) {
                    if (InputManager.getInstance().getInput(Keyboard.getEventKey()) != null) {
                        if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.UP)) {
                            if (currentState.equals(BattleSceneState.DEPLOY)) {
                                switch (GraphicsManager.getInstance().getCurrentPointOfView()) {
                                    case EAST:
                                        cursorLeft();
                                        break;
                                    case NORTH:
                                        cursorDown();
                                        break;
                                    case SOUTH:
                                        cursorUp();
                                        break;
                                    case WEST:
                                        cursorRight();
                                        break;
                                }
                                GraphicsManager.getInstance().requestCenterPosition(cursor);
                            }
                        } else if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.DOWN)) {
                            if (currentState.equals(BattleSceneState.DEPLOY)) {
                                switch (GraphicsManager.getInstance().getCurrentPointOfView()) {
                                    case EAST:
                                        cursorRight();
                                        break;
                                    case NORTH:
                                        cursorUp();
                                        break;
                                    case SOUTH:
                                        cursorDown();
                                        break;
                                    case WEST:
                                        cursorLeft();
                                        break;
                                }
                                GraphicsManager.getInstance().requestCenterPosition(cursor);
                            }
                        } else if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.LEFT)) {
                            if (currentState.equals(BattleSceneState.DEPLOY)) {
                                switch (GraphicsManager.getInstance().getCurrentPointOfView()) {
                                    case EAST:
                                        cursorDown();
                                        break;
                                    case NORTH:
                                        cursorRight();
                                        break;
                                    case SOUTH:
                                        cursorLeft();
                                        break;
                                    case WEST:
                                        cursorUp();
                                        break;
                                }
                                GraphicsManager.getInstance().requestCenterPosition(cursor);
                            }
                        } else if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.RIGHT)) {
                            if (currentState.equals(BattleSceneState.DEPLOY)) {
                                switch (GraphicsManager.getInstance().getCurrentPointOfView()) {
                                    case EAST:
                                        cursorUp();
                                        break;
                                    case NORTH:
                                        cursorLeft();
                                        break;
                                    case SOUTH:
                                        cursorRight();
                                        break;
                                    case WEST:
                                        cursorDown();
                                        break;
                                }
                                GraphicsManager.getInstance().requestCenterPosition(cursor);
                            }
                        } else if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.ROTATE_LEFT)) {
                            if (currentState.equals(BattleSceneState.DEPLOY)) {
                                switch (GraphicsManager.getInstance().getCurrentPointOfView()) {
                                    case EAST:
                                        GraphicsManager.getInstance().requestPointOfView(PointOfView.NORTH);
                                        break;
                                    case NORTH:
                                        GraphicsManager.getInstance().requestPointOfView(PointOfView.WEST);
                                        break;
                                    case SOUTH:
                                        GraphicsManager.getInstance().requestPointOfView(PointOfView.EAST);
                                        break;
                                    case WEST:
                                        GraphicsManager.getInstance().requestPointOfView(PointOfView.SOUTH);
                                        break;
                                }
                            }
                        } else if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.ROTATE_RIGHT)) {
                            if (currentState.equals(BattleSceneState.DEPLOY)) {
                                switch (GraphicsManager.getInstance().getCurrentPointOfView()) {
                                    case EAST:
                                        GraphicsManager.getInstance().requestPointOfView(PointOfView.SOUTH);
                                        break;
                                    case NORTH:
                                        GraphicsManager.getInstance().requestPointOfView(PointOfView.EAST);
                                        break;
                                    case SOUTH:
                                        GraphicsManager.getInstance().requestPointOfView(PointOfView.WEST);
                                        break;
                                    case WEST:
                                        GraphicsManager.getInstance().requestPointOfView(PointOfView.NORTH);
                                        break;
                                }
                            }
                        } else if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.ZOOM_IN)) {
                            if (currentState.equals(BattleSceneState.DEPLOY)) {

                            }
                        } else if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.ZOOM_OUT)) {
                            if (currentState.equals(BattleSceneState.DEPLOY)) {

                            }
                        } else if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.VALIDATE)) {
                            if (currentState.equals(BattleSceneState.DEPLOY)) {
                                placeCharacter();
                            }
                        } else if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.CANCEL)) {
                            if (currentState.equals(BattleSceneState.DEPLOY)) {
                                Client.getInstance().setCurrentScene(LobbyScene.getInstance());
                            }
                        } else if (InputManager.getInstance().getInput(Keyboard.getEventKey()).equals(InputMap.Input.SWITCH)) {
                            if (currentState.equals(BattleSceneState.DEPLOY)) {

                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void closeConnections() {
    }

    public void postMessage(ClientMessage message) {
        try {
            log.debug(" [-] POST MESSAGE {} ON {}", message.getType(), gameQueueName);
            channelGameOut.basicPublish("", gameQueueName, null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage() {
        if (!engineIsBusy()) {
            Message message = Client.getInstance().receiveMessage();
            if (null != message) {
                switch (message.getType()) {
                    case START_DEPLOYMENT:
                        log.debug(" [-] START_DEPLOYMENT");
                        MessageDeploymentStart messageDeploymentStart = (MessageDeploymentStart) message;
                        characterListToDeploy = messageDeploymentStart.getCharacterList();
                        playerNumber = messageDeploymentStart.getPlayerNumber();
                        currentGameCharacter = characterListToDeploy.get(0);
                        characterRenderLeft = new CharacterRender(0, 0, 300, 100, 2, currentGameCharacter);
                        highlightDeploymentZone();
                        currentState = BattleSceneState.DEPLOY;
                        break;
                    case OTHER_PLAYER_DEPLOYMENT:
                        MessageDeploymentPositionsOfPlayer messageDeploymentPositionsOfPlayer = (MessageDeploymentPositionsOfPlayer) message;
                        log.debug(" [-] DEPLOYMENT OF PLAYER {}", messageDeploymentPositionsOfPlayer.getPlayerNumber());
                        for (GameCharacter gameCharacter : messageDeploymentPositionsOfPlayer.getCharacterPositionMap().keySet()) {
                            GameCharacterRepresentation gameCharacterRepresentation = new GameCharacterRepresentation(gameCharacter,
                                    messageDeploymentPositionsOfPlayer.getCharacterPositionMap().get(gameCharacter),
                                    messageDeploymentPositionsOfPlayer.getPlayerNumber());
                            characterRepresentationList.add(gameCharacterRepresentation);
                            drawableObjectList.add(gameCharacterRepresentation);
                            sortToDrawList();
                        }
                        break;
                    default:
                        log.error(" [X] UNEXPECTED MESSAGE : {}", message.getType());
                        break;
                }
            }
        }
    }

    @Override
    public void render() {
        render3D();
        render2D();
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

    public void placeCharacter() {
        if (battlefield.getDeploymentZones().get(playerNumber).contains(cursor)) {

            log.debug(" [-] PLACE CHARACTER AT {}", cursor.toString());
            Position position = new Position(cursor);
            position.plusY(1);
            GameCharacterRepresentation gameCharacterRepresentation = new GameCharacterRepresentation(currentGameCharacter, position, playerNumber);
            characterRepresentationList.add(gameCharacterRepresentation);
            drawableObjectList.add(gameCharacterRepresentation);
            sortToDrawList();
            characterListToDeploy.remove(currentGameCharacter);
            if (characterListToDeploy.isEmpty()) {

                characterRenderLeft = null;
                sendDeploymentResult();
                cleanHighlightDeploymentZone();
            } else {
                currentGameCharacter = characterListToDeploy.get(0);
                characterRenderLeft = new CharacterRender(0, 0, 300, 100, 2, currentGameCharacter);
            }
            battlefield.getDeploymentZones().get(playerNumber).remove(cursor);
        }
    }

    private void sendDeploymentResult() {
        Map<GameCharacter, Position> gameCharacterPositionMap = new HashMap<GameCharacter, Position>();

        for (GameCharacterRepresentation gameCharacterRepresentation : characterRepresentationList) {
            gameCharacterPositionMap.put(gameCharacterRepresentation.getCharacter(), gameCharacterRepresentation.getPosition());
        }

        MessageDeploymentFinishedForPlayer messageDeploymentFinishedForPlayer = new MessageDeploymentFinishedForPlayer(Client.getInstance().getTokenKey(), gameCharacterPositionMap, playerNumber);

        log.debug(" [-] DEPLOYMENT FINISHED FOR {}", messageDeploymentFinishedForPlayer.getKeyToken());
        postMessage(messageDeploymentFinishedForPlayer);

    }

    private void highlightDeploymentZone() {
        List<Position> deploymentZonePlayer = battlefield.getDeploymentZones().get(playerNumber);
        for (Position deploymentPosition : deploymentZonePlayer) {
            battlefieldRepresentation.get(deploymentPosition).setHighlight(HighlightColor.GREEN);
        }
    }

    private void cleanHighlightDeploymentZone() {
        List<Position> deploymentZonePlayer = battlefield.getDeploymentZones().get(playerNumber);
        for (Position deploymentPosition : deploymentZonePlayer) {
            if (battlefieldRepresentation.get(deploymentPosition).getHighlight().equals(HighlightColor.GREEN)) {
                battlefieldRepresentation.get(deploymentPosition).setHighlight(HighlightColor.NONE);
            }
        }
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
            log.debug("POV={}", GraphicsManager.getInstance().getCurrentPointOfView());
            currentPointOfView = GraphicsManager.getInstance().getCurrentPointOfView();
            result = true;
        }
        return result;
    }

    private void updateCursorTarget() {
        targetGameCharacterRepresentation = null;
        characterRenderRight = null;
        for (GameCharacterRepresentation gameCharacterRepresentation : characterRepresentationList) {
            if (cursor.equals(gameCharacterRepresentation.getFootPosition())) {
                targetGameCharacterRepresentation = gameCharacterRepresentation;
                characterRenderRight = new CharacterRender(500, 0, 300, 100, 2, targetGameCharacterRepresentation.getCharacter());
            }
        }
    }

    private void resetOldHighlight() {
        battlefieldRepresentation.get(cursor).setHighlight(HighlightColor.NONE);
        if (currentState.equals(BattleSceneState.DEPLOY)) {
            if (battlefield.getDeploymentZones().get(playerNumber).contains(cursor)) {
                battlefieldRepresentation.get(cursor).setHighlight(HighlightColor.GREEN);
            }
        }
        if (currentState.equals(BattleSceneState.MOVE)) {
            if (possiblePositionsToMove.contains(cursor)) {
                battlefieldRepresentation.get(cursor).setHighlight(HighlightColor.GREEN);
            }
        }
        if (currentState.equals(BattleSceneState.ATTACK)) {
            if (possiblePositionsToAttack.contains(cursor)) {
                battlefieldRepresentation.get(cursor).setHighlight(HighlightColor.RED);
            }
        }
    }

    private void cursorUp() {
        if (cursor.getZ() != 0) {
            resetOldHighlight();
            cursor.setZ(cursor.getZ() - 1);
            if (!positionsToSelect.contains(cursor)) {
                Position closestPosition = getClosestPosition(cursor);
                cursor = new Position(closestPosition);
            }
            battlefieldRepresentation.get(cursor).setHighlight(HighlightColor.BLUE);
        }
        updateCursorTarget();
    }

    private void cursorDown() {
        if (cursor.getZ() != battlefield.getDepth() - 1) {
            resetOldHighlight();
            cursor.setZ(cursor.getZ() + 1);
            if (!positionsToSelect.contains(cursor)) {
                Position closestPosition = getClosestPosition(cursor);
                cursor = new Position(closestPosition);
            }
        }
        updateCursorTarget();
    }

    private void cursorLeft() {
        if (cursor.getX() != 0) {
            resetOldHighlight();
            cursor.setX(cursor.getX() - 1);
            if (!positionsToSelect.contains(cursor)) {
                Position closestPosition = getClosestPosition(cursor);
                cursor = new Position(closestPosition);
            }
        }
        updateCursorTarget();
    }

    private void cursorRight() {
        if (cursor.getX() != battlefield.getLength() - 1) {
            resetOldHighlight();
            cursor.setX(cursor.getX() + 1);
            if (!positionsToSelect.contains(cursor)) {
                Position closestPosition = getClosestPosition(cursor);
                cursor = new Position(closestPosition);
            }
        }
        updateCursorTarget();
    }

    private void cursorTab() {
        List<Position> possiblePositions = getPossiblePositions(cursor.getX(), cursor.getZ());

        if (possiblePositions.size() != 1) {
            resetOldHighlight();
            int i = possiblePositions.indexOf(cursor);
            if (i == possiblePositions.size() - 1) {
                i = 0;
            } else {
                i++;
            }
            cursor.setY(possiblePositions.get(i).getY());
        }
    }

    /*
    * GETTERS AND SETTERS
    */

    private List<Position> getPossiblePositions(int x, int z) {
        List<Position> possiblePositions = new ArrayList<Position>();
        for (Position position : positionsToSelect) {
            if ((position.getX() == x) && (position.getZ() == z)) {
                possiblePositions.add(position);
            }
        }
        if (possiblePositions.size() != 1) {
            Collections.sort(possiblePositions);
        }
        return possiblePositions;
    }

    private Position getClosestPosition(Position cursor) {
        List<Position> possiblePositions = getPossiblePositions(cursor.getX(), cursor.getZ());
        for (int delta = 0; delta < battlefield.getHeight() - 1; delta++) {
            Position deltaUp = new Position(cursor.getX(), cursor.getY() + delta, cursor.getZ());
            Position deltaDown = new Position(cursor.getX(), cursor.getY() - delta, cursor.getZ());
            if (possiblePositions.contains(deltaUp)) {
                return deltaUp;
            }
            if (possiblePositions.contains(deltaDown)) {
                return deltaDown;
            }
        }
        return cursor;
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

    public void setBattlefield(Battlefield battlefield) {
        this.battlefield = battlefield;

        southPointOfView = new PositionAbsolute(battlefield.getLength(), battlefield.getHeight(), battlefield.getDepth());
        northPointOfView = new PositionAbsolute(0, battlefield.getHeight(), 0);
        eastPointOfView = new PositionAbsolute(battlefield.getLength(), battlefield.getHeight(), 0);
        westPointOfView = new PositionAbsolute(0, battlefield.getHeight(), battlefield.getDepth());

        extractBattlefieldRepresentation(battlefield);

    }

    public boolean engineIsBusy() {
        return engineIsBusy;
    }

    public void setEngineIsBusy(boolean engineIsBusy) {
        this.engineIsBusy = engineIsBusy;
    }
}
