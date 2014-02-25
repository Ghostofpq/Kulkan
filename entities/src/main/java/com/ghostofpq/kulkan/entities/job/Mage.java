package com.ghostofpq.kulkan.entities.job;

import com.ghostofpq.kulkan.entities.characteristics.Characteristics;
import com.ghostofpq.kulkan.entities.job.capacity.*;
import com.ghostofpq.kulkan.entities.utils.Range;
import com.ghostofpq.kulkan.entities.utils.RangeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Move magicBallMove = new Move("Magic Ball", "Throw a ball full of magic", 0, MoveRangeType.RANGE_AOE, MoveName.FIREBALL, 15);

        Range range = new Range(RangeType.SQUARE, 0, 6);
        Range aoe = new Range(RangeType.SQUARE, 0, 2);

        magicBallMove.setRange(range);
        magicBallMove.setAreaOfEffect(aoe);

        skillTree.add(magicBallMove);
        this.unlockCapacity(magicBallMove);
        Map<Characteristics.fields, Integer> characteristicsValueMap;
        Characteristics characteristics;

        characteristicsValueMap = new HashMap<Characteristics.fields, Integer>();
        characteristicsValueMap.put(Characteristics.fields.MAGICAL_DAMAGE, 5);
        characteristics = new Characteristics(characteristicsValueMap);

        CharacteristicAmelioration intelligence1 = new CharacteristicAmelioration("I1",
                "Add 5 Intelligence", characteristics, 10);
        skillTree.add(intelligence1);

        characteristicsValueMap = new HashMap<Characteristics.fields, Integer>();
        characteristicsValueMap.put(Characteristics.fields.MAGICAL_DAMAGE, 10);
        characteristics = new Characteristics(characteristicsValueMap);

        CharacteristicAmelioration intelligence2 = new CharacteristicAmelioration("I2",
                "Add 10 Intelligence", characteristics, 150);
        skillTree.add(intelligence2);

        characteristicsValueMap = new HashMap<Characteristics.fields, Integer>();
        characteristicsValueMap.put(Characteristics.fields.MAGICAL_DAMAGE, 20);
        characteristics = new Characteristics(characteristicsValueMap);

        CharacteristicAmelioration intelligence3 = new CharacteristicAmelioration("I3",
                "Add 20 Intelligence", characteristics, 500);
        skillTree.add(intelligence3);

        characteristicsValueMap = new HashMap<Characteristics.fields, Integer>();
        characteristicsValueMap.put(Characteristics.fields.MAX_HEALTH, 15);
        characteristics = new Characteristics(characteristicsValueMap);

        CharacteristicAmelioration endurance1 = new CharacteristicAmelioration("E1",
                "Add 5 endurance", characteristics, 50);
        skillTree.add(endurance1);

        characteristicsValueMap = new HashMap<Characteristics.fields, Integer>();
        characteristicsValueMap.put(Characteristics.fields.MAX_HEALTH, 30);
        characteristics = new Characteristics(characteristicsValueMap);
        CharacteristicAmelioration endurance2 = new CharacteristicAmelioration("E2",
                "Add 10 endurance", characteristics, 150);
        skillTree.add(endurance2);

        characteristicsValueMap = new HashMap<Characteristics.fields, Integer>();
        characteristicsValueMap.put(Characteristics.fields.MAX_HEALTH, 50);
        characteristics = new Characteristics(characteristicsValueMap);
        CharacteristicAmelioration endurance3 = new CharacteristicAmelioration("E3",
                "Add 20 endurance", characteristics, 500);
        skillTree.add(endurance3);


        intelligence2.addPrerequisite(intelligence1);
        intelligence3.addPrerequisite(intelligence2);
        endurance2.addPrerequisite(endurance1);
        endurance3.addPrerequisite(endurance2);

        this.setSkillTree(skillTree);
    }
}
