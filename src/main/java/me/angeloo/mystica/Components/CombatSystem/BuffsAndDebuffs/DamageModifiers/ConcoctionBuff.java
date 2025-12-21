package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusStackType;

public class ConcoctionBuff implements StatusEffect {

    @Override
    public String getId() {
        return "concoction_buff";
    }

    @Override
    public int getDuration() {
        return 15*20;
    }

    //reduce damage taken, multiply result by .95
    @Override
    public double getMagnitude(){
        return 0.95;
    }
}
