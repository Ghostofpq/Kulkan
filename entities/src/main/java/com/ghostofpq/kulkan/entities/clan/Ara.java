package com.ghostofpq.kulkan.entities.clan;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.job.JobType;

public class Ara extends Clan {

    private static final long serialVersionUID = -54460941361229277L;
    private final String ARA_NAME = "ARA";
    private final String ARA_DESCRIPTION = "blablablabla ARAFFFF!!";
    private final int ARA_LVL_STRENGTH = 1;
    private final int ARA_LVL_ENDURANCE = 1;
    private final int ARA_LVL_INTELLIGENCE = 3;
    private final int ARA_LVL_WILL = 3;
    private final int ARA_LVL_AGILITY = 3;
    private final int ARA_LVL_MOVEMENT = 0;

    public Ara() {
        this.setName(ARA_NAME);
        this.setDescription(ARA_DESCRIPTION);
        this.setClan(ClanType.ARA);
        this.setBaseCharacteristics(new PrimaryCharacteristics(
                BASE_STRENGTH, BASE_ENDURANCE,
                BASE_INTELLIGENCE, BASE_WILL, BASE_AGILITY,
                BASE_MOVEMENT));
        this.setLevelUpCharacteristics(new PrimaryCharacteristics(
                ARA_LVL_STRENGTH, ARA_LVL_ENDURANCE,
                ARA_LVL_INTELLIGENCE, ARA_LVL_WILL, ARA_LVL_AGILITY,
                ARA_LVL_MOVEMENT));
        this.setBaseJob(JobType.MAGE);
    }
}
