package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;

public class Fear implements StatusEffect {

    @Override
    public String getId() {
        return "fear";
    }

    @Override
    public boolean requireDurationDeclaration(){
        return true;
    }

    @Override
    public StatusInstance createInstance(int duration, double magnitude) {
        // Use our custom SleepInstance
        return new FearInstance(this, getDuration(), getMagnitude());
    }

}
