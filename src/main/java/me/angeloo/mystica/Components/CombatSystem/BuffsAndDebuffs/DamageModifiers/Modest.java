package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusStackType;

public class Modest implements StatusEffect {

    @Override
    public String getId() {
        return "modest";
    }

    @Override
    public StatusStackType stackType(){
        return StatusStackType.REPLACE_SMALLER;
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
