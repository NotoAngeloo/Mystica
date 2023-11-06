package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.Mystica;
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

        if(time == 0){
            return;
        }

        BukkitTask task = new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(count >= time){
                    this.cancel();
                    sleepMap.remove(entity.getUniqueId());
                }

                count++;
            }
        }.runTaskTimer(main, 0, 1);

        removeSleepTaskMap.put(entity.getUniqueId(), task);
    }


    public boolean getIfSleep(LivingEntity entity){
        return sleepMap.getOrDefault(entity.getUniqueId(), false);
    }

    public void forceWakeUp(LivingEntity entity){
        sleepMap.remove(entity.getUniqueId());
        immobile.removeImmobile(entity);

    }
}
