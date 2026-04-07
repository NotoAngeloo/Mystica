package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusStackType;

public class GenericDamageReduction implements StatusEffect {

    @Override
    public String getId() {
        return "damage_reduction";
    }

    //Smaller is better, means they take less
    @Override
    public StatusStackType stackType(){
        return StatusStackType.REPLACE_LARGER;
    }

    @Override
    public boolean requireMagnitudeDeclaration(){
        return true;
    }

    @Override
    public boolean requireDurationDeclaration(){
        return true;
    }

}
