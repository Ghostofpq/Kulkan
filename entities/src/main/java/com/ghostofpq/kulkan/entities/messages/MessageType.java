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
    CHARACTER_POSITION_TO_USE_CAPACITY_REQUEST,
    CHARACTER_POSITION_TO_USE_CAPACITY_RESPONSE,
    CHARACTER_CAPACITY_AOE_REQUEST,
    CHARACTER_CAPACITY_AOE_RESPONSE,
    CHARACTER_ACTION_CAPACITY_USE,
    CHARACTER_GAINS_XP,
    CHARACTER_GAINS_HP,
    CHARACTER_GAINS_MP,
    CHARACTER_POSITION_TO_ATTACK_REQUEST,
    CHARACTER_POSITION_TO_ATTACK_RESPONSE,
    CHARACTER_ACTION_END_TURN,

    CAPACITY_FIREBALL,

    AUTHENTICATION_SALT_RESPONSE,
    AUTHENTICATION_SALT_REQUEST,
    AUTHENTICATION_REQUEST,
    AUTHENTICATION_RESPONSE,
    CREATE_ACCOUNT, CREATE_ACCOUT,
    CREATE_ACCOUNT_RESPONSE,

    LOBBY_CLIENT,
    LOBBY_SERVER,
    LOBBY_PING,
    LOBBY_PONG,
    LOBBY_SUBSCRIBE,
    LOBBY_UNSUBCRIBE,

    MATCHMAKING_SUBSCRIBE,
    MATCHMAKING_UNSUBSCRIBE,
    MATCHMAKING_MATCH_FOUND,
    MATCHMAKING_MATCH_ABORT,
    MATCHMAKING_ACCEPT,
    MATCHMAKING_REFUSE,

    GET_ITEMS_BY_TYPE_REQUEST,
    GET_ITEMS_BY_TYPE_RESPONSE,

    CREATE_NEW_GAME_CHARACTER_REQUEST,
    DELETE_GAME_CHARACTER_FROM_STOCK_REQUEST,
    DELETE_GAME_CHARACTER_FROM_TEAM_REQUEST,
    PUT_GAME_CHARACTER_FROM_TEAM_TO_STOCK_REQUEST,
    PUT_GAME_CHARACTER_FROM_STOCK_TO_TEAM_REQUEST,
    CHARACTER_UNLOCK_CAPACITY,
    CHARACTER_CHANGE_JOB,
    BUY_ITEM_REQUEST,
    EQUIP_ITEM,
    UNEQUIP_ITEM,
    PLAYER_UPDATE,
    ERROR,

    PING,
    PONG,
    MAJONG;

}
