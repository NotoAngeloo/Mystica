package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.CustomEvents.StatusUpdateEvent;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Immune {

    private final Mystica main;

    private final Map<UUID, BukkitTask> removeImmobileTaskMap = new HashMap<>();
    private final Map<UUID, Boolean> immobileMap = new HashMap<>();

    public Immune(Mystica main){
        this.main = main;
    }

    public void applyImmune(LivingEntity entity, int time){
        immobileMap.put(entity.getUniqueId(), true);

        if(removeImmobileTaskMap.containsKey(entity.getUniqueId())){
            removeImmobileTaskMap.get(entity.getUniqueId()).cancel();
        }

        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player, false));
        }

        if(time == 0){
            return;
        }

        BukkitTask task = new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(count >= time){
                    this.cancel();
                    removeImmune(entity);
                }

                count++;
            }
        }.runTaskTimer(main, 0, 1);

        removeImmobileTaskMap.put(entity.getUniqueId(), task);
    }

    public boolean getImmune(LivingEntity entity){
        return immobileMap.getOrDefault(entity.getUniqueId(), false);
    }

    public void removeImmune(LivingEntity entity){
        immobileMap.remove(entity.getUniqueId());
        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player, false));
        }

    }

}
