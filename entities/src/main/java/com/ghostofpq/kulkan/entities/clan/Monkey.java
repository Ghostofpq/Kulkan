package com.ghostofpq.kulkan.entities.clan;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.job.JobType;

public class Monkey extends Clan {

    private static final long serialVersionUID = -54460941361229277L;
    private final String MONKEY_NAME = "MONKEY";
    private final String MONKEY_DESCRIPTION = "blablablabla MONKEYFFFF!!";
    private final int MONKEY_LVL_STRENGTH = 2;
    private final int MONKEY_LVL_ENDURANCE = 2;
    private final int MONKEY_LVL_INTELLIGENCE = 2;
    private final int MONKEY_LVL_WILL = 2;
    private final int MONKEY_LVL_AGILITY = 2;
    private final int MONKEY_LVL_MOVEMENT = 0;

    public Monkey() {
        this.setName(MONKEY_NAME);
        this.setDescription(MONKEY_DESCRIPTION);
        this.setClan(ClanType.MONKEY);
        this.setBaseCharacteristics(new PrimaryCharacteristics(
                BASE_STRENGTH, BASE_ENDURANCE,
                BASE_INTELLIGENCE, BASE_WILL, BASE_AGILITY,
                BASE_MOVEMENT));
        this.setLevelUpCharacteristics(new PrimaryCharacteristics(
                MONKEY_LVL_STRENGTH, MONKEY_LVL_ENDURANCE,
                MONKEY_LVL_INTELLIGENCE, MONKEY_LVL_WILL, MONKEY_LVL_AGILITY,
                MONKEY_LVL_MOVEMENT));
        this.setBaseJob(JobType.WARRIOR);
    }
}
