package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs;

import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class PassThrough {

    private final Map<LivingEntity, LivingEntity> passingToThisCaster = new HashMap<>();

    public PassThrough(){

    }

    public void applyPassThrough(LivingEntity caster, LivingEntity target){
        passingToThisCaster.put(target, caster);
    }

    public void removePassThrough(LivingEntity livingEntity){
        passingToThisCaster.remove(livingEntity);
    }

    public LivingEntity getPassingToCaster(LivingEntity target){
        return passingToThisCaster.get(target);
    }

    public boolean getIfPassingToPlayer(LivingEntity target){
        return passingToThisCaster.containsKey(target);
    }

}
