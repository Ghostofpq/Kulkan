package com.ghostofpq.kulkan.entities.race;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;

public class Human extends Race {

    private static final long serialVersionUID = -1782671271505630783L;

    private final String HUMAN_NAME = "Human";
    private final String HUMAN_DESCRIPTION = "blablablabla HUMANZZZZ!!";

    private final int HUMAN_BASE_STRENGTH = 10;
    private final int HUMAN_BASE_ENDURANCE = 10;
    private final int HUMAN_BASE_INTELLIGENCE = 10;
    private final int HUMAN_BASE_WILL = 10;
    private final int HUMAN_BASE_AGILITY = 10;
    private final int HUMAN_BASE_MOVEMENT = 3;

    private final int HUMAN_LVL_STRENGTH = 2;
    private final int HUMAN_LVL_ENDURANCE = 2;
    private final int HUMAN_LVL_INTELLIGENCE = 2;
    private final int HUMAN_LVL_WILL = 2;
    private final int HUMAN_LVL_AGILITY = 2;
    private final int HUMAN_LVL_MOVEMENT = 0;

    public Human() {
        this.setName(HUMAN_NAME);
        this.setDescription(HUMAN_DESCRIPTION);
        this.setRace(RaceType.HUMAN);
        this.setBaseCaracteristics(new PrimaryCharacteristics(HUMAN_BASE_STRENGTH,
                HUMAN_BASE_ENDURANCE, HUMAN_BASE_INTELLIGENCE, HUMAN_BASE_WILL,
                HUMAN_BASE_AGILITY, HUMAN_BASE_MOVEMENT));
        this.setLevelUpCaracteristics(new PrimaryCharacteristics(HUMAN_LVL_STRENGTH,
                HUMAN_LVL_ENDURANCE, HUMAN_LVL_INTELLIGENCE, HUMAN_LVL_WILL,
                HUMAN_LVL_AGILITY, HUMAN_LVL_MOVEMENT));
    }
}
