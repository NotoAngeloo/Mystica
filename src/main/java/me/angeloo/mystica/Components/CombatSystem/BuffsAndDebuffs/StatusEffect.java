package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Shields.ShieldInstance;
import org.bukkit.entity.LivingEntity;

public interface StatusEffect {

    /*

    status effect priority rules:

1: mechanic related effects, such as armor break

2: class rotation required effects such as soul mark or decision

3. team buffs, such as wild roar

4. crowd control and debuffs, for example, stun and modest

5. damage over time, for example burns. EXCEPTION! infection is priority 2.

     */

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

    /** Unicode icon */ //soul reap, for example, has a different icon on max stacks
    default String getIcon(LivingEntity entity, StatusInstance instance) { return "\ue219"; } // default icon

    /** Does this effect stack (e.g., adds values) or refresh duration? */
    default ApplicationBehavior applicationBehavior() { return ApplicationBehavior.REPLACE; }

    default int getMaxStacks() { return 1; }
    default boolean usesStacks() { return false; }
    //because soul reap at max stacks shouldn't display stacks
    default boolean displayStackCount(LivingEntity entity, StatusInstance instance){return true;}

    /** Does it have a multiplier? (like haste) */
    default boolean usesMultiplier() { return false; }

    default boolean requireDurationDeclaration() { return false; }
    default boolean requireMagnitudeDeclaration() { return false; }

    /** Create a new instance with custom params (shield amount, multiplier, etc.) */
    default StatusInstance createInstance(int duration, double magnitude, LivingEntity source){
        return new StatusInstance(this, duration, magnitude, source);
    }

    default ShieldInstance createShieldInstance(int duration, double magnitude, LivingEntity source){
        return new ShieldInstance(this, duration, magnitude, source);
    }

    //for stacking effect
    default void onApply(LivingEntity entity, StatusInstance instance, CombatContext combatContext, StatusApplicationResult statusApplicationResult) {}

    //for not
    default void onApply(LivingEntity entity, StatusInstance instance){

    }

    default boolean isHardCC(){
        return false;
    }


    default void onTick(LivingEntity entity, StatusInstance instance, CombatContext combatContext) {}
    default void onRemoveEffects(LivingEntity entity, StatusInstance instance, CombatContext combatContext) {}
    //non-damage related things
    default void onRemove(LivingEntity entity){}
    default void onDamage(LivingEntity entity, StatusInstance instance, double amount) {}

}
