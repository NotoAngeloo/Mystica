package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusStackType;

public class ConjuringForceBuff implements StatusEffect {

    @Override
    public String getId(){
        return "conjuring_force";
    }

    @Override
    public int getDuration() {
        return -1;
    }

    @Override
    public StatusStackType stackType(){
        return StatusStackType.REPLACE_SMALLER;
    }


    @Override
    public String getIcon() {
        return "\uE02A";
    }

    @Override
    public boolean requireMagnitudeDeclaration(){
        return true;
    }

    //Range modifer +10, but i have no idea how to implement it withing this refactor
}
