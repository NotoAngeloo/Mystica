package me.angeloo.mystica.Components.CombatSystem.Abilities;

import org.bukkit.entity.LivingEntity;

public interface Ability {

    String getId();

    boolean use(LivingEntity caster);

    int cooldown();

    default int getGlobalCooldownMillis() {
        return 1000; // default 1 second
    }

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

    //reason why is to be able to display greyed out icons under certain conditions
    default String skillBarIcon(LivingEntity entity){
        return "\ue1d3";
    }

    default String statusBarIcon(){return "\ue219";}

}
