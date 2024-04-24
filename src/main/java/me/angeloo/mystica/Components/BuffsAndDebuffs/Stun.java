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

public class Stun {

    private final Mystica main;
    private final Immobile immobile;

    private final Map<UUID, BukkitTask> removeStunTaskMap = new HashMap<>();
    private final Map<UUID, Boolean> stunMap = new HashMap<>();

    public  Stun(Mystica main, Immobile immobile){
        this.main = main;
        this.immobile = immobile;
    }

    public void applyStun(LivingEntity entity, int time){
        immobile.applyImmobile(entity, time);

        stunMap.put(entity.getUniqueId(), true);

        if(removeStunTaskMap.containsKey(entity.getUniqueId())){
            removeStunTaskMap.get(entity.getUniqueId()).cancel();
        }

        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
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
                    removeStun(entity);
                }

                count++;
            }
        }.runTaskTimer(main, 0, 1);

        removeStunTaskMap.put(entity.getUniqueId(), task);
    }


    public boolean getIfStun(LivingEntity entity){
        return stunMap.getOrDefault(entity.getUniqueId(), false);
    }


    public void removeStun(LivingEntity entity){
        stunMap.remove(entity.getUniqueId());
        immobile.removeImmobile(entity);
        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
        }
    }

}
