package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ClassSpecific;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;

public class RepresentativeBuff implements StatusEffect {

    @Override
    public String getId() {
        return "representative";
    }

    @Override
    public int getDuration(){
        return 10 * 20;
    }

    @Override
    public boolean requireMagnitudeDeclaration() {
        return true;
    }

    @Override
    public int getPriority(){
        return 1;
    }
}
