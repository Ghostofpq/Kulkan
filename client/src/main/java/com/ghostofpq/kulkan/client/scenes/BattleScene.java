package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.*;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.client.utils.HighlightColor;
import com.ghostofpq.kulkan.client.utils.InputManager;
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
    // COMMUNICATIONS
    private String gameQueueName;
    private Channel channelGameOut;
    private String gameId;
    private int playerNumber;
    // GRAPHICS
    private PositionAbsolute southPointOfView;
    private PositionAbsolute northPointOfView;
    private PositionAbsolute eastPointOfView;
    private PositionAbsolute westPointOfView;
    private PointOfView currentPointOfView;
    private boolean engineIsBusy;
    private Position cursor;
    private Battlefield battlefield;
    private Map<Position, Cube> battlefieldRepresentation;
    private List<DrawableObject> drawableObjectList;
    private GameCharacter currentGameCharacter;
    private GameCharacterRepresentation currentGameCharacterRepresentation;
    private GameCharacterRepresentation targetGameCharacterRepresentation;
    private CharacterRender characterRenderLeft;
    private CharacterRender characterRenderRight;
    private MenuSelectAction menuSelectAction;
    // LOGIC
    private BattleSceneState currentState;
    private List<Position> possiblePositionsToMove;
    private List<Position> possiblePositionsToAttack;
    private List<Position> positionsToSelect;
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
        // COMMUNICATIONS
        gameQueueName = null;
        channelGameOut = null;
        gameId = null;
        playerNumber = 0;
        // GRAPHICS
        southPointOfView = null;
        northPointOfView = null;
        eastPointOfView = null;
        westPointOfView = null;
        currentPointOfView = GraphicsManager.getInstance().getCurrentPointOfView();
        cursor = new Position(4, 0, 4);
        battlefield = null;
        battlefieldRepresentation = new HashMap<Position, Cube>();
        drawableObjectList = new ArrayList<DrawableObject>();
        currentGameCharacter = null;
        currentGameCharacterRepresentation = null;
        targetGameCharacterRepresentation = null;
        characterRenderLeft = null;
        characterRenderRight = null;
        List<String> options = new ArrayList<String>();
        options.add("Move");
        options.add("Attack");
        options.add("End Turn");
        menuSelectAction = new MenuSelectAction(300, 0, 200, 100, 2);
        // LOGIC
        currentState = BattleSceneState.PENDING;
        possiblePositionsToMove = new ArrayList<Position>();
        possiblePositionsToAttack = new ArrayList<Position>();
        positionsToSelect = new ArrayList<Position>();
        characterListToDeploy = new ArrayList<GameCharacter>();
        characterRepresentationList = new ArrayList<GameCharacterRepresentation>();

        GraphicsManager.getInstance().setupLights();
        GraphicsManager.getInstance().ready3D();
        GraphicsManager.getInstance().requestCenterPosition(cursor);
    }

    public void setBattlefield(Battlefield battlefield) {
        this.battlefield = battlefield;

        southPointOfView = new PositionAbsolute(battlefield.getLength(), battlefield.getHeight(), battlefield.getDepth());
        northPointOfView = new PositionAbsolute(0, battlefield.getHeight(), 0);
        eastPointOfView = new PositionAbsolute(battlefield.getLength(), battlefield.getHeight(), 0);
        westPointOfView = new PositionAbsolute(0, battlefield.getHeight(), battlefield.getDepth());

        extractBattlefieldRepresentation(battlefield);
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
        if (GraphicsManager.getInstance().isBusy()) {
            busy = true;
        }
        if (pointOfViewHasChanged()) {
            sortToDrawList();
        }
        setEngineIsBusy(busy);
        for (DrawableObject drawableObject : drawableObjectList) {
            drawableObject.update(deltaTime);
        }
        battlefieldRepresentation.get(cursor).setHighlight(HighlightColor.BLUE);
    }

    private void manageInputUp() {
        if (currentState.equals(BattleSceneState.DEPLOY_POSITION)
                || currentState.equals(BattleSceneState.MOVE)
                || currentState.equals(BattleSceneState.ATTACK)
                || currentState.equals(BattleSceneState.PENDING)) {
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
        } else if (currentState.equals(BattleSceneState.DEPLOY_HEADING_ANGLE)
                || currentState.equals(BattleSceneState.END_TURN)) {
            switch (GraphicsManager.getInstance().getCurrentPointOfView()) {
                case EAST:
                    currentGameCharacterRepresentation.setHeadingAngle(PointOfView.WEST);
                    break;
                case NORTH:
                    currentGameCharacterRepresentation.setHeadingAngle(PointOfView.SOUTH);
                    break;
                case SOUTH:
                    currentGameCharacterRepresentation.setHeadingAngle(PointOfView.NORTH);
                    break;
                case WEST:
                    currentGameCharacterRepresentation.setHeadingAngle(PointOfView.EAST);
                    break;
            }
        } else if (currentState.equals(BattleSceneState.ACTION)) {
            menuSelectAction.decrementOptionsIndex();
        }
    }

    private void manageInputDown() {
        if (currentState.equals(BattleSceneState.DEPLOY_POSITION)
                || currentState.equals(BattleSceneState.MOVE)
                || currentState.equals(BattleSceneState.ATTACK)
                || currentState.equals(BattleSceneState.PENDING)) {
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
        } else if (currentState.equals(BattleSceneState.DEPLOY_HEADING_ANGLE)
                || currentState.equals(BattleSceneState.END_TURN)) {
            switch (GraphicsManager.getInstance().getCurrentPointOfView()) {
                case EAST:
                    currentGameCharacterRepresentation.setHeadingAngle(PointOfView.EAST);
                    break;
                case NORTH:
                    currentGameCharacterRepresentation.setHeadingAngle(PointOfView.NORTH);
                    break;
                case SOUTH:
                    currentGameCharacterRepresentation.setHeadingAngle(PointOfView.SOUTH);
                    break;
                case WEST:
                    currentGameCharacterRepresentation.setHeadingAngle(PointOfView.WEST);
                    break;
            }
        } else if (currentState.equals(BattleSceneState.ACTION)) {
            menuSelectAction.incrementOptionsIndex();
        }
    }

    private void manageInputLeft() {
        if (currentState.equals(BattleSceneState.DEPLOY_POSITION)
                || currentState.equals(BattleSceneState.MOVE)
                || currentState.equals(BattleSceneState.ATTACK)
                || currentState.equals(BattleSceneState.PENDING)) {
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
        } else if (currentState.equals(BattleSceneState.DEPLOY_HEADING_ANGLE)
                || currentState.equals(BattleSceneState.END_TURN)) {
            switch (GraphicsManager.getInstance().getCurrentPointOfView()) {
                case EAST:
                    currentGameCharacterRepresentation.setHeadingAngle(PointOfView.SOUTH);
                    break;
                case NORTH:
                    currentGameCharacterRepresentation.setHeadingAngle(PointOfView.EAST);
                    break;
                case SOUTH:
                    currentGameCharacterRepresentation.setHeadingAngle(PointOfView.WEST);
                    break;
                case WEST:
                    currentGameCharacterRepresentation.setHeadingAngle(PointOfView.NORTH);
                    break;
            }
        } else if (currentState.equals(BattleSceneState.ACTION)) {
            menuSelectAction.decrementOptionsIndex();
        }
    }

    private void manageInputRight() {
        if (currentState.equals(BattleSceneState.DEPLOY_POSITION)
                || currentState.equals(BattleSceneState.MOVE)
                || currentState.equals(BattleSceneState.ATTACK)
                || currentState.equals(BattleSceneState.PENDING)) {
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
        } else if (currentState.equals(BattleSceneState.DEPLOY_HEADING_ANGLE)
                || currentState.equals(BattleSceneState.END_TURN)) {
            switch (GraphicsManager.getInstance().getCurrentPointOfView()) {
                case EAST:
                    currentGameCharacterRepresentation.setHeadingAngle(PointOfView.NORTH);
                    break;
                case NORTH:
                    currentGameCharacterRepresentation.setHeadingAngle(PointOfView.WEST);
                    break;
                case SOUTH:
                    currentGameCharacterRepresentation.setHeadingAngle(PointOfView.EAST);
                    break;
                case WEST:
                    currentGameCharacterRepresentation.setHeadingAngle(PointOfView.SOUTH);
                    break;
            }
        } else if (currentState.equals(BattleSceneState.ACTION)) {
            menuSelectAction.incrementOptionsIndex();
        }
    }

    private void manageInputRotateLeft() {
        if (currentState.equals(BattleSceneState.DEPLOY_POSITION)
                || currentState.equals(BattleSceneState.DEPLOY_HEADING_ANGLE)
                || currentState.equals(BattleSceneState.MOVE)
                || currentState.equals(BattleSceneState.ATTACK)
                || currentState.equals(BattleSceneState.PENDING)) {
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
    }

    private void manageInputRotateRight() {
        if (currentState.equals(BattleSceneState.DEPLOY_POSITION)
                || currentState.equals(BattleSceneState.DEPLOY_HEADING_ANGLE)
                || currentState.equals(BattleSceneState.MOVE)
                || currentState.equals(BattleSceneState.ATTACK)
                || currentState.equals(BattleSceneState.PENDING)) {
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
    }

    private void manageInputValidate() {
        switch (currentState) {
            case DEPLOY_POSITION:
                deployCharacterPosition();
                break;
            case DEPLOY_HEADING_ANGLE:
                deployCharacterHeadingAngle();
                break;
            case ACTION:
                if (menuSelectAction.getSelectedOption().equals(MenuSelectAction.MenuSelectActions.MOVE)) {
                    sendPositionToMoveRequest();
                    currentState = BattleSceneState.WAITING_SERVER_RESPONSE_MOVE;
                } else if (menuSelectAction.getSelectedOption().equals(MenuSelectAction.MenuSelectActions.ATTACK)) {
                    sendPositionToAttackRequest();
                    currentState = BattleSceneState.WAITING_SERVER_RESPONSE_ATTACK;
                } else if (menuSelectAction.getSelectedOption().equals(MenuSelectAction.MenuSelectActions.END_TURN)) {
                    currentState = BattleSceneState.END_TURN;
                }
                break;
            case MOVE:
                sendActionMove();
                cleanHighlightPossiblePositionsToMove();
                possiblePositionsToMove = new ArrayList<Position>();
                currentState = BattleSceneState.PENDING;
                break;
            case ATTACK:
                sendActionAttack();
                cleanHighlightPossiblePositionsToAttack();
                possiblePositionsToAttack = new ArrayList<Position>();
                currentState = BattleSceneState.PENDING;
                break;
            case END_TURN:
                sendEndTurn();
                currentState = BattleSceneState.PENDING;
                break;
        }
    }

    private void manageInputCancel() {
        Client.getInstance().setCurrentScene(LobbyScene.getInstance());
    }

    @Override
    public void manageInput() {
        if (!engineIsBusy()) {
            while (Keyboard.next()) {
                if (Keyboard.getEventKeyState()) {
                    if (InputManager.getInstance().getInput(Keyboard.getEventKey()) != null) {
                        switch (InputManager.getInstance().getInput(Keyboard.getEventKey())) {
                            case UP:
                                manageInputUp();
                                break;
                            case DOWN:
                                manageInputDown();
                                break;
                            case LEFT:
                                manageInputLeft();
                                break;
                            case RIGHT:
                                manageInputRight();
                                break;
                            case ROTATE_LEFT:
                                manageInputRotateLeft();
                                break;
                            case ROTATE_RIGHT:
                                manageInputRotateRight();
                                break;
                            case ZOOM_IN:
                                GraphicsManager.getInstance().zoomIn();
                                break;
                            case ZOOM_OUT:
                                GraphicsManager.getInstance().zoomOut();
                                break;
                            case VALIDATE:
                                manageInputValidate();
                                break;
                            case CANCEL:
                                manageInputCancel();
                                break;
                            case SWITCH:

                                break;
                        }
                    }
                }
            }
        }
    }

    public void postMessage(ClientMessage message) {
        try {
            log.debug(" [-] POST MESSAGE {} ON {}", message.getType(), gameQueueName);
            channelGameOut.basicPublish("", gameQueueName, null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void manageMessageStartDeployment(Message message) {
        log.debug(" [-] START_DEPLOYMENT");
        MessageDeploymentStart messageDeploymentStart = (MessageDeploymentStart) message;
        characterListToDeploy = messageDeploymentStart.getCharacterList();
        playerNumber = messageDeploymentStart.getPlayerNumber();
        currentGameCharacter = characterListToDeploy.get(0);
        characterRenderLeft = new CharacterRender(0, 0, 300, 100, 2, currentGameCharacter);
        highlightDeploymentZone();
        currentState = BattleSceneState.DEPLOY_POSITION;
    }

    private void manageMessageOtherPlayerDeployment(Message message) {
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
    }

    private void manageMessageAllCharacter(Message message) {
        MessageUpdateCharacters messageUpdateCharacters = (MessageUpdateCharacters) message;
        log.debug(" [-] UPDATE ALL CHARACTERS");
        for (GameCharacter gameCharacter : messageUpdateCharacters.getCharacterPositionMap().keySet()) {
            for (GameCharacterRepresentation gameCharacterRepresentation : characterRepresentationList) {
                if (gameCharacterRepresentation.getCharacter().equals(gameCharacter)) {
                    gameCharacterRepresentation.updateCharacter(gameCharacter);
                    if (!gameCharacterRepresentation.getPosition().equals(messageUpdateCharacters.getCharacterPositionMap().get(gameCharacter))) {
                        log.error("[X] CHAR {} IS MISPLACED ! {}/{}", gameCharacter.getName(),
                                gameCharacterRepresentation.getPosition(), messageUpdateCharacters.getCharacterPositionMap().get(gameCharacter));
                    }
                }
            }
        }
    }

    private void manageMessageCharacterToPlay(Message message) {
        MessageCharacterToPlay messageCharacterToPlay = (MessageCharacterToPlay) message;
        log.debug(" [-] CHARACTER TO PLAY : {}", messageCharacterToPlay.getCharactertoPlay().getName());

        currentGameCharacter = messageCharacterToPlay.getCharactertoPlay();
        for (GameCharacterRepresentation characterRepresentation : characterRepresentationList) {
            if (characterRepresentation.getCharacter().equals(currentGameCharacter)) {
                currentGameCharacterRepresentation = characterRepresentation;
                break;
            }
        }

        battlefieldRepresentation.get(cursor).setHighlight(HighlightColor.NONE);
        cursor = messageCharacterToPlay.getPositionOfChar();
        battlefieldRepresentation.get(cursor).setHighlight(HighlightColor.BLUE);
        GraphicsManager.getInstance().requestCenterPosition(cursor);
        updateCursorTarget();
        menuSelectAction.reinitMenu();
        if (currentGameCharacter.hasMoved()) {
            menuSelectAction.setHasMoved();
        }
        if (currentGameCharacter.hasActed()) {
            menuSelectAction.setHasActed();
        }
        currentState = BattleSceneState.ACTION;
    }

    private void manageMessageCharacterPositionToMoveResponse(Message message) {
        MessagePositionToMoveResponse messagePositionToMoveResponse = (MessagePositionToMoveResponse) message;
        if (currentState.equals(BattleSceneState.WAITING_SERVER_RESPONSE_MOVE)) {
            log.debug(" [-] RECEIVED POSITIONS TO MOVE");
            possiblePositionsToMove = messagePositionToMoveResponse.getPossiblePositionsToMove();
            highlightPossiblePositionsToMove();
            currentState = BattleSceneState.MOVE;
        } else {
            log.error(" [-] RECEIVED POSITIONS TO MOVE");
        }
    }

    private void manageMessageCharacterMoves(Message message) {
        MessageCharacterMoves messageCharacterMoves = (MessageCharacterMoves) message;
        for (GameCharacterRepresentation characterRepresentation : characterRepresentationList) {
            if (characterRepresentation.getCharacter().equals(messageCharacterMoves.getCharacter())) {
                characterRepresentation.setPositionsToGo(messageCharacterMoves.getPath());
                characterRepresentation.setHasMoved(messageCharacterMoves.getCharacter().hasMoved());
                break;
            }
        }
    }

    private void manageMessageCharacterPositionToAttackResponse(Message message) {
        MessagePositionToAttackResponse messagePositionToAttackResponse = (MessagePositionToAttackResponse) message;
        if (currentState.equals(BattleSceneState.WAITING_SERVER_RESPONSE_ATTACK)) {
            log.debug(" [-] RECEIVED POSITIONS TO ATTACK");
            possiblePositionsToAttack = messagePositionToAttackResponse.getPossiblePositionsToAttack();
            highlightPossiblePositionsToAttack();
            currentState = BattleSceneState.ATTACK;
        } else {
            log.error(" [-] RECEIVED POSITIONS TO ATTACK");
        }
    }

    private void manageMessageCharacterAttacks(Message message) {
        MessageCharacterAttacks messageCharacterAttacks = (MessageCharacterAttacks) message;
        GameCharacterRepresentation attackingCharRepresentation = null;
        GameCharacterRepresentation attackedCharRepresentation = null;
        for (GameCharacterRepresentation characterRepresentation : characterRepresentationList) {
            if (characterRepresentation.getCharacter().equals(messageCharacterAttacks.getAttackingChar())) {
                attackingCharRepresentation = characterRepresentation;
            }
            if (characterRepresentation.getCharacter().equals(messageCharacterAttacks.getAttackingChar())) {
                attackedCharRepresentation = characterRepresentation;
            }
        }
        if (null != attackingCharRepresentation && null != attackedCharRepresentation) {
            int damages = messageCharacterAttacks.getDamages();
            boolean crit = messageCharacterAttacks.crits();
            boolean hit = messageCharacterAttacks.hits();
            log.debug("{} takes {} damage from {}", attackedCharRepresentation.getCharacter().getName(), damages, attackingCharRepresentation.getCharacter().getName());
            attackedCharRepresentation.getCharacter().addHealthPoint(-damages);
        }
    }

    @Override
    public void receiveMessage() {
        if (!engineIsBusy()) {
            Message message = Client.getInstance().receiveMessage();
            if (null != message) {
                switch (message.getType()) {
                    case START_DEPLOYMENT:
                        manageMessageStartDeployment(message);
                        break;
                    case OTHER_PLAYER_DEPLOYMENT:
                        manageMessageOtherPlayerDeployment(message);
                        break;
                    case ALL_CHARACTERS:
                        manageMessageAllCharacter(message);
                        break;
                    case CHARACTER_TO_PLAY:
                        manageMessageCharacterToPlay(message);
                        break;
                    case CHARACTER_POSITION_TO_MOVE_RESPONSE:
                        manageMessageCharacterPositionToMoveResponse(message);
                        break;
                    case CHARACTER_MOVES:
                        manageMessageCharacterMoves(message);
                        break;
                    case CHARACTER_POSITION_TO_ATTACK_RESPONSE:
                        manageMessageCharacterPositionToAttackResponse(message);
                        break;
                    case CHARACTER_ATTACKS:
                        manageMessageCharacterAttacks(message);
                        break;
                    default:
                        log.error(" [X] UNEXPECTED MESSAGE : {}", message.getType());
                        break;
                }
            }
        }
    }

    @Override
    public void closeConnections() {
    }

    @Override
    public void render() {
        render3D();
        render2D();
    }

    private void render3D() {
        GraphicsManager.getInstance().make3D();
        GraphicsManager.getInstance().update3DMovement();
        for (int i = 0; i < drawableObjectList.size(); i++) {
            drawableObjectList.get(i).draw();
        }
    }

    private void render2D() {
        GraphicsManager.getInstance().make2D();
        if (null != currentGameCharacter) {
            characterRenderLeft = new CharacterRender(0, 0, 300, 100, 2, currentGameCharacter);
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

    public void deployCharacterPosition() {
        if (battlefield.getDeploymentZones().get(playerNumber).contains(cursor)) {
            log.debug(" [-] PLACE CHARACTER AT {}", cursor.toString());
            Position position = new Position(cursor);
            position.plusY(1);
            currentGameCharacter.setHeadingAngle(battlefield.getStartingPointsOfViewForPlayer(playerNumber));
            GameCharacterRepresentation gameCharacterRepresentation = new GameCharacterRepresentation(currentGameCharacter, position, playerNumber);
            currentGameCharacterRepresentation = gameCharacterRepresentation;
            characterRepresentationList.add(gameCharacterRepresentation);
            drawableObjectList.add(gameCharacterRepresentation);
            sortToDrawList();
            currentState = BattleSceneState.DEPLOY_HEADING_ANGLE;
        }
    }

    public void deployCharacterHeadingAngle() {
        characterListToDeploy.remove(currentGameCharacter);
        battlefield.getDeploymentZones().get(playerNumber).remove(cursor);
        currentGameCharacterRepresentation = null;
        updateCursorTarget();
        if (characterListToDeploy.isEmpty()) {
            characterRenderLeft = null;
            sendDeploymentResult();
            cleanHighlightDeploymentZone();

            currentGameCharacter = null;

            currentState = BattleSceneState.PENDING;
        } else {
            currentGameCharacter = characterListToDeploy.get(0);
            currentState = BattleSceneState.DEPLOY_POSITION;
            characterRenderLeft = new CharacterRender(0, 0, 300, 100, 2, currentGameCharacter);
        }
    }

    private void sendPositionToMoveRequest() {
        MessagePositionToMoveRequest messagePositionToMoveRequest = new MessagePositionToMoveRequest(Client.getInstance().getTokenKey(), currentGameCharacter);
        postMessage(messagePositionToMoveRequest);
    }

    private void sendActionMove() {
        if (possiblePositionsToMove.contains(cursor)) {
            MessageCharacterActionMove messageCharacterActionMove = new MessageCharacterActionMove(Client.getInstance().getTokenKey(), currentGameCharacter, cursor);
            postMessage(messageCharacterActionMove);
        } else {
            resetOldHighlight();
            cursor = new Position(currentGameCharacterRepresentation.getFootPosition());
            GraphicsManager.getInstance().requestCenterPosition(cursor);
            currentState = BattleSceneState.ACTION;
        }
        cleanHighlightPossiblePositionsToMove();
    }

    private void sendPositionToAttackRequest() {
        MessagePositionToAttackRequest messagePositionToAttackRequest = new MessagePositionToAttackRequest(Client.getInstance().getTokenKey(), currentGameCharacter);
        postMessage(messagePositionToAttackRequest);
    }

    private void sendActionAttack() {
        if (possiblePositionsToAttack.contains(cursor)) {
            MessageCharacterActionAttack messageCharacterActionAttack = new MessageCharacterActionAttack(Client.getInstance().getTokenKey(), currentGameCharacter, cursor);
            postMessage(messageCharacterActionAttack);
        } else {
            resetOldHighlight();
            cursor = new Position(currentGameCharacterRepresentation.getFootPosition());
            GraphicsManager.getInstance().requestCenterPosition(cursor);
            currentState = BattleSceneState.ACTION;
        }
        cleanHighlightPossiblePositionsToAttack();
    }

    private void sendEndTurn() {
        MessageCharacterEndTurn messageCharacterEndturn = new MessageCharacterEndTurn(Client.getInstance().getTokenKey(), currentGameCharacter);
        postMessage(messageCharacterEndturn);
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

    private void highlightPossiblePositionsToMove() {
        for (Position possiblePositionToMove : possiblePositionsToMove) {
            if (battlefieldRepresentation.containsKey(possiblePositionToMove)) {
                log.debug("highlight green {}", possiblePositionToMove.toString());
                battlefieldRepresentation.get(possiblePositionToMove).setHighlight(HighlightColor.GREEN);
            } else {
                log.error("{} can't be highlighted", possiblePositionToMove.toString());
            }
        }
    }

    private void cleanHighlightPossiblePositionsToMove() {
        for (Position possiblePositionToMove : possiblePositionsToMove) {
            if (battlefieldRepresentation.get(possiblePositionToMove).getHighlight().equals(HighlightColor.GREEN)) {
                battlefieldRepresentation.get(possiblePositionToMove).setHighlight(HighlightColor.NONE);
            }
        }
    }

    private void highlightPossiblePositionsToAttack() {
        for (Position possiblePositionToAttack : possiblePositionsToAttack) {
            battlefieldRepresentation.get(possiblePositionToAttack).setHighlight(HighlightColor.RED);
        }
    }

    private void cleanHighlightPossiblePositionsToAttack() {
        for (Position possiblePositionToAttack : possiblePositionsToAttack) {
            if (battlefieldRepresentation.get(possiblePositionToAttack).getHighlight().equals(HighlightColor.RED)) {
                battlefieldRepresentation.get(possiblePositionToAttack).setHighlight(HighlightColor.NONE);
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
        if (currentState.equals(BattleSceneState.DEPLOY_POSITION)) {
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

    public boolean engineIsBusy() {
        return engineIsBusy;
    }

    public void setEngineIsBusy(boolean engineIsBusy) {
        this.engineIsBusy = engineIsBusy;
    }
}
