package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs;

import org.bukkit.entity.LivingEntity;

public interface StatusEffect {

    /** Unique ID like "poison", "generic_shield", "haste" */
    String getId();

    /** Duration in ticks for a new instance */
    default int getDuration(){
        return -1;
    };
    //this defaults to -1 for permanent effect

    default double getMagnitude(){return 0;}

    /** Priority for HUD — lower shows first */
    default int getPriority() { return 5; }

    /** Unicode icon */
    default String getIcon() { return "\uF829"; } // default icon

    /** Does this effect stack (e.g., adds values) or refresh duration? */
    default StatusStackType stackType() { return StatusStackType.REPLACE; }

    /** Does it have a multiplier? (like haste) */
    default boolean usesMultiplier() { return false; }

    default boolean requireDurationDeclaration() { return false; }
    default boolean requireMagnitudeDeclaration() { return false; }

    /** Create a new instance with custom params (shield amount, multiplier, etc.) */
    default StatusInstance createInstance(int duration, double magnitude){
        return new StatusInstance(this, duration, magnitude);
    }

    default void onApply(LivingEntity entity, StatusInstance instance) {}
    default void onTick(LivingEntity entity, StatusInstance instance) {}
    default void onRemove(LivingEntity entity, StatusInstance instance) {}
    default void onDamage(LivingEntity entity, StatusInstance instance, double amount) {}

}
