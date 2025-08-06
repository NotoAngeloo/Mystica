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

public class ConcoctionDebuff {

    private final Mystica main;

    private final Map<UUID, BukkitTask> removeDebuffTaskMap = new HashMap<>();
    private final Map<UUID, Boolean> hasDebuff = new HashMap<>();

    public ConcoctionDebuff(Mystica main){
        this.main = main;
    }

    public void applyDebuff(LivingEntity entity){
        hasDebuff.put(entity.getUniqueId(), true);

        if(removeDebuffTaskMap.containsKey(entity.getUniqueId())){
            removeDebuffTaskMap.get(entity.getUniqueId()).cancel();
        }

        if(entity instanceof Player player){
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Status));
        }

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){
                removeDebuff(entity);
            }
        }.runTaskLaterAsynchronously(main, 15*20);

        removeDebuffTaskMap.put(entity.getUniqueId(), task);
    }

    //task to remove debuff instead

    public double getIncreasedDamageAmount(LivingEntity entity){
        if(hasDebuff.containsKey(entity.getUniqueId())){
            if(hasDebuff.get(entity.getUniqueId())){
                return 0.05;
            }
        }

        return 0;
    }

    public void removeDebuff(LivingEntity entity){
        hasDebuff.remove(entity.getUniqueId());

        if(entity instanceof Player){
            Bukkit.getScheduler().runTask(main,()->{
                Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent((Player)entity, BarType.Status));
            });

        }

    }

}
