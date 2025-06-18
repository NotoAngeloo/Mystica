package me.angeloo.mystica.Components.Abilities.ShadowKnight;

import me.angeloo.mystica.CustomEvents.HealthChangeEvent;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Hud.CooldownDisplayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Energy {

    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Integer> EnergyAmount = new HashMap<>();

    private final int maxEnergy = 100;

    public Energy(Mystica main, AbilityManager manager){
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void subTractEnergyFromEntity(LivingEntity caster, int cost){

        int currentMana = getCurrentEnergy(caster);
        int newCurrentMana = currentMana - cost;
        if(newCurrentMana < 0){
            newCurrentMana = 0;
        }
        EnergyAmount.put(caster.getUniqueId(), newCurrentMana);

        cooldownDisplayer.displayCooldown(caster, 2);
        cooldownDisplayer.displayCooldown(caster, 4);
        cooldownDisplayer.displayCooldown(caster, 5);
        cooldownDisplayer.displayCooldown(caster, 6);

        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(caster, true));
        if(caster instanceof Player){
            Player player = (Player) caster;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "resource"));
        }
    }

    public void addEnergyToEntity(LivingEntity entity, int amount){
        int currentMana = getCurrentEnergy(entity);
        int newCurrentMana = currentMana + amount;

        if(newCurrentMana > maxEnergy){
            newCurrentMana = maxEnergy;
        }
        EnergyAmount.put(entity.getUniqueId(), newCurrentMana);

        cooldownDisplayer.displayCooldown(entity, 2);
        cooldownDisplayer.displayCooldown(entity, 4);
        cooldownDisplayer.displayCooldown(entity, 5);
        cooldownDisplayer.displayCooldown(entity, 6);

        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(entity, true));
        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "resource"));
        }
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
