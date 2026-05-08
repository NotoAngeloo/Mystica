package me.angeloo.mystica.Components.CombatSystem.Abilities.BasicAttacks;

import org.bukkit.entity.LivingEntity;

public interface BasicAttackDefinition {

    boolean performStage(LivingEntity caster, int stage);

    int getMaxStages(LivingEntity caster);

    int getStageDelay(LivingEntity caster, int stage);

    default boolean canStart(LivingEntity caster){
        return true;
    }

    default boolean canContinue(LivingEntity caster, int nextStage){
        return true;
    }

    default String skillBarIcon(LivingEntity entity){
        return "\ue3b4";
    }

}
