package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Blocking {

    private final Mystica main;

    private final Map<UUID, BukkitTask> removeImmobileTaskMap = new HashMap<>();
    private final Map<UUID, Boolean> blockingMap = new HashMap<>();

    public Blocking(Mystica main){
        this.main = main;
    }

    public void applyBlocking(LivingEntity entity, int time){
        blockingMap.put(entity.getUniqueId(), true);

        if(removeImmobileTaskMap.containsKey(entity.getUniqueId())){
            removeImmobileTaskMap.get(entity.getUniqueId()).cancel();
        }

        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status", false));
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
                    removeBlocking(entity);
                }

                count++;
            }
        }.runTaskTimer(main, 0, 1);

        removeImmobileTaskMap.put(entity.getUniqueId(), task);
    }

    public boolean getIfBlocking(LivingEntity entity){
        return blockingMap.getOrDefault(entity.getUniqueId(), false);
    }

    public void removeBlocking(LivingEntity entity){
        blockingMap.remove(entity.getUniqueId());
        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status", false));
        }

    }

}
