package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ApplicationBehavior;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

public class WellCrit implements StatusEffect {


    @Override
    public String getId() {
        return "light_well";
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public double getMagnitude(){
        return 10;
    }


    @Override
    public ApplicationBehavior applicationBehavior(){
        return ApplicationBehavior.IGNORE;
    }

    @Override
    public String getIcon(LivingEntity entity, StatusInstance instance) {
        return "\ue41f";
    }
}
