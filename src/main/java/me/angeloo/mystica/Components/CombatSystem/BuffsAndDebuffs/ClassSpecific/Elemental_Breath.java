package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ClassSpecific;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;

public class Elemental_Breath implements StatusEffect {

    @Override
    public String getId() {
        return "elemental_breath";
    }

    @Override
    public boolean requireDurationDeclaration() {
        return true;
    }

    @Override
    public int getPriority(){
        return 1;
    }

}
