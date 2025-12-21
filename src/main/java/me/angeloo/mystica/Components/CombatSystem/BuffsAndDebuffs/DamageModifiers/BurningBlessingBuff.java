package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusStackType;

public class BurningBlessingBuff implements StatusEffect {

    @Override
    public String getId() {
        return "burning_blessing";
    }

    @Override
    public String getIcon() {
        return "\uE026";
    }

    @Override
    public StatusStackType stackType(){
        return StatusStackType.REPLACE_SMALLER;
    }

    @Override
    public int getDuration(){
        return 8 * 20;
    }

    @Override
    public int getPriority() {
        return 3;
    }

    //increase max health by magnitude
    @Override
    public boolean requireMagnitudeDeclaration(){
        return true;
    }


}
