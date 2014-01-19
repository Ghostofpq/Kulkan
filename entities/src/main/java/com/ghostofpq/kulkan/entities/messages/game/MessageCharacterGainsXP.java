package com.ghostofpq.kulkan.entities.messages.game;

import com.ghostofpq.kulkan.entities.character.GameCharacter;
import com.ghostofpq.kulkan.entities.messages.Message;
import com.ghostofpq.kulkan.entities.messages.MessageType;

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

    @Override
    public String toString() {
        return new StringBuffer().
                append("Message Type :").append(type).append(System.getProperty("line.separator"))
                .append("Character :").append(character.getName()).append(System.getProperty("line.separator"))
                .append("ExperiencePoints :").append(experiencePoints).append(System.getProperty("line.separator"))
                .append("JobPoints :").append(jobPoints)
                .toString();
    }
}
