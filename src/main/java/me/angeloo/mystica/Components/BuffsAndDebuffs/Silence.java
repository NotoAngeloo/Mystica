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

public class Silence {

    private final Mystica main;

    private final Map<UUID, BukkitTask> removeSilenceTaskMap = new HashMap<>();
    private final Map<UUID, Boolean> silenceMap = new HashMap<>();

    public Silence(Mystica main){
        this.main = main;
    }

    public void applySilence(LivingEntity entity, int time){
        silenceMap.put(entity.getUniqueId(), true);

        if(removeSilenceTaskMap.containsKey(entity.getUniqueId())){
            removeSilenceTaskMap.get(entity.getUniqueId()).cancel();
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
                    removeSilence(entity);
                }

                count++;
            }
        }.runTaskTimer(main, 0, 1);

        removeSilenceTaskMap.put(entity.getUniqueId(), task);
    }

    public boolean getSilence(LivingEntity entity){
        return silenceMap.getOrDefault(entity.getUniqueId(), false);
    }

    public void removeSilence(LivingEntity entity){
        silenceMap.remove(entity.getUniqueId());
        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player, false));
        }
    }

}
