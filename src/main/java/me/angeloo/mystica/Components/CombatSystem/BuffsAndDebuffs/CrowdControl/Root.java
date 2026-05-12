package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;

public class Root implements StatusEffect {

    @Override
    public String getId() {
        return "root";
    }

    @Override
    public boolean requireDurationDeclaration(){
        return true;
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public boolean isHardCC() {
        return true;
    }
}
