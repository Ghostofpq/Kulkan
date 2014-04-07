package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.ClientContext;
import com.ghostofpq.kulkan.client.ClientMessenger;
import com.ghostofpq.kulkan.client.graphics.CharacterRender;
import com.ghostofpq.kulkan.client.graphics.HUD.Button;
import com.ghostofpq.kulkan.client.graphics.HUD.Frame;
import com.ghostofpq.kulkan.client.graphics.HUD.PopUp;
import com.ghostofpq.kulkan.client.graphics.MenuSelectAction;
import com.ghostofpq.kulkan.client.graphics.MenuSelectCapacity;
import com.ghostofpq.kulkan.client.graphics.ingame.*;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
import com.ghostofpq.kulkan.client.utils.HighlightColor;
import com.ghostofpq.kulkan.client.utils.InputManager;
import com.ghostofpq.kulkan.client.utils.TextureKey;
import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.commons.PositionAbsolute;
import com.ghostofpq.kulkan.entities.battlefield.BattleSceneState;
import com.ghostofpq.kulkan.entities.battlefield.Battlefield;
import com.ghostofpq.kulkan.entities.battlefield.BattlefieldElement;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.job.capacity.Move;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.game.*;
import com.ghostofpq.kulkan.entities.messages.game.capacity.MessageCapacityFireball;
import com.ghostofpq.kulkan.entities.messages.user.MessagePlayerUpdate;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.ByteBuffer;
import java.util.*;

@Slf4j
public class BattleScene implements Scene {
    // COMMUNICATIONS
    private String gameId;
    private int playerNumber;
    // GRAPHICS
    private Position mousePosition;
    private PositionAbsolute southPointOfView;
    private PositionAbsolute northPointOfView;
    private PositionAbsolute eastPointOfView;
    private PositionAbsolute westPointOfView;
    private PointOfView currentPointOfView;
    private boolean engineIsBusy;
    private Battlefield battlefield;
    private Map<Position, Cube> battlefieldRepresentation;
    private List<DrawableObject> drawableObjectList;
    private GameCharacter currentGameCharacter;
    private GameCharacterRepresentation currentGameCharacterRepresentation;
    private GameCharacterRepresentation targetGameCharacterRepresentation;
    private CharacterRender characterRenderLeft;
    private CharacterRender characterRenderRight;
    private MenuSelectAction menuSelectAction;
    private Button gameOverButton;
    private MenuSelectCapacity menuSelectCapacity;
    private ActionButton actionButtonMove;
    private ActionButton actionButtonAttack;
    private ActionButton actionButtonCapacity;
    private ActionButton actionButtonEndTurn;

    // LOGIC
    private BattleSceneState currentState;
    private List<Position> possiblePositionsToMove;
    private List<Position> possiblePositionsToAttack;
    private List<Position> possiblePositionsToUseCapacity;
    private List<Position> capacityAreaOfEffect;
    private List<Position> positionsToSelect;
    private List<GameCharacter> characterListToDeploy;
    private List<GameCharacterRepresentation> characterRepresentationList;
    private Move selectedMove;
    // POPUP
    private PopUp popUp;
    // FRAME
    private Frame frame;
    private int x;
    private int y;
    private boolean frameClicked;

    private long lastTimeMouseWasClicked = System.currentTimeMillis();
    private long deltaMillis = 100;

    @Autowired
    private Client client;
    @Autowired
    private ClientContext clientContext;
    @Autowired
    private ClientMessenger clientMessenger;

    public BattleScene() {
    }

