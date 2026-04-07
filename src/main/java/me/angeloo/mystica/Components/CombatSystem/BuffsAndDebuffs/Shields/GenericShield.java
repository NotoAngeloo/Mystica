package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Shields;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusStackType;

public class GenericShield implements StatusEffect {

    @Override
    public String getId() {
        return "shield";
    }

    //the reason in which duration is not declared is different sources all use generic shield. each source has its own timer

    @Override
    public boolean requireMagnitudeDeclaration(){
        return true;
    }

    @Override
    public StatusStackType stackType(){
        return StatusStackType.ADDITIVE;
    }

}
