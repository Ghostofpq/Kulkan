package com.ghostofpq.kulkan.entities.clan;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.job.JobType;

public class Panther extends Clan {

    private static final long serialVersionUID = -54460941361229277L;
    private final String PANTHER_NAME = "PANTHER";
    private final String PANTHER_DESCRIPTION = "blablablabla PANTHERFFFF!!";
    private final int PANTHER_LVL_STRENGTH = 2;
    private final int PANTHER_LVL_ENDURANCE = 3;
    private final int PANTHER_LVL_INTELLIGENCE = 1;
    private final int PANTHER_LVL_WILL = 1;
    private final int PANTHER_LVL_AGILITY = 3;
    private final int PANTHER_LVL_MOVEMENT = 0;

    public Panther() {
        this.setName(PANTHER_NAME);
        this.setDescription(PANTHER_DESCRIPTION);
        this.setClan(ClanType.PANTHER);
        this.setBaseCharacteristics(new PrimaryCharacteristics(
                BASE_STRENGTH, BASE_ENDURANCE,
                BASE_INTELLIGENCE, BASE_WILL, BASE_AGILITY,
                BASE_MOVEMENT));
        this.setLevelUpCharacteristics(new PrimaryCharacteristics(
                PANTHER_LVL_STRENGTH, PANTHER_LVL_ENDURANCE,
                PANTHER_LVL_INTELLIGENCE, PANTHER_LVL_WILL, PANTHER_LVL_AGILITY,
                PANTHER_LVL_MOVEMENT));
        this.setBaseJob(JobType.WARRIOR);
    }
}
