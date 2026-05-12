package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

public class ShadowCrowsDebuff implements StatusEffect {

    @Override
    public String getId() {
        return "shadow_crows";
    }

    //increase damage taken on target
    @Override
    public double getMagnitude(){
        return 0.9;
    }

    @Override
    public int getDuration(){return 15*20;}

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public String getIcon(LivingEntity entity, StatusInstance instance) {
        return "\ue432";
    }
}
