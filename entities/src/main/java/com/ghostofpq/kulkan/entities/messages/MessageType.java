package com.ghostofpq.kulkan.entities.messages;

public enum MessageType {
    GAME_START,
    GAME_END,

    MAIN_PLAYER,
    OTHER_PLAYER,

    START_DEPLOYMENT,
    PLACE_CHARACTER,
    FINISH_DEPLOYMENT,

    ALL_POSITIONS,

    CHARACTER_ACTION_MOVE,
    CHARACTER_ACTION_ATTACK,
    CHARACTER_ACTION_END_TURN;
}
