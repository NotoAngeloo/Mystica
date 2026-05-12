package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Shields;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

public class WindWallShield implements StatusEffect {

    @Override
    public String getId() {
        return "wind_wall";
    }


    //on application, it is ALWAYS 1/4th max hp
    @Override
    public boolean requireMagnitudeDeclaration(){
        return true;
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public String getIcon(LivingEntity entity, StatusInstance instance) {
        return "\ue438";
    }
}
