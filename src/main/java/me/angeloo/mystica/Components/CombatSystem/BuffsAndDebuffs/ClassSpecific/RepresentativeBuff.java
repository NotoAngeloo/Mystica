package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ClassSpecific;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

public class RepresentativeBuff implements StatusEffect {

    @Override
    public String getId() {
        return "representative";
    }

    @Override
    public int getDuration(){
        return 10 * 20;
    }

    @Override
    public boolean requireMagnitudeDeclaration() {
        return true;
    }

    @Override
    public int getPriority(){
        return 2;
    }

    @Override
    public String getIcon(LivingEntity entity, StatusInstance instance) {
        return "\ue439";
    }
}
