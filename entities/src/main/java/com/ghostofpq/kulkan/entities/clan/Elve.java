package com.ghostofpq.kulkan.entities.clan;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;

public class Elve extends Clan {

    private static final long serialVersionUID = -6963245902545834866L;
    private final String ELVE_NAME = "Elve";
    private final String ELVE_DESCRIPTION = "blablablabla ELVEZZZZ!!";
    private final int ELVE_LVL_STRENGTH = 1;
    private final int ELVE_LVL_ENDURANCE = 1;
    private final int ELVE_LVL_INTELLIGENCE = 3;
    private final int ELVE_LVL_WILL = 2;
    private final int ELVE_LVL_AGILITY = 3;
    private final int ELVE_LVL_MOVEMENT = 0;

    public Elve() {
        this.setName(ELVE_NAME);
        this.setDescription(ELVE_DESCRIPTION);
        this.setRace(ClanType.ELVE);
        this.setBaseCaracteristics(new PrimaryCharacteristics(
                BASE_STRENGTH, BASE_ENDURANCE,
                BASE_INTELLIGENCE, BASE_WILL, BASE_AGILITY,
                BASE_MOVEMENT));
        this.setLevelUpCaracteristics(new PrimaryCharacteristics(
                ELVE_LVL_STRENGTH, ELVE_LVL_ENDURANCE, ELVE_LVL_INTELLIGENCE,
                ELVE_LVL_WILL, ELVE_LVL_AGILITY, ELVE_LVL_MOVEMENT));
    }
}
