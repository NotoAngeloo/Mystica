package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ClassSpecific;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;

public class Decision implements StatusEffect {

    @Override
    public String getId() {
        return "decision";
    }

    @Override
    public int getPriority(){
        return 1;
    }
}
