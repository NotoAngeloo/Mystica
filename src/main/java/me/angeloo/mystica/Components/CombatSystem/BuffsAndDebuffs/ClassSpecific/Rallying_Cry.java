package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ClassSpecific;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

public class Rallying_Cry implements StatusEffect {

    @Override
    public String getId() {
        return "rallying_cry";
    }

    @Override
    public int getPriority(){
        return 2;
    }

    @Override
    public boolean requireDurationDeclaration() {
        return true;
    }

    @Override
    public String getIcon(LivingEntity entity, StatusInstance instance) {
        return "\ue427";
    }
}
