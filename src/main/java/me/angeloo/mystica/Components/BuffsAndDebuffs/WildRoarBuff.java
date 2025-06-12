package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WildRoarBuff {

    private final Mystica main;

    private final Map<UUID, Integer> buffTimeMap = new HashMap<>();
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

        buffTimeMap.put(entity.getUniqueId(), getDuration());
        multiplierMap.put(entity.getUniqueId(), multiplier);


        if(removeBuffTaskMap.containsKey(entity.getUniqueId())){
            removeBuffTaskMap.get(entity.getUniqueId()).cancel();
        }

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                int buffTime = getBuffTime(entity);

                buffTime--;

                buffTimeMap.put(entity.getUniqueId(), buffTime);

                if(buffTime <= 0){
                    this.cancel();
                    multiplierMap.remove(entity.getUniqueId());
                    buffTimeMap.remove(entity.getUniqueId());
                }

                if(entity instanceof Player){
                    Player player = (Player) entity;
                    Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status"));
                }

            }
        }.runTaskTimer(main, 0, 20);

        removeBuffTaskMap.put(entity.getUniqueId(), task);
    }

    public int getBuffTime(LivingEntity entity){
        return buffTimeMap.getOrDefault(entity.getUniqueId(), 0);
    }

    public int getDuration(){
        return 11;
    }

    public double getMultiplier(LivingEntity entity){
        return multiplierMap.getOrDefault(entity.getUniqueId(), 0.0);
    }

    public void removeBuff(LivingEntity entity){
        buffTimeMap.remove(entity.getUniqueId());
        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status"));
        }
    }

}
