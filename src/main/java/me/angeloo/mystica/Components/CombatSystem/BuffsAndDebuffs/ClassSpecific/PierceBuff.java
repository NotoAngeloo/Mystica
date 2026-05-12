package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ClassSpecific;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

public class PierceBuff implements StatusEffect {

    @Override
    public String getId() {
        return "pierce";
    }

    @Override
    public int getDuration() {
        return 20;
    }

    @Override
    public int getPriority() {
        return 2;
    }

    //multiplies enemy def by .75, thus ignoring 25% of it
    @Override
    public double getMagnitude(){
        return 0.75;
    }

    @Override
    public String getIcon(LivingEntity entity, StatusInstance instance) {
        return "\ue423";
    }
}