    @Override
    public void init() {
        // COMMUNICATIONS
        gameId = null;
        playerNumber = 0;
        // GRAPHICS
        southPointOfView = null;
        northPointOfView = null;
        eastPointOfView = null;
        westPointOfView = null;
        currentPointOfView = GraphicsManager.getInstance().getCurrentPointOfView();
        battlefield = null;
        battlefieldRepresentation = new HashMap<Position, Cube>();
        drawableObjectList = new ArrayList<DrawableObject>();
        currentGameCharacter = null;
        currentGameCharacterRepresentation = null;
        targetGameCharacterRepresentation = null;
        characterRenderLeft = null;
        characterRenderRight = null;
        menuSelectAction = new MenuSelectAction(300, 0, 200, 100, 2);
        menuSelectCapacity = null;
        actionButtonMove = null;
        // LOGIC
        currentState = BattleSceneState.PENDING;
        possiblePositionsToMove = new ArrayList<Position>();
        possiblePositionsToAttack = new ArrayList<Position>();
        positionsToSelect = new ArrayList<Position>();
        characterListToDeploy = new ArrayList<GameCharacter>();
        characterRepresentationList = new ArrayList<GameCharacterRepresentation>();
        possiblePositionsToUseCapacity = new ArrayList<Position>();
        capacityAreaOfEffect = new ArrayList<Position>();
        selectedMove = null;
        GraphicsManager.getInstance().setupLights();
        GraphicsManager.getInstance().ready3D();
        GraphicsManager.getInstance().requestCenterPosition(new Position(4, 0, 4));

        frame = new Frame(0, 0, clientContext.getCurrentResolution().getWidth(), clientContext.getCurrentResolution().getHeight(), clientContext.getCurrentResolution().getWidth() / 64, clientContext.getCurrentResolution().getWidth() / 64, TextureKey.COMMON_EXT_FRAME);
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
        clientMessenger.openChannelGame(gameId);
    }

    @Override
    public void update(long deltaTime) {
        for (DrawableObject drawableObject : drawableObjectList) {
            drawableObject.update(deltaTime);
        }

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

        setHighlights();
    }

    private void manageInputUp() {
        if (currentState.equals(BattleSceneState.DEPLOY_HEADING_ANGLE)
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
        } else if (currentState.equals(BattleSceneState.CAPACITY_SELECT)) {
            menuSelectCapacity.decrementOptionsIndex();
        }
    }

    private void manageInputDown() {
        if (currentState.equals(BattleSceneState.DEPLOY_HEADING_ANGLE)
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
        } else if (currentState.equals(BattleSceneState.CAPACITY_SELECT)) {
            menuSelectCapacity.incrementOptionsIndex();
        }
    }

    private void manageInputLeft() {
        if (currentState.equals(BattleSceneState.DEPLOY_HEADING_ANGLE)
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
        } else if (currentState.equals(BattleSceneState.CAPACITY_SELECT)) {
            menuSelectCapacity.decrementOptionsIndex();
        }
    }

    private void manageInputRight() {
        if (currentState.equals(BattleSceneState.DEPLOY_HEADING_ANGLE)
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
        } else if (currentState.equals(BattleSceneState.CAPACITY_SELECT)) {
            menuSelectCapacity.incrementOptionsIndex();
        }
    }

