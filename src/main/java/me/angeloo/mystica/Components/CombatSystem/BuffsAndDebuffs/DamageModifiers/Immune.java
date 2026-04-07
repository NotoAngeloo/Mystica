package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;

public class Immune implements StatusEffect {

    @Override
    public String getId() {
        return "immune";
    }

    @Override
    public boolean requireDurationDeclaration(){
        return true;
    }
}
