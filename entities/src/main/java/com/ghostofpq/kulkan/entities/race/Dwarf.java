package com.ghostofpq.kulkan.entities.race;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;

public class Dwarf extends Race {

    private static final long serialVersionUID = -54460941361229277L;

    private final String DWARF_NAME = "Dwarf";
    private final String DWARF_DESCRIPTION = "blablablabla DWARFFFFF!!";

    private final int DWARF_BASE_STRENGTH = 11;
    private final int DWARF_BASE_ENDURANCE = 13;
    private final int DWARF_BASE_INTELLIGENCE = 8;
    private final int DWARF_BASE_WILL = 9;
    private final int DWARF_BASE_AGILITY = 9;
    private final int DWARF_BASE_MOVEMENT = 2;

    private final int DWARF_LVL_STRENGTH = 3;
    private final int DWARF_LVL_ENDURANCE = 3;
    private final int DWARF_LVL_INTELLIGENCE = 1;
    private final int DWARF_LVL_WILL = 2;
    private final int DWARF_LVL_AGILITY = 1;
    private final int DWARF_LVL_MOVEMENT = 0;

    public Dwarf() {
        this.setName(DWARF_NAME);
        this.setDescription(DWARF_DESCRIPTION);
        this.setRace(RaceType.DWARF);
        this.setBaseCaracteristics(new PrimaryCharacteristics(
                DWARF_BASE_STRENGTH, DWARF_BASE_ENDURANCE,
                DWARF_BASE_INTELLIGENCE, DWARF_BASE_WILL, DWARF_BASE_AGILITY,
                DWARF_BASE_MOVEMENT));
        this.setLevelUpCaracteristics(new PrimaryCharacteristics(
                DWARF_LVL_STRENGTH, DWARF_LVL_ENDURANCE,
                DWARF_LVL_INTELLIGENCE, DWARF_LVL_WILL, DWARF_LVL_AGILITY,
                DWARF_LVL_MOVEMENT));
    }
}
