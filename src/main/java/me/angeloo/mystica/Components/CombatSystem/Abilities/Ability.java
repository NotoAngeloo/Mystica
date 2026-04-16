package me.angeloo.mystica.Components.CombatSystem.Abilities;

import org.bukkit.entity.LivingEntity;

public interface Ability {

    String getId();

    void use(LivingEntity caster);

    //this is for rez effects which work different as companions
    default void useAsCompanion(LivingEntity caster, LivingEntity target){

    }

    default boolean usable(LivingEntity caster){
        return true;
    }

    default boolean usable(LivingEntity caster, LivingEntity target){
        return true;
    }

    default void onExternalTrigger(LivingEntity caster){

    }

    default void onExternalTrigger(LivingEntity caster, LivingEntity target){

    }

    default void onExternalTrigger(LivingEntity caster, int amount){

    }

}
