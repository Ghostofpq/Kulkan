package com.ghostofpq.kulkan.entities.messages.game;

import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

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

    @Override
    public String toString() {
        return new StringBuffer().
                append("Message Type :").append(type).append(System.getProperty("line.separator"))
                .append("AttackingChar :").append(attackingChar.getName()).append(System.getProperty("line.separator"))
                .append("TargetedChar :").append(targetedChar.getName()).append(System.getProperty("line.separator"))
                .append("Damages :").append(damages).append(System.getProperty("line.separator"))
                .append("Crits :").append(crits).append(System.getProperty("line.separator"))
                .append("Hits :").append(hits)
                .toString();
    }
}