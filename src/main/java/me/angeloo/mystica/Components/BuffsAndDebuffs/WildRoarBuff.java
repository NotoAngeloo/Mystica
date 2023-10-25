package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.Mystica;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WildRoarBuff {

    private final Mystica main;

    private final Map<UUID, Boolean> hasWildRoarBuffMap = new HashMap<>();
    private final Map<UUID, Double> multiplierMap = new HashMap<>();

    private final Map<UUID, BukkitTask> removeBuffTaskMap = new HashMap<>();

    public WildRoarBuff(Mystica main){
        this.main = main;
    }

    public void applyBuff(LivingEntity entity, double multiplier){

        double currentMultiplier = getMultiplier(entity);

        if(multiplier < currentMultiplier){
            return;
        }

        hasWildRoarBuffMap.put(entity.getUniqueId(), true);
        multiplierMap.put(entity.getUniqueId(), multiplier);

        if(removeBuffTaskMap.containsKey(entity.getUniqueId())){
            removeBuffTaskMap.get(entity.getUniqueId()).cancel();
        }

        BukkitTask task = new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(count >= 10){
                    this.cancel();
                    hasWildRoarBuffMap.remove(entity.getUniqueId());
                    multiplierMap.remove(entity.getUniqueId());
                }

                count++;
            }
        }.runTaskTimer(main, 0, 20);

        removeBuffTaskMap.put(entity.getUniqueId(), task);
    }

    public boolean getIfWildRoarBuff(LivingEntity entity){
        return hasWildRoarBuffMap.getOrDefault(entity.getUniqueId(), false);
    }

    public double getMultiplier(LivingEntity entity){
        return multiplierMap.getOrDefault(entity.getUniqueId(), 0.0);
    }

    public void removeBuff(Player player){
        hasWildRoarBuffMap.remove(player.getUniqueId());
    }

}
