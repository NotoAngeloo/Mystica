package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusStackType;

public class FlamingSigilAttack implements StatusEffect {

    @Override
    public String getId() {
        return "flaming_sigil_attack";
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

    @Override
    public boolean requireMagnitudeDeclaration(){
        return true;
    }

}
