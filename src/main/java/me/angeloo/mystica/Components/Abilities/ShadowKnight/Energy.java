package me.angeloo.mystica.Components.Abilities.ShadowKnight;

import me.angeloo.mystica.CustomEvents.HealthChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Energy {

    private final Map<UUID, Integer> EnergyAmount = new HashMap<>();

    private final int maxEnergy = 100;

    public Energy(){
    }

    public void subTractEnergyFromEntity(LivingEntity caster, int cost){

        int currentMana = getCurrentEnergy(caster);
        int newCurrentMana = currentMana - cost;
        if(newCurrentMana < 0){
            newCurrentMana = 0;
        }
        EnergyAmount.put(caster.getUniqueId(), newCurrentMana);
        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(caster, true));
    }

    public void addEnergyToEntity(LivingEntity entity, int amount){
        int currentMana = getCurrentEnergy(entity);
        int newCurrentMana = currentMana + amount;

        if(newCurrentMana > maxEnergy){
            newCurrentMana = maxEnergy;
        }
        EnergyAmount.put(entity.getUniqueId(), newCurrentMana);
        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(entity, true));
    }

    public int getCurrentEnergy(LivingEntity livingEntity){

        if(!EnergyAmount.containsKey(livingEntity.getUniqueId())){
            EnergyAmount.put(livingEntity.getUniqueId(), maxEnergy);
        }

        return EnergyAmount.get(livingEntity.getUniqueId());
    }

    public void regenEnergyNaturally(LivingEntity entity) {
        int currentMana = getCurrentEnergy(entity);

        int manaRegenRate = 20;


        if (currentMana > maxEnergy) {
            EnergyAmount.put(entity.getUniqueId(), maxEnergy);
        }

        if (currentMana < maxEnergy) {
            addEnergyToEntity(entity, manaRegenRate);

        }
    }



}
