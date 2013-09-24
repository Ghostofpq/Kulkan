package com.ghostofpq.kulkan.entities.race;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;

public class Elve extends Race {

    private static final long serialVersionUID = -6963245902545834866L;

    private final String ELVE_NAME = "Elve";
    private final String ELVE_DESCRIPTION = "blablablabla ELVEZZZZ!!";

    private final int ELVE_BASE_STRENGTH = 8;
    private final int ELVE_BASE_ENDURANCE = 8;
    private final int ELVE_BASE_INTELLIGENCE = 12;
    private final int ELVE_BASE_WILL = 11;
    private final int ELVE_BASE_AGILITY = 11;
    private final int ELVE_BASE_MOVEMENT = 4;

    private final int ELVE_LVL_STRENGTH = 1;
    private final int ELVE_LVL_ENDURANCE = 1;
    private final int ELVE_LVL_INTELLIGENCE = 3;
    private final int ELVE_LVL_WILL = 2;
    private final int ELVE_LVL_AGILITY = 3;
    private final int ELVE_LVL_MOVEMENT = 0;

    public Elve() {
        this.setName(ELVE_NAME);
        this.setDescription(ELVE_DESCRIPTION);
        this.setRace(RaceType.ELVE);
        this.setBaseCaracteristics(new PrimaryCharacteristics(
                ELVE_BASE_STRENGTH, ELVE_BASE_ENDURANCE,
                ELVE_BASE_INTELLIGENCE, ELVE_BASE_WILL, ELVE_BASE_AGILITY,
                ELVE_BASE_MOVEMENT));
        this.setLevelUpCaracteristics(new PrimaryCharacteristics(
                ELVE_LVL_STRENGTH, ELVE_LVL_ENDURANCE, ELVE_LVL_INTELLIGENCE,
                ELVE_LVL_WILL, ELVE_LVL_AGILITY, ELVE_LVL_MOVEMENT));
    }
}
