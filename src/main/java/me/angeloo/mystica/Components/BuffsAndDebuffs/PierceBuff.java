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

public class PierceBuff {

    private final Mystica main;

    private final Map<UUID, BukkitTask> removeBuffTaskMap = new HashMap<>();
    private final Map<UUID, Boolean> hasBuff = new HashMap<>();

    public PierceBuff(Mystica main){
        this.main = main;
    }

    public void applyBuff(LivingEntity entity){
        hasBuff.put(entity.getUniqueId(), true);

        if(removeBuffTaskMap.containsKey(entity.getUniqueId())){
            removeBuffTaskMap.get(entity.getUniqueId()).cancel();
        }

        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
        }

        BukkitTask task = new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(count >= 10){
                    this.cancel();
                    removeBuff(entity);
                }

                count++;
            }
        }.runTaskTimer(main, 0, 1);

        removeBuffTaskMap.put(entity.getUniqueId(), task);
    }

    //task to remove debuff instead

    public boolean getIfPierceBuff(LivingEntity entity){
        return hasBuff.getOrDefault(entity.getUniqueId(), false);
    }

    public void removeBuff(LivingEntity entity){
        hasBuff.remove(entity.getUniqueId());

        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
        }

    }

}
