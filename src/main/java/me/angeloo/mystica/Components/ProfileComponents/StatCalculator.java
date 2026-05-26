package me.angeloo.mystica.Components.ProfileComponents;

import me.angeloo.mystica.Components.CombatSystem.Classes.PlayerClass;
import me.angeloo.mystica.Components.CombatSystem.Classes.SubClass;

public final class StatCalculator {

    public Stats calculate(int level,
                           PlayerClass playerClass,
                           SubClass subClass){

        /*
         * Base values
         */
        int attack = 50;
        int health = 100;
        int defense = 50;
        int magicDefense = 50;
        int crit = 1;

        StatProfile classProfile = playerClass.getStatProfile();

        attack += level * classProfile.attackPerLevel();
        health += level * classProfile.healthPerLevel();
        defense += level * classProfile.defensePerLevel();
        magicDefense += level * classProfile.magicDefensePerLevel();
        crit += classProfile.baseCrit();

        if(subClass!=null){
            StatProfile subProfile = subClass.getStatProfile();

            attack += level * subProfile.attackPerLevel();
            health += level * subProfile.healthPerLevel();
            defense += level * subProfile.defensePerLevel();
            magicDefense += level * subProfile.magicDefensePerLevel();
            crit += subProfile.baseCrit();
        }

        return new Stats(
                level,
                attack,
                health,
                defense,
                magicDefense,
                crit
        );
    }

}