    private void manageInputRotateLeft() {
        if (currentState.equals(BattleSceneState.DEPLOY_POSITION)
                || currentState.equals(BattleSceneState.DEPLOY_HEADING_ANGLE)
                || currentState.equals(BattleSceneState.MOVE)
                || currentState.equals(BattleSceneState.ATTACK)
                || currentState.equals(BattleSceneState.PENDING)
                || currentState.equals(BattleSceneState.ACTION)
                || currentState.equals(BattleSceneState.CAPACITY_PLACE)) {
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
                || currentState.equals(BattleSceneState.PENDING)
                || currentState.equals(BattleSceneState.ACTION)
                || currentState.equals(BattleSceneState.CAPACITY_PLACE)) {
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

    private void manageInputAction() {
        if (actionButtonMove.isHovered() && !actionButtonMove.isUsed()) {
            sendPositionToMoveRequest();
            currentState = BattleSceneState.WAITING_SERVER_RESPONSE_MOVE;
            cleanActionButtons();
        } else if (actionButtonAttack.isHovered() && !actionButtonAttack.isUsed()) {
            sendPositionToAttackRequest();
            currentState = BattleSceneState.WAITING_SERVER_RESPONSE_ATTACK;
            cleanActionButtons();
        } else if (actionButtonCapacity.isHovered() && !actionButtonCapacity.isUsed()) {
            menuSelectCapacity = new MenuSelectCapacity(300, 0, 200, 100, 2, currentGameCharacter.getJob(currentGameCharacter.getCurrentJob()).getUnlockedMoves(), currentGameCharacter.getCurrentManaPoint());
            currentState = BattleSceneState.CAPACITY_SELECT;
            cleanActionButtons();
        } else if (actionButtonEndTurn.isHovered()) {
            currentState = BattleSceneState.END_TURN;
            cleanActionButtons();
        }
    }

    private void manageInputCancel() {
        switch (currentState) {
            case CAPACITY_USE:
                currentState = BattleSceneState.ACTION;
                capacityAreaOfEffect = new ArrayList<Position>();
                battlefieldRepresentation.get(mousePosition).setHighlight(HighlightColor.NONE);
                mousePosition = currentGameCharacter.getPosition().plusYNew(-1);
                battlefieldRepresentation.get(mousePosition).setHighlight(HighlightColor.BLUE);
                GraphicsManager.getInstance().requestCenterPosition(mousePosition);
                break;
            default:
                client.setCurrentScene(client.getLobbyScene());
        }
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
                            case CANCEL:
                                manageInputCancel();
                                break;
                            case SWITCH:

                                break;
                        }
                    }
                }
            }
            while (Mouse.next()) {
                //LEFT CLICK
                if (Mouse.isButtonDown(0)) {
                    if (okClick()) {
                        switch (currentState) {
                            case ACTION:
                                if (actionButtonMove != null && actionButtonAttack != null && actionButtonCapacity != null && actionButtonEndTurn != null) {
                                    if (actionButtonMove.isHovered() || actionButtonAttack.isHovered() || actionButtonCapacity.isHovered() || actionButtonEndTurn.isHovered()) {
                                        manageInputAction();
                                    }
                                }
                                break;
                            case DEPLOY_POSITION:
                                if (null != mousePosition) {
                                    deployCharacterPosition();
                                }
                                break;
                            case DEPLOY_HEADING_ANGLE:
                                deployCharacterHeadingAngle();
                                break;
                            case MOVE:
                                if (null != mousePosition) {
                                    possiblePositionsToMove = new ArrayList<Position>();
                                    sendActionMove();
                                    possiblePositionsToMove = new ArrayList<Position>();
                                    currentState = BattleSceneState.PENDING;
                                }
                                break;
                            case ATTACK:
                                if (null != mousePosition) {
                                    possiblePositionsToAttack = new ArrayList<Position>();
                                    sendActionAttack();
                                    possiblePositionsToAttack = new ArrayList<Position>();
                                    currentState = BattleSceneState.PENDING;
                                }
                                break;
                            case CAPACITY_SELECT:
                                selectedMove = menuSelectCapacity.getSelectedOption();
                                sendPositionToUseCapacityRequest();
                                menuSelectCapacity = null;
                                currentState = BattleSceneState.WAITING_SERVER_RESPONSE_CAPACITY;
                                break;
                            case CAPACITY_PLACE:
                                if (null != mousePosition) {
                                    possiblePositionsToUseCapacity = new ArrayList<Position>();
                                    sendCapacityAOERequest();
                                    currentState = BattleSceneState.WAITING_SERVER_RESPONSE_CAPACITY_AOE;
                                }
                                break;
                            case CAPACITY_USE:
                                if (null != mousePosition) {
                                    capacityAreaOfEffect = new ArrayList<Position>();
                                    sendActionCapacity();
                                    currentState = BattleSceneState.PENDING;
                                }
                                break;
                            case END_TURN:
                                sendEndTurn();
                                currentState = BattleSceneState.PENDING;
                                currentGameCharacter = null;
                                break;
                            case GAME_OVER:
                                if (gameOverButton.isClicked()) {
                                    gameOverButton.onClick();
                                }
                                break;
                        }
                        if (frame.isClicked()) {
                            if (x == -1 && y == -1) {
                                x = Mouse.getX();
                                y = (Display.getHeight() - Mouse.getY());
                                frameClicked = true;
                            }
                        }
                    }
                    //RIGHT CLICK
                } else if (Mouse.isButtonDown(1)) {
                    if (okClick()) {
                        if (currentState == BattleSceneState.ATTACK || currentState == BattleSceneState.MOVE || currentState == BattleSceneState.CAPACITY_PLACE ||
                                currentState == BattleSceneState.CAPACITY_USE || currentState == BattleSceneState.CAPACITY_SELECT || currentState == BattleSceneState.END_TURN) {
                            possiblePositionsToAttack = new ArrayList<Position>();
                            possiblePositionsToMove = new ArrayList<Position>();
                            capacityAreaOfEffect = new ArrayList<Position>();
                            possiblePositionsToUseCapacity = new ArrayList<Position>();
                            prepareActionButtons();
                            currentState = BattleSceneState.ACTION;
                        } else {
                            if (null != mousePosition) {
                                GraphicsManager.getInstance().requestCenterPosition(mousePosition);
                            }
                        }
                    }
                } else {
                    frameClicked = false;
                    x = -1;
                    y = -1;
                }
            }
            if (currentState == BattleSceneState.END_TURN || currentState == BattleSceneState.DEPLOY_HEADING_ANGLE ||
                    currentState == BattleSceneState.ATTACK || currentState == BattleSceneState.MOVE ||
                    currentState == BattleSceneState.CAPACITY_PLACE || currentState == BattleSceneState.CAPACITY_USE) {
                if (null != mousePosition) {
                    currentGameCharacterRepresentation.setHeadingAngle(currentGameCharacter.getPosition().getHeadingAngleFor(mousePosition));
                }
            }
        }
        if (frameClicked && !clientContext.isFullscreen()) {
            Display.setLocation(Display.getX() + (Mouse.getX()) - x, (Display.getY() + (Display.getHeight() - Mouse.getY())) - y);
        }
    }

    public void postMessage(ClientMessage message) {
        clientMessenger.sendMessageToGameService(message);
    }

    private void manageMessageStartDeployment(Message message) {
        log.debug(" [-] START_DEPLOYMENT");
        characterRepresentationList = new ArrayList<GameCharacterRepresentation>();
        Iterator<DrawableObject> iterator = drawableObjectList.iterator();
        while (iterator.hasNext()) {
            DrawableObject drawableObject = iterator.next();
            if (drawableObject instanceof GameCharacterRepresentation) {
                iterator.remove();
            }
        }

        MessageDeploymentStart messageDeploymentStart = (MessageDeploymentStart) message;
        characterListToDeploy = messageDeploymentStart.getCharacterList();
        playerNumber = messageDeploymentStart.getPlayerNumber();
        currentGameCharacter = characterListToDeploy.get(0);
        characterRenderLeft = new CharacterRender(0, 0, 300, 100, 2, currentGameCharacter);
        currentState = BattleSceneState.DEPLOY_POSITION;
    }

    private void manageMessageOtherPlayerDeployment(Message message) {
        MessageDeploymentPositionsOfPlayer messageDeploymentPositionsOfPlayer = (MessageDeploymentPositionsOfPlayer) message;
        log.debug(" [-] DEPLOYMENT OF PLAYER {}", messageDeploymentPositionsOfPlayer.getPlayerNumber());
        for (GameCharacter gameCharacter : messageDeploymentPositionsOfPlayer.getCharacterList()) {
            GameCharacterRepresentation gameCharacterRepresentation = new GameCharacterRepresentation(gameCharacter,
                    gameCharacter.getPosition(),
                    messageDeploymentPositionsOfPlayer.getPlayerNumber());
            characterRepresentationList.add(gameCharacterRepresentation);
            drawableObjectList.add(gameCharacterRepresentation);
            sortToDrawList();
        }
    }

    private void manageMessageAllCharacter(Message message) {
        MessageUpdateCharacters messageUpdateCharacters = (MessageUpdateCharacters) message;
        log.debug(" [-] UPDATE ALL CHARACTERS");
        for (GameCharacter gameCharacter : messageUpdateCharacters.getCharacterList()) {
            for (GameCharacterRepresentation gameCharacterRepresentation : characterRepresentationList) {
                if (gameCharacterRepresentation.getCharacter().equals(gameCharacter)) {
                    gameCharacterRepresentation.updateCharacter(gameCharacter);
                    if (!gameCharacterRepresentation.getPosition().equals(gameCharacter.getPosition())) {
                        log.error("[X] CHAR {} IS MISPLACED ! {}/{}", gameCharacter.getName(),
                                gameCharacterRepresentation.getPosition(), gameCharacter.getPosition());
                    }
                }
            }
        }
    }

    private void manageMessageCharacterToPlay(Message message) {
        MessageCharacterToPlay messageCharacterToPlay = (MessageCharacterToPlay) message;
        log.debug(" [-] CHARACTER TO PLAY : {}", messageCharacterToPlay.getCharacterToPlay().getName());

        for (GameCharacterRepresentation characterRepresentation : characterRepresentationList) {
            if (characterRepresentation.getCharacter().equals(messageCharacterToPlay.getCharacterToPlay())) {
                currentGameCharacterRepresentation = characterRepresentation;
                currentGameCharacter = messageCharacterToPlay.getCharacterToPlay();
                break;
            }
        }

        GraphicsManager.getInstance().requestCenterPosition(messageCharacterToPlay.getPositionOfCursor());

        menuSelectAction.reinitMenu();
        selectedMove = null;

        if (currentGameCharacter.hasMoved()) {
            menuSelectAction.setHasMoved();
        }
        if (currentGameCharacter.hasActed()) {
            menuSelectAction.setHasActed();
        }
        prepareActionButtons();
        currentState = BattleSceneState.ACTION;
    }

    private void cleanActionButtons() {
        drawableObjectList.remove(actionButtonMove);
        drawableObjectList.remove(actionButtonAttack);
        drawableObjectList.remove(actionButtonCapacity);
        drawableObjectList.remove(actionButtonEndTurn);
        actionButtonMove = null;
        actionButtonAttack = null;
        actionButtonCapacity = null;
        actionButtonEndTurn = null;
        sortToDrawList();
    }

    private void prepareActionButtons() {
        actionButtonMove = new ActionButton(currentGameCharacterRepresentation.getPosition(), ActionButtonType.MOVE);
        actionButtonAttack = new ActionButton(currentGameCharacterRepresentation.getPosition(), ActionButtonType.ATTACK);
        actionButtonCapacity = new ActionButton(currentGameCharacterRepresentation.getPosition(), ActionButtonType.CAPACITY);
        actionButtonEndTurn = new ActionButton(currentGameCharacterRepresentation.getPosition(), ActionButtonType.END_TURN);

        drawableObjectList.add(actionButtonMove);
        drawableObjectList.add(actionButtonAttack);
        drawableObjectList.add(actionButtonCapacity);
        drawableObjectList.add(actionButtonEndTurn);

        sortToDrawList();

        if (currentGameCharacter.hasMoved()) {
            actionButtonMove.setUsed(true);
        }
        if (currentGameCharacter.hasActed()) {
            actionButtonAttack.setUsed(true);
            actionButtonCapacity.setUsed(true);
        }
    }

    private void manageMessageCharacterPositionToMoveResponse(Message message) {
        MessagePositionToMoveResponse messagePositionToMoveResponse = (MessagePositionToMoveResponse) message;
        if (currentState.equals(BattleSceneState.WAITING_SERVER_RESPONSE_MOVE)) {
            log.debug(" [-] RECEIVED POSITIONS TO MOVE");
            possiblePositionsToMove = messagePositionToMoveResponse.getPossiblePositionsToMove();
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
            if (characterRepresentation.getCharacter().equals(messageCharacterAttacks.getTargetedChar())) {
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

    private void manageMessageCharacterGainsXP(Message message) {
        MessageCharacterGainsXP messageCharacterGainsXP = (MessageCharacterGainsXP) message;
        for (GameCharacterRepresentation characterRepresentation : characterRepresentationList) {
            if (characterRepresentation.getCharacter().equals(messageCharacterGainsXP.getCharacter())) {
                characterRepresentation.getCharacter().gainXp(messageCharacterGainsXP.getExperiencePoints());
                characterRepresentation.getCharacter().gainJobPoints(messageCharacterGainsXP.getJobPoints());
                log.debug("{} gains {} exp and {} job points", messageCharacterGainsXP.getCharacter().getName(),
                        messageCharacterGainsXP.getExperiencePoints(), messageCharacterGainsXP.getJobPoints());
                break;
            }
        }
    }

    private void manageMessagePositionToUseCapacityResponse(Message message) {
        MessageCharacterPositionToUseCapacityResponse messageCharacterPositionToUseCapacityResponse = (MessageCharacterPositionToUseCapacityResponse) message;
        if (currentState.equals(BattleSceneState.WAITING_SERVER_RESPONSE_CAPACITY)) {
            log.debug(" [-] RECEIVED POSSIBLE POSITION FOR CAPACITY USE");
            possiblePositionsToUseCapacity = messageCharacterPositionToUseCapacityResponse.getPossiblePositionsToUseCapacity();
            currentState = BattleSceneState.CAPACITY_PLACE;
        } else {
            log.error(" [-] RECEIVED POSSIBLE POSITION FOR CAPACITY USE");
        }
    }

    private void manageMessageCapacityAOEResponse(Message message) {
        MessageCapacityAOEResponse messageCapacityAOEResponse = (MessageCapacityAOEResponse) message;
        if (currentState.equals(BattleSceneState.WAITING_SERVER_RESPONSE_CAPACITY_AOE)) {
            log.debug(" [-] RECEIVED CAPACITY AOE");
            capacityAreaOfEffect = messageCapacityAOEResponse.getAreaOfEffect();
            currentState = BattleSceneState.CAPACITY_USE;
        } else {
            log.error(" [-] RECEIVED POSSIBLE POSITION FOR CAPACITY USE");
        }
    }

    private void manageCapacityFireball(Message message) {
        MessageCapacityFireball messageCapacityFireball = (MessageCapacityFireball) message;

        for (GameCharacterRepresentation characterRepresentation : characterRepresentationList) {
            if (characterRepresentation.getCharacter().equals(messageCapacityFireball.getGameCharacter())) {
                characterRepresentation.getCharacter().addManaPoint(-messageCapacityFireball.getManaCost());
            }
        }

        for (GameCharacter gameCharacter : messageCapacityFireball.getGameCharacterDamageMap().keySet()) {
            for (GameCharacterRepresentation characterRepresentation : characterRepresentationList) {
                if (characterRepresentation.getCharacter().equals(gameCharacter)) {
                    characterRepresentation.getCharacter().addHealthPoint(-messageCapacityFireball.getGameCharacterDamageMap().get(gameCharacter));
                }
            }
        }

    }

    private void manageMessageGameOver(Message message) {
        MessageGameEnd messageGameEnd = (MessageGameEnd) message;
        String buttonText;
        if (messageGameEnd.isWinner()) {
            buttonText = "YOU WIN";
        } else {
            buttonText = "YOU LOSE";
        }
        gameOverButton = new Button(300, 400, 50, 50, buttonText) {
            @Override
            public void onClick() {
                client.setCurrentScene(client.getLobbyScene());
            }
        };
        currentState = BattleSceneState.GAME_OVER;
    }

    @Override
    public void receiveMessage() {
        if (!engineIsBusy()) {
            Message message = clientMessenger.receiveMessage();
            if (null != message) {
                log.debug("RECEIVED");
                log.debug(message.toString());
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
                    case CHARACTER_GAINS_XP:
                        manageMessageCharacterGainsXP(message);
                        break;
                    case GAME_END:
                        manageMessageGameOver(message);
                        break;
                    case CHARACTER_POSITION_TO_USE_CAPACITY_RESPONSE:
                        manageMessagePositionToUseCapacityResponse(message);
                        break;
                    case CHARACTER_CAPACITY_AOE_RESPONSE:
                        manageMessageCapacityAOEResponse(message);
                        break;
                    case PLAYER_UPDATE:
                        MessagePlayerUpdate messagePlayerUpdate = (MessagePlayerUpdate) message;
                        clientContext.setPlayer(messagePlayerUpdate.getPlayer());
                        break;
                    case CAPACITY_FIREBALL:
                        manageCapacityFireball(message);
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
        if (!GraphicsManager.getInstance().update3DMovement()) {
            renderForMousePosition();
        }
        render3D();
        render2D();
    }

    private void renderForMousePosition() {

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);

        for (int i = 0; i < drawableObjectList.size(); i++) {
            if (drawableObjectList.get(i) instanceof Cube) {
                ((Cube) drawableObjectList.get(i)).renderForMousePosition();
            } else if (drawableObjectList.get(i) instanceof ActionButton) {
                ActionButton actionButton = (ActionButton) drawableObjectList.get(i);
                switch (actionButton.getActionButtonType()) {
                    case MOVE:
                        actionButton.renderForMousePosition(0.0f, 0.0f, 50.0f);
                        break;
                    case ATTACK:
                        actionButton.renderForMousePosition(0.0f, 0.0f, 60.0f);
                        break;
                    case CAPACITY:
                        actionButton.renderForMousePosition(0.0f, 0.0f, 70.0f);
                        break;
                    case END_TURN:
                        actionButton.renderForMousePosition(0.0f, 0.0f, 80.0f);
                        break;
                }
            }
        }
        ByteBuffer pixel = BufferUtils.createByteBuffer(3);
        GL11.glReadPixels(Mouse.getX(), Mouse.getY(), 1, 1, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, pixel);
        if (null != pixel) {
            int x = pixel.get(0);
            int y = pixel.get(1);
            int z = pixel.get(2);

            if (x >= 10 && y >= 10 && z >= 10) {
                x -= 10;
                y -= 10;
                z -= 10;
                mousePosition = new Position(x, y, z);
            } else {
                if (actionButtonMove != null) {
                    if (x == actionButtonMove.getR() && y == actionButtonMove.getG() && z == actionButtonMove.getB()) {
                        actionButtonMove.setHovered(true);
                    } else {
                        actionButtonMove.setHovered(false);
                    }
                }
                if (actionButtonAttack != null) {
                    if (x == actionButtonAttack.getR() && y == actionButtonAttack.getG() && z == actionButtonAttack.getB()) {
                        actionButtonAttack.setHovered(true);
                    } else {
                        actionButtonAttack.setHovered(false);
                    }
                }
                if (actionButtonCapacity != null) {
                    if (x == actionButtonCapacity.getR() && y == actionButtonCapacity.getG() && z == actionButtonCapacity.getB()) {
                        actionButtonCapacity.setHovered(true);
                    } else {
                        actionButtonCapacity.setHovered(false);
                    }
                }
                if (actionButtonEndTurn != null) {
                    if (x == actionButtonEndTurn.getR() && y == actionButtonEndTurn.getG() && z == actionButtonEndTurn.getB()) {
                        actionButtonEndTurn.setHovered(true);
                    } else {
                        actionButtonEndTurn.setHovered(false);
                    }
                }
                mousePosition = null;
            }
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    private void render3D() {
        GraphicsManager.getInstance().make3D();
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
        if (null != menuSelectCapacity && currentState.equals(BattleSceneState.CAPACITY_SELECT)) {
            menuSelectCapacity.render(Color.white);
        }
        if (currentState.equals(BattleSceneState.ACTION)) {
            menuSelectAction.render(Color.white);
        }
        if (currentState.equals(BattleSceneState.GAME_OVER)) {
            gameOverButton.draw();
        }
        frame.draw();
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
        if (battlefield.getDeploymentZones().get(playerNumber).contains(mousePosition)) {
            log.debug(" [-] PLACE CHARACTER AT {}", mousePosition.toString());
            Position position = new Position(mousePosition);
            position.plusY(1);
            if (positionIsEmpty(position)) {
                currentGameCharacter.setHeadingAngle(battlefield.getStartingPointsOfViewForPlayer(playerNumber));
                GameCharacterRepresentation gameCharacterRepresentation = new GameCharacterRepresentation(currentGameCharacter, position, playerNumber);
                currentGameCharacterRepresentation = gameCharacterRepresentation;
                characterRepresentationList.add(gameCharacterRepresentation);
                drawableObjectList.add(gameCharacterRepresentation);
                sortToDrawList();
                currentState = BattleSceneState.DEPLOY_HEADING_ANGLE;
            } else {
                log.error("[-] CAN'T PLACE CHARACTER AT {}", mousePosition.toString());
            }
        }
    }

    private boolean positionIsEmpty(Position position) {
        for (GameCharacterRepresentation gameCharacterRepresentation : characterRepresentationList) {
            if (position.equals(gameCharacterRepresentation.getPosition())) {
                return false;
            }
        }
        return !battlefield.positionIsOccupied(position);
    }

    public void deployCharacterHeadingAngle() {
        characterListToDeploy.remove(currentGameCharacter);
        battlefield.getDeploymentZones().get(playerNumber).remove(currentGameCharacter.getPosition().plusYNew(-1));
        currentGameCharacterRepresentation = null;
        updateTarget();
        if (characterListToDeploy.isEmpty()) {
            characterRenderLeft = null;
            sendDeploymentResult();
            currentGameCharacter = null;

            currentState = BattleSceneState.PENDING;
        } else {
            currentGameCharacter = characterListToDeploy.get(0);
            currentState = BattleSceneState.DEPLOY_POSITION;
            characterRenderLeft = new CharacterRender(0, 0, 300, 100, 2, currentGameCharacter);
        }
    }

    private void sendPositionToMoveRequest() {
        MessagePositionToMoveRequest messagePositionToMoveRequest = new MessagePositionToMoveRequest(clientContext.getTokenKey(), currentGameCharacter);
        postMessage(messagePositionToMoveRequest);
    }

    private void sendActionMove() {
        MessageCharacterActionMove messageCharacterActionMove = new MessageCharacterActionMove(clientContext.getTokenKey(), playerNumber, currentGameCharacter, mousePosition);
        postMessage(messageCharacterActionMove);
    }

    private void sendPositionToAttackRequest() {
        MessagePositionToAttackRequest messagePositionToAttackRequest = new MessagePositionToAttackRequest(clientContext.getTokenKey(), currentGameCharacter);
        postMessage(messagePositionToAttackRequest);
    }

    private void sendActionAttack() {
        MessageCharacterActionAttack messageCharacterActionAttack = new MessageCharacterActionAttack(clientContext.getTokenKey(), currentGameCharacter, mousePosition);
        postMessage(messageCharacterActionAttack);
    }

    private void sendPositionToUseCapacityRequest() {
        MessageCharacterPositionToUseCapacityRequest messageCharacterPositionToUseCapacityRequest = new MessageCharacterPositionToUseCapacityRequest(clientContext.getTokenKey(), currentGameCharacter, selectedMove);
        postMessage(messageCharacterPositionToUseCapacityRequest);
    }

    private void sendCapacityAOERequest() {
        MessageCapacityAOERequest messageCapacityAOERequest = new MessageCapacityAOERequest(clientContext.getTokenKey(), currentGameCharacter, selectedMove, mousePosition);
        postMessage(messageCapacityAOERequest);
    }

    private void sendActionCapacity() {
        MessageCharacterActionCapacity messageCharacterUsesCapacity = new MessageCharacterActionCapacity(clientContext.getTokenKey(), currentGameCharacter, mousePosition, selectedMove);
        postMessage(messageCharacterUsesCapacity);
    }

    private void sendEndTurn() {
        MessageCharacterEndTurn messageCharacterEndturn = new MessageCharacterEndTurn(clientContext.getTokenKey(), playerNumber, currentGameCharacterRepresentation.getCharacter());
        postMessage(messageCharacterEndturn);

    }

    private void sendDeploymentResult() {
        List<GameCharacter> gameCharacterList = new ArrayList<GameCharacter>();

        for (GameCharacterRepresentation gameCharacterRepresentation : characterRepresentationList) {
            gameCharacterList.add(gameCharacterRepresentation.getCharacter());
        }

        MessageDeploymentFinishedForPlayer messageDeploymentFinishedForPlayer = new MessageDeploymentFinishedForPlayer(clientContext.getTokenKey(), playerNumber, gameCharacterList);

        log.debug(" [-] DEPLOYMENT FINISHED FOR {}", messageDeploymentFinishedForPlayer.getKeyToken());
        postMessage(messageDeploymentFinishedForPlayer);

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

    private void updateTarget() {
        targetGameCharacterRepresentation = null;
        characterRenderRight = null;
        if (mousePosition != null) {
            for (GameCharacterRepresentation gameCharacterRepresentation : characterRepresentationList) {
                if (mousePosition.equals(gameCharacterRepresentation.getFootPosition())) {
                    targetGameCharacterRepresentation = gameCharacterRepresentation;
                    characterRenderRight = new CharacterRender(500, 0, 300, 100, 2, targetGameCharacterRepresentation.getCharacter());
                }
            }
        }
    }

    private void setHighlights() {
        for (Cube cube : battlefieldRepresentation.values()) {
            cube.setHighlight(HighlightColor.NONE);
        }
        if (currentState.equals(BattleSceneState.DEPLOY_POSITION)) {
            for (Position position : battlefield.getDeploymentZones().get(playerNumber)) {
                battlefieldRepresentation.get(position).setHighlight(HighlightColor.GREEN);
            }
        }
        if (currentState.equals(BattleSceneState.MOVE)) {
            for (Position position : possiblePositionsToMove) {
                battlefieldRepresentation.get(position).setHighlight(HighlightColor.GREEN);
            }
        }
        if (currentState.equals(BattleSceneState.ATTACK)) {
            for (Position position : possiblePositionsToAttack) {
                battlefieldRepresentation.get(position).setHighlight(HighlightColor.RED);
            }
        }
        if (currentState.equals(BattleSceneState.CAPACITY_PLACE)) {
            for (Position position : possiblePositionsToUseCapacity) {
                battlefieldRepresentation.get(position).setHighlight(HighlightColor.RED);
            }
        }
        if (currentState.equals(BattleSceneState.CAPACITY_USE)) {
            for (Position position : capacityAreaOfEffect) {
                battlefieldRepresentation.get(position).setHighlight(HighlightColor.GREEN);
            }
        }
        if (mousePosition != null) {
            battlefieldRepresentation.get(mousePosition).setHighlight(HighlightColor.BLUE);
            updateTarget();
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

    private boolean okClick() {
        boolean okClick;
        if (System.currentTimeMillis() - lastTimeMouseWasClicked < deltaMillis) {
            okClick = false;
        } else {
            lastTimeMouseWasClicked = System.currentTimeMillis();
            okClick = true;
        }
        return okClick;
    }

    public boolean engineIsBusy() {
        return engineIsBusy;
    }

    public void setEngineIsBusy(boolean engineIsBusy) {
        this.engineIsBusy = engineIsBusy;
    }
}
