package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusStackType;

public class WildRoarBuff implements StatusEffect {


    @Override
    public String getId() {
        return "wild_roar";
    }

    @Override
    public int getDuration() {
        return 11 * 20;
    }

    @Override
    public StatusStackType stackType(){
        return StatusStackType.REPLACE_SMALLER;
    }

    @Override
    public String getIcon() {
        return "\uE029";
    }

    @Override
    public boolean requireMagnitudeDeclaration(){
        return true;
    }
}
