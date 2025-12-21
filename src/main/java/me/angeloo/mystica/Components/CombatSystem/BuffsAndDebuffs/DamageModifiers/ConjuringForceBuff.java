package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusStackType;

public class ConjuringForceBuff implements StatusEffect {

    @Override
    public String getId(){
        return "conjuring_force";
    }

    @Override
    public StatusStackType stackType(){
        return StatusStackType.REPLACE_SMALLER;
    }


    @Override
    public String getIcon() {
        return "\uE02A";
    }

    //increase damage dealt by flat amount
    @Override
    public boolean requireMagnitudeDeclaration(){
        return true;
    }

}
