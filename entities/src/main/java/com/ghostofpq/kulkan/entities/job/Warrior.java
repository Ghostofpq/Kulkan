package com.ghostofpq.kulkan.entities.job;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.job.capacity.Amelioration;
import com.ghostofpq.kulkan.entities.job.capacity.Capacity;

import java.util.ArrayList;
import java.util.List;

public class Warrior extends Job {

    private static final long serialVersionUID = 2620023619865757113L;

    private final static String WARRIOR_NAME = "Warrior";
    private final static String WARRIOR_DESC = "Warriors are stronk";

    public Warrior() {
        super(WARRIOR_NAME, WARRIOR_DESC);
        this.prepareSkillTree();
    }

    // Warrior Skills
    private void prepareSkillTree() {
        List<Capacity> skillTree = new ArrayList<Capacity>();

        PrimaryCharacteristics strength1C = new PrimaryCharacteristics(5, 0, 0,
                0, 0, 0);
        Amelioration strength1 = new Amelioration("strength1",
                "strength1 desc", strength1C, 50);
        skillTree.add(strength1);

        PrimaryCharacteristics strength2C = new PrimaryCharacteristics(10, 0,
                0, 0, 0, 0);
        Amelioration strength2 = new Amelioration("strength2",
                "strength2 desc", strength2C, 150);
        skillTree.add(strength2);

        PrimaryCharacteristics strength3C = new PrimaryCharacteristics(20, 0,
                0, 0, 0, 0);
        Amelioration strength3 = new Amelioration("strength3",
                "strength3 desc", strength3C, 500);
        skillTree.add(strength3);

        PrimaryCharacteristics endurance1C = new PrimaryCharacteristics(0, 5,
                0, 0, 0, 0);
        Amelioration endurance1 = new Amelioration("endurance1",
                "endurance1 desc", endurance1C, 50);
        skillTree.add(endurance1);

        PrimaryCharacteristics endurance2C = new PrimaryCharacteristics(0, 10,
                0, 0, 0, 0);
        Amelioration endurance2 = new Amelioration("endurance2",
                "endurance2 desc", endurance2C, 150);
        skillTree.add(endurance2);
        PrimaryCharacteristics endurance3C = new PrimaryCharacteristics(0, 20,
                0, 0, 0, 0);
        Amelioration endurance3 = new Amelioration("endurance3",
                "endurance3 desc", endurance3C, 500);
        skillTree.add(endurance3);

        strength2.addPrerequisites(strength1);
        strength3.addPrerequisites(strength2);
        endurance2.addPrerequisites(endurance1);
        endurance3.addPrerequisites(endurance2);

        this.setSkillTree(skillTree);
    }
}
