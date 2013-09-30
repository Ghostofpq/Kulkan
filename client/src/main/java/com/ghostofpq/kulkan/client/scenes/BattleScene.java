package com.ghostofpq.kulkan.client.scenes;

import com.ghostofpq.kulkan.client.graphics.CharacterRender;
import com.ghostofpq.kulkan.client.graphics.Cube;
import com.ghostofpq.kulkan.client.graphics.DrawableObject;
import com.ghostofpq.kulkan.client.graphics.GameCharacterRepresentation;
import com.ghostofpq.kulkan.commons.PointOfView;
import com.ghostofpq.kulkan.commons.Position;
import com.ghostofpq.kulkan.commons.PositionAbsolute;
import com.ghostofpq.kulkan.entities.battlefield.Battlefield;
import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.character.Player;
import com.rabbitmq.client.Channel;

import java.util.List;
import java.util.Map;

public class BattleScene implements Scene {

    private static volatile BattleScene instance = null;
    private final String GAME_SERVER_QUEUE_NAME_BASE = "/server/game";
    private BattleSceneState currentState;
    private String gameId;
    private Channel channelGameOut;

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

    private enum BattleSceneState {
        DEPLOY, PENDING, ACTION, MOVE, ATTACK, END_TURN
    }
}
