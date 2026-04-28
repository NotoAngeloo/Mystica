package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ClassSpecific;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.*;
import org.bukkit.entity.LivingEntity;

public class Inflame implements StatusEffect {

    @Override
    public String getId() {
        return "inflame";
    }

    @Override
    public ApplicationBehavior applicationBehavior(){
        return ApplicationBehavior.ADDITIVE;
    }

    @Override
    public double getMagnitude(){
        return 1;
    }

    @Override
    public int getPriority(){
        return 1;
    }

    @Override
    public int getMaxStacks() {
        return 4;
    }

    @Override
    public boolean usesStacks() {
        return true;
    }

    @Override
    public void onApply(LivingEntity entity, StatusInstance instance, CombatContext context, StatusApplicationResult result) {

        if(result.reachedMaxStacks){
            LivingEntity caster = instance.getSource();

            if(caster == null){
                return;
            }

            context.getCooldownManager().clear(entity.getUniqueId(), -1);
            instance.markForRemoval();

        }

    }
}
