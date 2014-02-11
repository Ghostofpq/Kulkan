package com.ghostofpq.kulkan.entities.clan;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.job.JobType;

public class Human extends Clan {

    private static final long serialVersionUID = -1782671271505630783L;
    private final String HUMAN_NAME = "Human";
    private final String HUMAN_DESCRIPTION = "blablablabla HUMANZZZZ!!";
    private final int HUMAN_LVL_STRENGTH = 2;
    private final int HUMAN_LVL_ENDURANCE = 2;
    private final int HUMAN_LVL_INTELLIGENCE = 2;
    private final int HUMAN_LVL_WILL = 2;
    private final int HUMAN_LVL_AGILITY = 2;
    private final int HUMAN_LVL_MOVEMENT = 0;

    public Human() {
        this.setName(HUMAN_NAME);
        this.setDescription(HUMAN_DESCRIPTION);
        this.setClan(ClanType.HUMAN);
        this.setBaseCharacteristics(new PrimaryCharacteristics(
                BASE_STRENGTH, BASE_ENDURANCE,
                BASE_INTELLIGENCE, BASE_WILL, BASE_AGILITY,
                BASE_MOVEMENT));
        this.setLevelUpCharacteristics(new PrimaryCharacteristics(HUMAN_LVL_STRENGTH,
                HUMAN_LVL_ENDURANCE, HUMAN_LVL_INTELLIGENCE, HUMAN_LVL_WILL,
                HUMAN_LVL_AGILITY, HUMAN_LVL_MOVEMENT));
        this.setBaseJob(JobType.WARRIOR);
    }
}
