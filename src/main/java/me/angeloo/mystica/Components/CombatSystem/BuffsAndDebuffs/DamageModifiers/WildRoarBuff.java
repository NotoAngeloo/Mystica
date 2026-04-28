package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ApplicationBehavior;

public class WildRoarBuff implements StatusEffect {


    @Override
    public String getId() {
        return "wild_roar";
    }

    @Override
    public int getDuration() {
        return 11 * 20;
    }

    @Override
    public ApplicationBehavior applicationBehavior(){
        return ApplicationBehavior.REPLACE_SMALLER;
    }


    @Override
    public boolean requireMagnitudeDeclaration(){
        return true;
    }
}
