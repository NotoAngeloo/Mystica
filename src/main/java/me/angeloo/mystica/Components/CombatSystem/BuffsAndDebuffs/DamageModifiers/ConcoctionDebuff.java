package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

public class ConcoctionDebuff implements StatusEffect {


    @Override
    public String getId() {
        return "concoction_debuff";
    }

    @Override
    public int getDuration() {
        return 15*20;
    }

    //increase damage taken
    @Override
    public double getMagnitude(){
        return 1.05;
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public String getIcon(LivingEntity entity, StatusInstance instance) {
        return "\ue42c";
    }
}
