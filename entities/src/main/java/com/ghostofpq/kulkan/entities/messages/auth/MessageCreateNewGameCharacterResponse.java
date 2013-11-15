package com.ghostofpq.kulkan.entities.messages.auth;

import com.ghostofpq.kulkan.entities.character.Player;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

public class MessageCreateNewGameCharacterResponse extends Message {
    private Player player;
    private MessageErrorCode messageErrorCode;

    public MessageCreateNewGameCharacterResponse(Player player, MessageErrorCode messageErrorCode) {
        type = MessageType.CREATE_NEW_GAME_CHARACTER_RESPONSE;
        this.player = player;
        this.messageErrorCode = messageErrorCode;
    }

    public Player getPlayer() {
        return player;
    }

    public MessageErrorCode getMessageErrorCode() {
        return messageErrorCode;
    }
}

