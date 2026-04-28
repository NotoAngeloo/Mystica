package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ApplicationBehavior;

public class WellCrit implements StatusEffect {


    @Override
    public String getId() {
        return "light_well";
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public double getMagnitude(){
        return 10;
    }


    @Override
    public ApplicationBehavior applicationBehavior(){
        return ApplicationBehavior.IGNORE;
    }

}
