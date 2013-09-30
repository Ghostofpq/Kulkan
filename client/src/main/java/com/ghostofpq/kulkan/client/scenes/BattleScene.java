package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.Client;
import com.ghostofpq.kulkan.client.graphics.*;
import com.ghostofpq.kulkan.client.utils.GraphicsManager;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<Position, Cube> todraw;
    private List<DrawableObject> toDrawList;
    private List<Position> positionsToDraw;
    private List<Position> positionsToSelect;
    private Player currentPlayer;
    private List<Player> players;
    private List<GameCharacterRepresentation> gameCharacterRepresentations;
    private GameCharacter currentGameCharacter;
    private GameCharacterRepresentation currentGameCharacterRepresentation;
    private GameCharacterRepresentation targetGameCharacterRepresentation;

    private MenuSelectAction menuSelectAction;

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

        todraw = toDrawableList(battlefield);
        toDrawList = new ArrayList<DrawableObject>();
        toDrawList.addAll(todraw.values());
    }

    @Override
    public void update(long deltaTime) {
    }

    @Override
    public void render() {
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

    private enum BattleSceneState {
        DEPLOY, PENDING, ACTION, MOVE, ATTACK, END_TURN
    }

    private void render3D() {
        GraphicsManager.getInstance().make3D();
        for (int i = 0; i < toDrawList.size(); i++) {
            toDrawList.get(i).draw();
            // if (toDrawList.get(i).getPosition().equals(cursor)) {
            //    Cube c = (Cube) toDrawList.get(i);
            //   log.debug("Cube E:{} W:{} N:{} S:{} Z:{}", c.getFacetEast().isVisible(), c.getFacetWest().isVisible(), c.getFacetNorth().isVisible(), c.getFacetSouth().isVisible(), c.getFacetZenith().isVisible());
            //}
        }
        /**
         for (Position position : positionsToDraw) {
         todraw.get(position).draw();
         }  */

    }

    private void render2D() {
        GraphicsManager.getInstance().make2D();
        characterRenderLeft.render(Color.white);
        if (null != targetGameCharacterRepresentation && !targetGameCharacterRepresentation.equals(currentGameCharacterRepresentation)) {
            characterRenderRight.render(Color.white);
        }
        if (currentState.equals(BattleSceneState.ACTION)) {
            menuSelectAction.render(Color.white);
        }
        // hud.render();
    }

    private Map<Position, Cube> toDrawableList(Battlefield battlefield) {
        Map<Position, Cube> toDraw = new HashMap<Position, Cube>();
        for (Position position : battlefield.getBattlefieldElementMap().keySet()) {
            BattlefieldElement element = battlefield.getBattlefieldElementMap().get(position);
            if (element != null) {
                if (element.getType().equals(BattlefieldElement.BattlefieldElementType.BLOC)) {
                    Cube cube = new Cube(position);
                    toDraw.put(position, cube);
                }
            }
        }
        for (Position position : toDraw.keySet()) {
            Cube cube = toDraw.get(position);
            Position positionAbove = new Position(position.getX(), position.getY() + 1, position.getZ());
            Position positionLeft = new Position(position.getX() - 1, position.getY(), position.getZ());
            Position positionRight = new Position(position.getX() + 1, position.getY(), position.getZ());
            Position positionUp = new Position(position.getX(), position.getY(), position.getZ() - 1);
            Position positionDown = new Position(position.getX(), position.getY(), position.getZ() + 1);

            if (toDraw.keySet().contains(positionAbove)) {
                cube.setSelectable(false);
                if (toDraw.keySet().contains(positionLeft) && toDraw.keySet().contains(positionRight) && toDraw.keySet().contains(positionUp) && toDraw.keySet().contains(positionDown)) {
                    cube.setVisible(false);
                }
            }

            if (toDraw.keySet().contains(positionLeft)) {
                cube.getFacetWest().setVisible(false);
            }
            if (toDraw.keySet().contains(positionRight)) {
                cube.getFacetEast().setVisible(false);
            }
            if (toDraw.keySet().contains(positionUp)) {
                cube.getFacetNorth().setVisible(false);
            }
            if (toDraw.keySet().contains(positionDown)) {
                cube.getFacetSouth().setVisible(false);
            }
            if (toDraw.keySet().contains(positionAbove)) {
                cube.getFacetZenith().setVisible(false);
            }
        }

        return toDraw;
    }
}
