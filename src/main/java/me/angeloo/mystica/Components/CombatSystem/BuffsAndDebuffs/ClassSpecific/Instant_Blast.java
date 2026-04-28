package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ClassSpecific;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;

public class Instant_Blast implements StatusEffect {

    @Override
    public String getId() {
        return "instant_blast";
    }

    @Override
    public int getPriority(){
        return 1;
    }
}
