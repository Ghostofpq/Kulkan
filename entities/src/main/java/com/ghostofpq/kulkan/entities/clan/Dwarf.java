package com.ghostofpq.kulkan.entities.clan;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;

public class Dwarf extends Clan {

    private static final long serialVersionUID = -54460941361229277L;
    private final String DWARF_NAME = "Dwarf";
    private final String DWARF_DESCRIPTION = "blablablabla DWARFFFFF!!";
    private final int DWARF_LVL_STRENGTH = 3;
    private final int DWARF_LVL_ENDURANCE = 3;
    private final int DWARF_LVL_INTELLIGENCE = 1;
    private final int DWARF_LVL_WILL = 2;
    private final int DWARF_LVL_AGILITY = 1;
    private final int DWARF_LVL_MOVEMENT = 0;

    public Dwarf() {
        this.setName(DWARF_NAME);
        this.setDescription(DWARF_DESCRIPTION);
        this.setRace(ClanType.DWARF);
        this.setBaseCaracteristics(new PrimaryCharacteristics(
                BASE_STRENGTH, BASE_ENDURANCE,
                BASE_INTELLIGENCE, BASE_WILL, BASE_AGILITY,
                BASE_MOVEMENT));
        this.setLevelUpCaracteristics(new PrimaryCharacteristics(
                DWARF_LVL_STRENGTH, DWARF_LVL_ENDURANCE,
                DWARF_LVL_INTELLIGENCE, DWARF_LVL_WILL, DWARF_LVL_AGILITY,
                DWARF_LVL_MOVEMENT));
    }
}
