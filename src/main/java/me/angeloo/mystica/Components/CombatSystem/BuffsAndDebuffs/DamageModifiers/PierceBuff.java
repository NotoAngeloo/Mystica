package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;

public class PierceBuff implements StatusEffect {

    @Override
    public String getId() {
        return "pierce";
    }

    @Override
    public int getDuration() {
        return 20;
    }

    //multiplies enemy def by .75, thus ignoring 25% of it
    @Override
    public double getMagnitude(){
        return 0.75;
    }


}
