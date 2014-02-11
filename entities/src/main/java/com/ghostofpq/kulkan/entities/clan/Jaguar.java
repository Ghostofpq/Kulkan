package com.ghostofpq.kulkan.entities.clan;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.job.JobType;

public class Jaguar extends Clan {

    private static final long serialVersionUID = -54460941361229277L;
    private final String JAGUAR_NAME = "JAGUAR";
    private final String JAGUAR_DESCRIPTION = "blablablabla JAGUARFFFF!!";
    private final int JAGUAR_LVL_STRENGTH = 3;
    private final int JAGUAR_LVL_ENDURANCE = 2;
    private final int JAGUAR_LVL_INTELLIGENCE = 1;
    private final int JAGUAR_LVL_WILL = 1;
    private final int JAGUAR_LVL_AGILITY = 3;
    private final int JAGUAR_LVL_MOVEMENT = 0;

    public Jaguar() {
        this.setName(JAGUAR_NAME);
        this.setDescription(JAGUAR_DESCRIPTION);
        this.setClan(ClanType.JAGUAR);
        this.setBaseCharacteristics(new PrimaryCharacteristics(
                BASE_STRENGTH, BASE_ENDURANCE,
                BASE_INTELLIGENCE, BASE_WILL, BASE_AGILITY,
                BASE_MOVEMENT));
        this.setLevelUpCharacteristics(new PrimaryCharacteristics(
                JAGUAR_LVL_STRENGTH, JAGUAR_LVL_ENDURANCE,
                JAGUAR_LVL_INTELLIGENCE, JAGUAR_LVL_WILL, JAGUAR_LVL_AGILITY,
                JAGUAR_LVL_MOVEMENT));
        this.setBaseJob(JobType.WARRIOR);
    }
}
