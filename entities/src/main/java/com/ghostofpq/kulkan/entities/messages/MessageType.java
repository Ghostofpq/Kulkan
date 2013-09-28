package com.ghostofpq.kulkan.entities.messages;

import java.io.Serializable;

public enum MessageType implements Serializable {
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
    CHARACTER_ACTION_END_TURN,

    AUTHENTICATION_REQUEST,
    AUTHENTICATION_RESPONSE,

    LOBBY_CLIENT,
    LOBBY_SERVER;
}
