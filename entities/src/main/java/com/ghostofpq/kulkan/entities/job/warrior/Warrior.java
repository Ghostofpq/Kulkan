package com.ghostofpq.kulkan.entities.job.warrior;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.characteristics.SecondaryCharacteristics;
import com.ghostofpq.kulkan.entities.job.Job;
import com.ghostofpq.kulkan.entities.job.JobType;
import com.ghostofpq.kulkan.entities.job.capacity.*;
import com.ghostofpq.kulkan.entities.utils.Range;
import com.ghostofpq.kulkan.entities.utils.RangeType;

import java.util.ArrayList;
import java.util.List;

public class Warrior extends Job {
    private final static String WARRIOR_NAME = "Warrior";
    private final static String WARRIOR_DESC = "Warriors are stronk";

    public Warrior() {
        super(WARRIOR_NAME, WARRIOR_DESC);
        this.jobType = JobType.WARRIOR;
        this.prepareSkillTree();
    }

    // Warrior Skills
    private void prepareSkillTree() {
        List<Capacity> skillTree = new ArrayList<Capacity>();

        Move dash = new Move("Dash", "Enorme Dash dans la face du mec", 0, MoveRangeType.RANGE, MoveName.DASH, 20);
        Range range = new Range(RangeType.CROSS, 0, 3);
        dash.setRange(range);
        skillTree.add(dash);
        this.unlockCapacity(dash);

        Move empower = new Move("Empower", "+10 Strength", 0, MoveRangeType.SELF, MoveName.EMPOWER, 20);
        skillTree.add(empower);
        SecondaryCharacteristics nope = new SecondaryCharacteristics();


        PrimaryCharacteristics strength1C = new PrimaryCharacteristics(5, 0, 0, 0, 0, 0);

        AmeliorationPrimary strength1 = new AmeliorationPrimary("S1", "Add 5 strength", strength1C, nope, 10);
        skillTree.add(strength1);

        PrimaryCharacteristics strength2C = new PrimaryCharacteristics(10, 0, 0, 0, 0, 0);
        AmeliorationPrimary strength2 = new AmeliorationPrimary("S2", "Add 10 strength", strength2C, nope, 150);
        skillTree.add(strength2);

        PrimaryCharacteristics strength3C = new PrimaryCharacteristics(20, 0, 0, 0, 0, 0);
        AmeliorationPrimary strength3 = new AmeliorationPrimary("S3", "Add 20 strength", strength3C, nope, 500);
        skillTree.add(strength3);

        PrimaryCharacteristics endurance1C = new PrimaryCharacteristics(0, 5, 0, 0, 0, 0);
        AmeliorationPrimary endurance1 = new AmeliorationPrimary("E1", "Add 5 endurance", endurance1C, nope, 50);
        skillTree.add(endurance1);

        PrimaryCharacteristics endurance2C = new PrimaryCharacteristics(0, 10, 0, 0, 0, 0);
        AmeliorationPrimary endurance2 = new AmeliorationPrimary("E2", "Add 10 endurance", endurance2C, nope, 150);
        skillTree.add(endurance2);
        PrimaryCharacteristics endurance3C = new PrimaryCharacteristics(0, 20, 0, 0, 0, 0);
        AmeliorationPrimary endurance3 = new AmeliorationPrimary("E3", "Add 20 endurance", endurance3C, nope, 500);
        skillTree.add(endurance3);

        strength2.addPrerequisite(strength1);
        strength3.addPrerequisite(strength2);
        endurance2.addPrerequisite(endurance1);
        endurance3.addPrerequisite(endurance2);

        this.setSkillTree(skillTree);
    }
}
