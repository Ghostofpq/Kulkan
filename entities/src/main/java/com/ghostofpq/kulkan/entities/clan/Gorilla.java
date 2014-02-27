package com.ghostofpq.kulkan.entities.clan;

import com.ghostofpq.kulkan.entities.characteristics.Characteristics;
import com.ghostofpq.kulkan.entities.job.JobType;

public class Gorilla extends Clan {
    public Gorilla() {
        String GORILLA_NAME = "GORILLA";
        String GORILLA_DESCRIPTION = "blablablabla GORILLAFFFF!!";

        int BASE_ATTACK_DAMAGE = 15;
        int BASE_MAGICAL_DAMAGE = 10;
        int BASE_ARMOR = 15;
        int BASE_MAGIC_RESIST = 10;
        int BASE_ARMOR_PENETRATION = 0;
        int BASE_MAGIC_PENETRATION = 0;
        int BASE_MOVEMENT = 2;
        int BASE_SPEED = 10;
        int BASE_MAX_HEALTH = 110;
        int BASE_MAX_MANA = 20;
        int BASE_LIFE_REGENERATION = 0;
        int BASE_MANA_REGENERATION = 0;
        int BASE_ESCAPE = 0;
        int BASE_CRITICAL_STRIKE = 0;
        int BASE_PRECISION = 0;
        int BASE_RESILIENCE = 0;

        int PER_LVL_ATTACK_DAMAGE = 3;
        int PER_LVL_MAGICAL_DAMAGE = 0;
        int PER_LVL_ARMOR = 1;
        int PER_LVL_MAGIC_RESIST = 1;
        int PER_LVL_ARMOR_PENETRATION = 1;
        int PER_LVL_MAGIC_PENETRATION = 0;
        int PER_LVL_MOVEMENT = 0;
        int PER_LVL_SPEED = 0;
        int PER_LVL_MAX_HEALTH = 9;
        int PER_LVL_MAX_MANA = 3;
        int PER_LVL_LIFE_REGENERATION = 0;
        int PER_LVL_MANA_REGENERATION = 0;
        int PER_LVL_ESCAPE = 0;
        int PER_LVL_CRITICAL_STRIKE = 0;
        int PER_LVL_PRECISION = 0;
        int PER_LVL_RESILIENCE = 0;

        this.setName(GORILLA_NAME);
        this.setDescription(GORILLA_DESCRIPTION);
        this.setClan(ClanType.GORILLA);

        this.setBaseCharacteristics(new Characteristics(
                BASE_ATTACK_DAMAGE,
                BASE_MAGICAL_DAMAGE,
                BASE_ARMOR,
                BASE_MAGIC_RESIST,
                BASE_ARMOR_PENETRATION,
                BASE_MAGIC_PENETRATION,
                BASE_MOVEMENT,
                BASE_SPEED,
                BASE_MAX_HEALTH,
                BASE_MAX_MANA,
                BASE_LIFE_REGENERATION,
                BASE_MANA_REGENERATION,
                BASE_ESCAPE,
                BASE_CRITICAL_STRIKE,
                BASE_PRECISION,
                BASE_RESILIENCE
        ));
        this.setLevelUpCharacteristics(new Characteristics(
                PER_LVL_ATTACK_DAMAGE,
                PER_LVL_MAGICAL_DAMAGE,
                PER_LVL_ARMOR,
                PER_LVL_MAGIC_RESIST,
                PER_LVL_ARMOR_PENETRATION,
                PER_LVL_MAGIC_PENETRATION,
                PER_LVL_MOVEMENT,
                PER_LVL_SPEED,
                PER_LVL_MAX_HEALTH,
                PER_LVL_MAX_MANA,
                PER_LVL_LIFE_REGENERATION,
                PER_LVL_MANA_REGENERATION,
                PER_LVL_ESCAPE,
                PER_LVL_CRITICAL_STRIKE,
                PER_LVL_PRECISION,
                PER_LVL_RESILIENCE
        ));

        this.setBaseJob(JobType.WARRIOR);
    }
}
