package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.BarType;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BladeTempestCrit {

    private final Mystica main;

    private final Map<UUID, BukkitTask> removeBuffTaskMap = new HashMap<>();
    private final Map<UUID, Boolean> active = new HashMap<>();

    public BladeTempestCrit(Mystica main){
        this.main = main;
    }

    public void applyBonus(LivingEntity entity){

        active.put(entity.getUniqueId(), true);

        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Status, false));
        }

        if(removeBuffTaskMap.containsKey(entity.getUniqueId())){
            removeBuffTaskMap.get(entity.getUniqueId()).cancel();
        }

        BukkitTask task = new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(count >= 10){
                    this.cancel();
                    active.remove(entity.getUniqueId());
                }

                count++;
            }
        }.runTaskTimerAsynchronously(main, 0, 20);

        removeBuffTaskMap.put(entity.getUniqueId(), task);

    }

    public void removeBonus(LivingEntity entity){
        active.remove(entity.getUniqueId());
        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Status, false));
        }
    }

    public Integer getTempestCrit(LivingEntity entity){
        if(!active.containsKey(entity.getUniqueId())){
            return 0;
        }

        if(active.get(entity.getUniqueId())){
            return 10;
        }

        return 0;
    }

}
