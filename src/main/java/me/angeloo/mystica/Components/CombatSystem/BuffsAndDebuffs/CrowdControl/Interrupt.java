package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;

public class Interrupt implements StatusEffect {

    @Override
    public String getId() {
        return "interrupt";
    }

    //one sec
    @Override
    public int getDuration() {
        return 20;
    }

}
