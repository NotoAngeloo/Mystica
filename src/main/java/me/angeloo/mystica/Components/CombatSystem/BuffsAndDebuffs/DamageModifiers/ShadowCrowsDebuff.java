package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;

public class ShadowCrowsDebuff implements StatusEffect {

    @Override
    public String getId() {
        return "shadow_crows";
    }

    @Override
    public double getMagnitude(){
        return 0.1;
    }

    @Override
    public boolean requireDurationDeclaration(){
        return true;
    }
}
