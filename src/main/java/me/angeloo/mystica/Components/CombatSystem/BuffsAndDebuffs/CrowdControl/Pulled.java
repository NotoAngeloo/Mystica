package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusStackType;

public class Pulled implements StatusEffect {

    @Override
    public String getId() {
        return "pull";
    }

    @Override
    public int getDuration() {
        return -1;
    }

    @Override
    public StatusStackType stackType(){
        return StatusStackType.IGNORE;
    }

}
