package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;

public class ConcoctionDebuff implements StatusEffect {


    @Override
    public String getId() {
        return "concoction_debuff";
    }

    @Override
    public int getDuration() {
        return 15*20;
    }

    @Override
    public double getMagnitude(){
        return 0.05;
    }

}
