package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ClassSpecific;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ApplicationBehavior;

public class Soul_Mark implements StatusEffect {

    @Override
    public String getId() {
        return "soul_mark";
    }

    @Override
    public ApplicationBehavior applicationBehavior(){
        return ApplicationBehavior.ADDITIVE;
    }

    @Override
    public double getMagnitude(){
        return 1;
    }

    @Override
    public int getPriority(){
        return 1;
    }

    @Override
    public int getMaxStacks() {
        return 5;
    }

    @Override
    public boolean usesStacks() {
        return true;
    }


}
