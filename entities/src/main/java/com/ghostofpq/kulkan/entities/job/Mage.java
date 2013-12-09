package com.ghostofpq.kulkan.entities.job;

import com.ghostofpq.kulkan.entities.characteristics.PrimaryCharacteristics;
import com.ghostofpq.kulkan.entities.job.capacity.AmeliorationPrimary;
import com.ghostofpq.kulkan.entities.job.capacity.Capacity;
import com.ghostofpq.kulkan.entities.job.capacity.Move;

import java.util.ArrayList;
import java.util.List;

public class Mage extends Job {
    private final static String MAGE_NAME = "Mage";
    private final static String MAGE_DESC = "Mages are stiffy";

    public Mage() {
        super(MAGE_NAME, MAGE_DESC);
        this.jobType = JobType.MAGE;
        this.prepareSkillTree();

    }

    // Warrior Skills
    private void prepareSkillTree() {
        List<Capacity> skillTree = new ArrayList<Capacity>();

        Move magicBallMove = new Move("Magic Ball", "Throw a ball full of magic", 0);
        skillTree.add(magicBallMove);
        this.unlockCapacity(magicBallMove);

        PrimaryCharacteristics intelligence1C = new PrimaryCharacteristics(5, 0, 0,
                0, 0, 0);
        AmeliorationPrimary intelligence1 = new AmeliorationPrimary("I1",
                "Add 5 Intelligence", intelligence1C, 10);
        skillTree.add(intelligence1);

        PrimaryCharacteristics intelligence2C = new PrimaryCharacteristics(10, 0,
                0, 0, 0, 0);
        AmeliorationPrimary intelligence2 = new AmeliorationPrimary("I2",
                "Add 10 Intelligence", intelligence2C, 150);
        skillTree.add(intelligence2);

        PrimaryCharacteristics intelligence3C = new PrimaryCharacteristics(20, 0,
                0, 0, 0, 0);
        AmeliorationPrimary intelligence3 = new AmeliorationPrimary("I3",
                "Add 20 Intelligence", intelligence3C, 500);
        skillTree.add(intelligence3);

        PrimaryCharacteristics endurance1C = new PrimaryCharacteristics(0, 5,
                0, 0, 0, 0);
        AmeliorationPrimary endurance1 = new AmeliorationPrimary("E1",
                "Add 5 endurance", endurance1C, 50);
        skillTree.add(endurance1);

        PrimaryCharacteristics endurance2C = new PrimaryCharacteristics(0, 10,
                0, 0, 0, 0);
        AmeliorationPrimary endurance2 = new AmeliorationPrimary("E2",
                "Add 10 endurance", endurance2C, 150);
        skillTree.add(endurance2);
        PrimaryCharacteristics endurance3C = new PrimaryCharacteristics(0, 20,
                0, 0, 0, 0);
        AmeliorationPrimary endurance3 = new AmeliorationPrimary("E3",
                "Add 20 endurance", endurance3C, 500);
        skillTree.add(endurance3);


        intelligence2.addPrerequisite(intelligence1);
        intelligence3.addPrerequisite(intelligence2);
        endurance2.addPrerequisite(endurance1);
        endurance3.addPrerequisite(endurance2);

        this.setSkillTree(skillTree);
    }
}
