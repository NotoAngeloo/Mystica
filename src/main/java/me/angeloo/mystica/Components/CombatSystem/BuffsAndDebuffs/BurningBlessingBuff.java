package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs;

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

public class BurningBlessingBuff {

    private final Mystica main;

    private final Map<UUID, Boolean> hasHealthMap = new HashMap<>();
    private final Map<UUID, Double> healthAmountMap = new HashMap<>();
    private final Map<UUID, BukkitTask> removeHealthBuffTaskMap = new HashMap<>();

    public BurningBlessingBuff(Mystica main){
        this.main = main;
    }

    public void applyHealthBuff(LivingEntity entity, double multiplier){

        double currentMultiplier = getHealthMultiplier(entity);

        if(multiplier < currentMultiplier){
            return;
        }

        hasHealthMap.put(entity.getUniqueId(), true);
        healthAmountMap.put(entity.getUniqueId(), multiplier);

        if(entity instanceof Player player){
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Status));
        }

        if(removeHealthBuffTaskMap.containsKey(entity.getUniqueId())){
            removeHealthBuffTaskMap.get(entity.getUniqueId()).cancel();
        }

        BukkitTask task = new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(count >= 7){
                    this.cancel();
                    removeHealthBuff(entity);
                }

                count++;
            }
        }.runTaskTimerAsynchronously(main, 0, 20);

        removeHealthBuffTaskMap.put(entity.getUniqueId(), task);
    }

    public double getHealthMultiplier(LivingEntity entity){
        return healthAmountMap.getOrDefault(entity.getUniqueId(), 0.0);
    }

    public boolean getIfHealthBuff(LivingEntity entity){
        return hasHealthMap.getOrDefault(entity.getUniqueId(), false);
    }

    public void removeHealthBuff(LivingEntity entity){
        healthAmountMap.remove(entity.getUniqueId());
        hasHealthMap.remove(entity.getUniqueId());
        if(entity instanceof Player){
            Bukkit.getScheduler().runTask(main,()->{
                Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(entity, BarType.Status));
            });

        }
    }

}
