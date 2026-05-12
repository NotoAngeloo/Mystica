package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ClassSpecific;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

public class Elemental_Breath implements StatusEffect {

    @Override
    public String getId() {
        return "elemental_breath";
    }

    @Override
    public boolean requireDurationDeclaration() {
        return true;
    }

    @Override
    public int getPriority(){
        return 2;
    }

    @Override
    public String getIcon(LivingEntity entity, StatusInstance instance) {
        return "\ue424";
    }
}
