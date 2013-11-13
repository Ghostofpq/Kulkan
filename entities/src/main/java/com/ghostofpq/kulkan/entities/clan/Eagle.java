package com.ghostofpq.kulkan.entities.clan;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;

public class Eagle extends Clan {

    private static final long serialVersionUID = -54460941361229277L;

    private final String EAGLE_NAME = "EAGLE";
    private final String EAGLE_DESCRIPTION = "blablablabla EAGLEFFFF!!";

    private final int EAGLE_LVL_STRENGTH = 0;
    private final int EAGLE_LVL_ENDURANCE = 0;
    private final int EAGLE_LVL_INTELLIGENCE = 0;
    private final int EAGLE_LVL_WILL = 0;
    private final int EAGLE_LVL_AGILITY = 0;
    private final int EAGLE_LVL_MOVEMENT = 0;

    public Eagle() {
        this.setName(EAGLE_NAME);
        this.setDescription(EAGLE_DESCRIPTION);
        this.setRace(ClanType.EAGLE);
        this.setBaseCaracteristics(new PrimaryCharacteristics(
                BASE_STRENGTH, BASE_ENDURANCE,
                BASE_INTELLIGENCE, BASE_WILL, BASE_AGILITY,
                BASE_MOVEMENT));
        this.setLevelUpCaracteristics(new PrimaryCharacteristics(
                EAGLE_LVL_STRENGTH, EAGLE_LVL_ENDURANCE,
                EAGLE_LVL_INTELLIGENCE, EAGLE_LVL_WILL, EAGLE_LVL_AGILITY,
                EAGLE_LVL_MOVEMENT));
    }
}
