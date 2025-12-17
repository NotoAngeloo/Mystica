package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;

public class BladeTempestCrit implements StatusEffect {

    @Override
    public String getId() {
        return "blade_tempest";
    }

    @Override
    public int getDuration() {
        return 10 * 20;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public double getMagnitude(){
        return 10;
    }

    @Override
    public String getIcon() {
        return "\uE023";
    }


}
