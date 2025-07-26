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

public class Sleep {

    private final Mystica main;
    private final Immobile immobile;

    private final Map<UUID, BukkitTask> removeSleepTaskMap = new HashMap<>();
    private final Map<UUID, Boolean> sleepMap = new HashMap<>();

    public  Sleep(Mystica main, Immobile immobile){
        this.main = main;
        this.immobile = immobile;
    }

    public void applySleep(LivingEntity entity, int time){
        immobile.applyImmobile(entity, time);

        sleepMap.put(entity.getUniqueId(), true);

        if(removeSleepTaskMap.containsKey(entity.getUniqueId())){
            removeSleepTaskMap.get(entity.getUniqueId()).cancel();
        }

        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Status, false));
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
                    removeSleep(entity);
                }

                count++;
            }
        }.runTaskTimerAsynchronously(main, 0, 1);

        removeSleepTaskMap.put(entity.getUniqueId(), task);
    }


    public boolean getIfSleep(LivingEntity entity){
        return sleepMap.getOrDefault(entity.getUniqueId(), false);
    }

    public void forceWakeUp(LivingEntity entity){
        removeSleep(entity);
        immobile.removeImmobile(entity);
    }

    public void removeSleep(LivingEntity entity){
        sleepMap.remove(entity.getUniqueId());
        if(entity instanceof Player){
            Bukkit.getScheduler().runTask(main, ()->{
                Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent((Player) entity, BarType.Status, false));
            });

        }
    }
}
