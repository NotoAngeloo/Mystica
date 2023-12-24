package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.Mystica;
import org.bukkit.entity.LivingEntity;
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

        if(time == 0){
            return;
        }

        BukkitTask task = new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(count >= time){
                    this.cancel();
                    silenceMap.remove(entity.getUniqueId());
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
    }

}
