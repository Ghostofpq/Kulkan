package com.ghostofpq.kulkan.entities.clan;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.job.JobType;

public class Gorilla extends Clan {

    private static final long serialVersionUID = -54460941361229277L;

    private final String GORILLA_NAME = "GORILLA";
    private final String GORILLA_DESCRIPTION = "blablablabla GORILLAFFFF!!";

    private final int GORILLA_LVL_STRENGTH = 3;
    private final int GORILLA_LVL_ENDURANCE = 3;
    private final int GORILLA_LVL_INTELLIGENCE = 1;
    private final int GORILLA_LVL_WILL = 1;
    private final int GORILLA_LVL_AGILITY = 2;
    private final int GORILLA_LVL_MOVEMENT = 0;

    public Gorilla() {
        this.setName(GORILLA_NAME);
        this.setDescription(GORILLA_DESCRIPTION);
        this.setClan(ClanType.GORILLA);
        this.setBaseCharacteristics(new PrimaryCharacteristics(
                BASE_STRENGTH, BASE_ENDURANCE,
                BASE_INTELLIGENCE, BASE_WILL, BASE_AGILITY,
                BASE_MOVEMENT));
        this.setLevelUpCharacteristics(new PrimaryCharacteristics(
                GORILLA_LVL_STRENGTH, GORILLA_LVL_ENDURANCE,
                GORILLA_LVL_INTELLIGENCE, GORILLA_LVL_WILL, GORILLA_LVL_AGILITY,
                GORILLA_LVL_MOVEMENT));
        this.setBaseJob(JobType.WARRIOR);
    }
}
