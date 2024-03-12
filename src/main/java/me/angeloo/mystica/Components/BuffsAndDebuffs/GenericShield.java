package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.CustomEvents.HealthChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GenericShield {


    private final Map<UUID, Double> shieldAmount = new HashMap<>();

    public GenericShield(){
    }

    public void applyOrAddShield(LivingEntity entity, double additional){

        double currentAmount = getCurrentShieldAmount(entity);

        double newAmount = currentAmount + additional;

        shieldAmount.put(entity.getUniqueId(), newAmount);
        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(entity, true));
    }

    public double removeSomeShieldAndReturnHowMuchOver(LivingEntity entity, double amount){
        double currentAmount = getCurrentShieldAmount(entity);
        double newAmount = currentAmount - amount;

        if(newAmount <=0){
            double remainder = Math.abs(0 - newAmount);
            removeShields(entity);
            return remainder;
        }

        shieldAmount.put(entity.getUniqueId(), newAmount);
        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(entity, true));
        return 0;
    }

    public void removeShields(LivingEntity entity){
        shieldAmount.remove(entity.getUniqueId());
        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(entity, true));
    }

    public double getCurrentShieldAmount(LivingEntity entity){
        return shieldAmount.getOrDefault(entity.getUniqueId(), 0.0);
    }

}
