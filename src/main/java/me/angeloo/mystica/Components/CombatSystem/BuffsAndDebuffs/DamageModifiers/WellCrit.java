package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusStackType;

public class WellCrit implements StatusEffect {


    @Override
    public String getId() {
        return "light_well";
    }

    @Override
    public int getDuration() {
        return -1;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public double getMagnitude(){
        return 10;
    }

    @Override
    public String getIcon() {
        return "\uE02B";
    }

    @Override
    public StatusStackType stackType(){
        return StatusStackType.IGNORE;
    }

}
