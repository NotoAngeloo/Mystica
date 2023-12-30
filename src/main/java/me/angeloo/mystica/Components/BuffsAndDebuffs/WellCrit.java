package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.CustomEvents.StatusUpdateEvent;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WellCrit {

    private final Map<UUID, Boolean> active = new HashMap<>();

    public WellCrit(){

    }

    public void applyBonus(LivingEntity entity){

        active.put(entity.getUniqueId(), true);

        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player, false));
        }

    }

    public void removeBonus(LivingEntity entity){
        active.remove(entity.getUniqueId());
        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player, false));
        }
    }

    public Integer getWellCrit(LivingEntity entity){
        if(!active.containsKey(entity.getUniqueId())){
            return 0;
        }

        if(active.get(entity.getUniqueId())){
            return 10;
        }

        return 0;
    }

}
