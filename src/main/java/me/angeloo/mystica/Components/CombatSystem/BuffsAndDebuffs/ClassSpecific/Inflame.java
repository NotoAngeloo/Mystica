package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ClassSpecific;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusStackType;

public class Inflame implements StatusEffect {

    @Override
    public String getId() {
        return "inflame";
    }

    @Override
    public StatusStackType stackType(){
        return StatusStackType.ADDITIVE;
    }

    @Override
    public double getMagnitude(){
        return 1;
    }

    @Override
    public int getPriority(){
        return 1;
    }

}
