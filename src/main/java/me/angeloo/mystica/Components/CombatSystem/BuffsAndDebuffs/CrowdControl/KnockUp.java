package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

public class KnockUp implements StatusEffect {

    @Override
    public String getId() {
        return "knock_up";
    }

    @Override
    public void onApply(LivingEntity entity, StatusInstance instance){
        //interrupt casting
    }

    @Override
    public StatusInstance createInstance(int duration, double magnitude) {
        // Use our custom SleepInstance
        return new KnockUpInstance(this, getDuration(), getMagnitude());
    }

}
