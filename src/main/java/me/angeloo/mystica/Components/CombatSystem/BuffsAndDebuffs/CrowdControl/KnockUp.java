package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CombatContext;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusApplicationResult;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

public class KnockUp implements StatusEffect {

    @Override
    public String getId() {
        return "knock_up";
    }

    @Override
    public void onApply(LivingEntity entity, StatusInstance instance, CombatContext combatContext, StatusApplicationResult statusApplicationResult){
        //interrupt casting
    }

    @Override
    public StatusInstance createInstance(int duration, double magnitude, LivingEntity source) {
        // Use our custom SleepInstance
        return new KnockUpInstance(this, getDuration(), getMagnitude(), source);
    }

}
