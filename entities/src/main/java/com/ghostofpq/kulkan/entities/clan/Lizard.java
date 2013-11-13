package com.ghostofpq.kulkan.entities.clan;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;

public class Lizard extends Clan {

    private static final long serialVersionUID = -54460941361229277L;
    private final String LIZARD_NAME = "LIZARD";
    private final String LIZARD_DESCRIPTION = "blablablabla LIZARDFFFF!!";
    private final int LIZARD_LVL_STRENGTH = 1;
    private final int LIZARD_LVL_ENDURANCE = 3;
    private final int LIZARD_LVL_INTELLIGENCE = 3;
    private final int LIZARD_LVL_WILL = 1;
    private final int LIZARD_LVL_AGILITY = 2;
    private final int LIZARD_LVL_MOVEMENT = 0;

    public Lizard() {
        this.setName(LIZARD_NAME);
        this.setDescription(LIZARD_DESCRIPTION);
        this.setRace(ClanType.LIZARD);
        this.setBaseCaracteristics(new PrimaryCharacteristics(
                BASE_STRENGTH, BASE_ENDURANCE,
                BASE_INTELLIGENCE, BASE_WILL, BASE_AGILITY,
                BASE_MOVEMENT));
        this.setLevelUpCaracteristics(new PrimaryCharacteristics(
                LIZARD_LVL_STRENGTH, LIZARD_LVL_ENDURANCE,
                LIZARD_LVL_INTELLIGENCE, LIZARD_LVL_WILL, LIZARD_LVL_AGILITY,
                LIZARD_LVL_MOVEMENT));
    }
}
