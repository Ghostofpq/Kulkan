package com.ghostofpq.kulkan.entities.battlefield;

public enum BattleSceneState {
    DEPLOY_POSITION,
    DEPLOY_HEADING_ANGLE,

    PENDING,

    ACTION,
    MOVE,
    ATTACK,
    CAPACITY,
    CAPACITY_SELECT,
    CAPACITY_PLACE,
    END_TURN,

    WAITING_SERVER_RESPONSE_MOVE,
    WAITING_SERVER_RESPONSE_ATTACK,

    GAME_OVER
}
