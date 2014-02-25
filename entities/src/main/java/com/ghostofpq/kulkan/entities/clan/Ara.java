package com.ghostofpq.kulkan.entities.clan;

import com.ghostofpq.kulkan.entities.characteristics.Characteristics;
import com.ghostofpq.kulkan.entities.job.JobType;

public class Ara extends Clan {
    public Ara() {
        String ARA_NAME = "ARA";
        String ARA_DESCRIPTION = "blablablabla ARAFFFF!!";

        int BASE_ATTACK_DAMAGE = 10;
        int BASE_MAGICAL_DAMAGE = 20;
        int BASE_ARMOR = 10;
        int BASE_MAGIC_RESIST = 10;
        int BASE_ARMOR_PENETRATION = 0;
        int BASE_MAGIC_PENETRATION = 0;
        int BASE_MOVEMENT = 2;
        int BASE_SPEED = 10;
        int BASE_MAX_HEALTH = 90;
        int BASE_MAX_MANA = 30;
        int BASE_LIFE_REGENERATION = 0;
        int BASE_MANA_REGENERATION = 3;
        int BASE_ESCAPE = 10;
        int BASE_CRITICAL_STRIKE = 0;
        int BASE_PRECISION = 0;
        int BASE_RESILIENCE = 0;

        int PER_LVL_ATTACK_DAMAGE = 1;
        int PER_LVL_MAGICAL_DAMAGE = 3;
        int PER_LVL_ARMOR = 1;
        int PER_LVL_MAGIC_RESIST = 1;
        int PER_LVL_ARMOR_PENETRATION = 0;
        int PER_LVL_MAGIC_PENETRATION = 2;
        int PER_LVL_MOVEMENT = 0;
        int PER_LVL_SPEED = 0;
        int PER_LVL_MAX_HEALTH = 5;
        int PER_LVL_MAX_MANA = 7;
        int PER_LVL_LIFE_REGENERATION = 0;
        int PER_LVL_MANA_REGENERATION = 1;
        int PER_LVL_ESCAPE = 0;
        int PER_LVL_CRITICAL_STRIKE = 0;
        int PER_LVL_PRECISION = 0;
        int PER_LVL_RESILIENCE = 0;

        this.setName(ARA_NAME);
        this.setDescription(ARA_DESCRIPTION);
        this.setClan(ClanType.ARA);

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

        this.setBaseJob(JobType.MAGE);
    }
}
