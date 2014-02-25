package com.ghostofpq.kulkan.entities.job.warrior;

import com.ghostofpq.kulkan.entities.characteristics.Characteristics;
import com.ghostofpq.kulkan.entities.job.Job;
import com.ghostofpq.kulkan.entities.job.JobType;
import com.ghostofpq.kulkan.entities.job.capacity.*;
import com.ghostofpq.kulkan.entities.utils.Range;
import com.ghostofpq.kulkan.entities.utils.RangeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Move empower = new Move("Empower", "+10 Attack Damage", 0, MoveRangeType.SELF, MoveName.EMPOWER, 20);
        skillTree.add(empower);

        Map<Characteristics.fields, Integer> characteristicsValueMap;
        Characteristics characteristics;

        characteristicsValueMap = new HashMap<Characteristics.fields, Integer>();
        characteristicsValueMap.put(Characteristics.fields.ATTACK_DAMAGE, 5);
        characteristics = new Characteristics(characteristicsValueMap);

        CharacteristicAmelioration strength1 = new CharacteristicAmelioration("S1", "Add 5 Attack Damage", characteristics, 10);
        skillTree.add(strength1);

        characteristicsValueMap = new HashMap<Characteristics.fields, Integer>();
        characteristicsValueMap.put(Characteristics.fields.ATTACK_DAMAGE, 10);
        characteristics = new Characteristics(characteristicsValueMap);
        CharacteristicAmelioration strength2 = new CharacteristicAmelioration("S2", "Add 10 Attack Damage", characteristics, 150);
        skillTree.add(strength2);

        characteristicsValueMap = new HashMap<Characteristics.fields, Integer>();
        characteristicsValueMap.put(Characteristics.fields.ATTACK_DAMAGE, 20);
        characteristics = new Characteristics(characteristicsValueMap);
        CharacteristicAmelioration strength3 = new CharacteristicAmelioration("S3", "Add 20 Attack Damage", characteristics, 500);
        skillTree.add(strength3);

        characteristicsValueMap = new HashMap<Characteristics.fields, Integer>();
        characteristicsValueMap.put(Characteristics.fields.MAX_HEALTH, 15);
        characteristics = new Characteristics(characteristicsValueMap);
        CharacteristicAmelioration endurance1 = new CharacteristicAmelioration("E1", "Add 15 Max HP", characteristics, 50);
        skillTree.add(endurance1);

        characteristicsValueMap = new HashMap<Characteristics.fields, Integer>();
        characteristicsValueMap.put(Characteristics.fields.MAX_HEALTH, 30);
        characteristics = new Characteristics(characteristicsValueMap);
        CharacteristicAmelioration endurance2 = new CharacteristicAmelioration("E2", "Add 30 Max HP", characteristics, 150);
        skillTree.add(endurance2);

        characteristicsValueMap = new HashMap<Characteristics.fields, Integer>();
        characteristicsValueMap.put(Characteristics.fields.MAX_HEALTH, 50);
        characteristics = new Characteristics(characteristicsValueMap);
        CharacteristicAmelioration endurance3 = new CharacteristicAmelioration("E3", "Add 50 Max HP", characteristics, 500);
        skillTree.add(endurance3);

        strength2.addPrerequisite(strength1);
        strength3.addPrerequisite(strength2);
        endurance2.addPrerequisite(endurance1);
        endurance3.addPrerequisite(endurance2);

        this.setSkillTree(skillTree);
    }
}
