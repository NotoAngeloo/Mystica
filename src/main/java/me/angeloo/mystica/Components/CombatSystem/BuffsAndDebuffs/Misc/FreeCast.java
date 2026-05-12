package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Misc;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

public class FreeCast implements StatusEffect {

    @Override
    public String getId() {
        return "free_cast";
    }

    @Override
    public int getPriority(){
        return 2;
    }

    @Override
    public String getIcon(LivingEntity entity, StatusInstance instance) {
        return "\ue426";
    }
}
