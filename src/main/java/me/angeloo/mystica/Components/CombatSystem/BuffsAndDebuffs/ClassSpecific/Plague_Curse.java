package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ClassSpecific;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;

public class Plague_Curse implements StatusEffect {

    @Override
    public String getId() {
        return "plague_curse";
    }

    @Override
    public int getDuration(){
        return 10 * 20;
    }

    @Override
    public int getPriority() {
        return 2;
    }
}
