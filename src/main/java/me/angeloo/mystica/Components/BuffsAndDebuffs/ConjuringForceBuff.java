package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ConjuringForceBuff {

    private final Map<LivingEntity, Boolean> hasConjForceBuffMap = new HashMap<>();
    private final Map<LivingEntity, Double> extraDamageMap = new HashMap<>();

    public ConjuringForceBuff(){
    }

    public void applyConjuringForceBuff(LivingEntity entity, double extraDamageAmount){
        hasConjForceBuffMap.put(entity, true);

        double currentExtraDamage = getExtraDamageAmount(entity);

        if(extraDamageAmount > currentExtraDamage){
            extraDamageMap.put(entity, extraDamageAmount);

            if(entity instanceof Player){
                Player player = (Player) entity;
                Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status", false));
            }

        }

    }

    public void removeConjuringForceBuff(LivingEntity entity){
        hasConjForceBuffMap.remove(entity);
        extraDamageMap.remove(entity);

        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status", false));
        }

    }

    public boolean getIfConjForceBuff(LivingEntity entity){
        return hasConjForceBuffMap.getOrDefault(entity, false);
    }

    public double getExtraDamageAmount(LivingEntity entity){
        return extraDamageMap.getOrDefault(entity, 0.0);
    }

    public double getRangeModifier(LivingEntity entity){

        if(getIfConjForceBuff(entity)){
            return 10;
        }

        return 0;
    }

}
