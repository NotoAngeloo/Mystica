package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ApplicationBehavior;

public class Pulled implements StatusEffect {

    @Override
    public String getId() {
        return "pull";
    }

    @Override
    public ApplicationBehavior applicationBehavior(){
        return ApplicationBehavior.IGNORE;
    }

    @Override
    public int getDuration() {
        return 4;
    }

    @Override
    public boolean isHardCC() {
        return true;
    }
}
