package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ClassSpecific;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;

public class Rallying_Cry implements StatusEffect {

    @Override
    public String getId() {
        return "rallying_cry";
    }

    @Override
    public int getPriority(){
        return 1;
    }

    @Override
    public boolean requireDurationDeclaration() {
        return true;
    }
}
