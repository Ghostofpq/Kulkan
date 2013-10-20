package com.ghostofpq.kulkan.entities.messages;

import com.ghostofpq.kulkan.entities.character.GameCharacter;

import java.io.Serializable;

public class MessageCharacterGainsXP extends Message implements Serializable {
    private GameCharacter character;
    private int experiencePoints;
    private int jobPoints;

    public MessageCharacterGainsXP(GameCharacter character, int experiencePoints, int jobPoints) {
        type = MessageType.CHARACTER_GAINS_XP;
        this.character = character;
        this.experiencePoints = experiencePoints;
        this.jobPoints = jobPoints;
    }

    public GameCharacter getCharacter() {
        return character;
    }

    public int getExperiencePoints() {
        return experiencePoints;
    }

    public int getJobPoints() {
        return jobPoints;
    }
}
