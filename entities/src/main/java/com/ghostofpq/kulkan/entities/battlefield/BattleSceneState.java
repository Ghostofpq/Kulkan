package com.ghostofpq.kulkan.entities.battlefield;

public enum BattleSceneState {
    DEPLOY_POSITION,
    DEPLOY_HEADING_ANGLE,

    PENDING,

    ACTION,
    MOVE,
    ATTACK,
    CAPACITY_SELECT,
    CAPACITY_PLACE,
    CAPACITY_USE,
    END_TURN,

    WAITING_SERVER_RESPONSE_MOVE,
    WAITING_SERVER_RESPONSE_ATTACK,
    WAITING_SERVER_RESPONSE_CAPACITY,
    WAITING_SERVER_RESPONSE_CAPACITY_AOE,

    GAME_OVER
}
