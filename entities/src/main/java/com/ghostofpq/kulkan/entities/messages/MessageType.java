package com.ghostofpq.kulkan.entities.messages;

import java.io.Serializable;

public enum MessageType implements Serializable {
    GAME_START,
    GAME_END,

    MAIN_PLAYER,
    OTHER_PLAYER,

    START_DEPLOYMENT,
    OTHER_PLAYER_DEPLOYMENT,
    FINISH_DEPLOYMENT,

    ALL_CHARACTERS,

    CHARACTER_TO_PLAY,
    CHARACTER_ACTION_MOVE,
    CHARACTER_MOVES,
    CHARACTER_POSITION_TO_MOVE_REQUEST,
    CHARACTER_POSITION_TO_MOVE_RESPONSE,
    CHARACTER_ACTION_ATTACK,
    CHARACTER_ATTACKS,
    CHARACTER_GAINS_XP,
    CHARACTER_POSITION_TO_ATTACK_REQUEST,
    CHARACTER_POSITION_TO_ATTACK_RESPONSE,
    CHARACTER_ACTION_END_TURN,

    AUTHENTICATION_SALT_RESPONSE,
    AUTHENTICATION_SALT_REQUEST,
    AUTHENTICATION_REQUEST,
    AUTHENTICATION_RESPONSE,
    CREATE_ACCOUT,
    CREATE_ACCOUT_RESPONSE,

    LOBBY_CLIENT,
    LOBBY_SERVER,
    LOBBY_PING,
    LOBBY_PONG,

    MATCHMAKING_SUBSCRIBE,
    MATCHMAKING_UNSUBSCRIBE,
    MATCHMAKING_MATCH_FOUND,
    MATCHMAKING_MATCH_ABORT,
    MATCHMAKING_ACCEPT,
    MATCHMAKING_REFUSE,

    CREATE_NEW_GAME_CHARACTER_REQUEST,
    PLAYER_UPDATE;

}
