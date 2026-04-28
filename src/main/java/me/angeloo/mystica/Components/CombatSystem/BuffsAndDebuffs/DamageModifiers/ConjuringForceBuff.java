package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ApplicationBehavior;

public class ConjuringForceBuff implements StatusEffect {

    @Override
    public String getId(){
        return "conjuring_force";
    }

    @Override
    public ApplicationBehavior applicationBehavior(){
        return ApplicationBehavior.REPLACE_SMALLER;
    }


    //increase damage dealt by flat amount
    @Override
    public boolean requireMagnitudeDeclaration(){
        return true;
    }

}
