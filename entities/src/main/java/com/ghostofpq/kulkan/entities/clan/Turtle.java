package com.ghostofpq.kulkan.entities.clan;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;

public class Turtle extends Clan {

    private static final long serialVersionUID = -54460941361229277L;
    private final String TURTLE_NAME = "TURTLE";
    private final String TURTLE_DESCRIPTION = "blablablabla TURTLEFFFF!!";
    private final int TURTLE_LVL_STRENGTH = 1;
    private final int TURTLE_LVL_ENDURANCE = 3;
    private final int TURTLE_LVL_INTELLIGENCE = 3;
    private final int TURTLE_LVL_WILL = 2;
    private final int TURTLE_LVL_AGILITY = 1;
    private final int TURTLE_LVL_MOVEMENT = 0;

    public Turtle() {
        this.setName(TURTLE_NAME);
        this.setDescription(TURTLE_DESCRIPTION);
        this.setRace(ClanType.TURTLE);
        this.setBaseCaracteristics(new PrimaryCharacteristics(
                BASE_STRENGTH, BASE_ENDURANCE,
                BASE_INTELLIGENCE, BASE_WILL, BASE_AGILITY,
                BASE_MOVEMENT));
        this.setLevelUpCaracteristics(new PrimaryCharacteristics(
                TURTLE_LVL_STRENGTH, TURTLE_LVL_ENDURANCE,
                TURTLE_LVL_INTELLIGENCE, TURTLE_LVL_WILL, TURTLE_LVL_AGILITY,
                TURTLE_LVL_MOVEMENT));
    }
}
