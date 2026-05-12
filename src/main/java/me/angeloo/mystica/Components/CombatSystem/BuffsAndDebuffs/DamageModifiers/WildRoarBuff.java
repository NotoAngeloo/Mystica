package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ApplicationBehavior;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

public class WildRoarBuff implements StatusEffect {


    @Override
    public String getId() {
        return "wild_roar";
    }

    @Override
    public int getDuration() {
        return 11 * 20;
    }

    @Override
    public ApplicationBehavior applicationBehavior(){
        return ApplicationBehavior.REPLACE_SMALLER;
    }


    @Override
    public boolean requireMagnitudeDeclaration(){
        return true;
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public String getIcon(LivingEntity entity, StatusInstance instance) {
        return "\ue433";
    }
}
