package com.ghostofpq.kulkan.entities.messages;

import com.ghostofpq.kulkan.entities.character.GameCharacter;

import java.io.Serializable;

public class MessageCharacterAttacks extends Message implements Serializable {
    GameCharacter attackingChar;
    GameCharacter targetedChar;
    int damages;
    boolean crits;
    boolean hits;


    public MessageCharacterAttacks(GameCharacter attackingChar, GameCharacter targetedChar, boolean hits, int damages, boolean crits) {
        type = MessageType.CHARACTER_ATTACKS;
        this.attackingChar = attackingChar;
        this.targetedChar = targetedChar;
        this.damages = damages;
        this.crits = crits;
        this.hits = hits;
    }

    public GameCharacter getAttackingChar() {
        return attackingChar;
    }

    public GameCharacter getTargetedChar() {
        return targetedChar;
    }

    public int getDamages() {
        return damages;
    }

    public boolean hits() {
        return hits;
    }

    public boolean crits() {
        return crits;
    }
}