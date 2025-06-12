package me.angeloo.mystica.Components.BuffsAndDebuffs;


import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShadowCrowsDebuff {

    private final Mystica main;

    private final Map<UUID, BukkitTask> removeDebuffTaskMap = new HashMap<>();
    private final Map<UUID, Boolean> hasDebuff = new HashMap<>();

    public ShadowCrowsDebuff(Mystica main){
        this.main = main;
    }

    public void applyDebuff(LivingEntity entity, int time){
        hasDebuff.put(entity.getUniqueId(), true);

        if(removeDebuffTaskMap.containsKey(entity.getUniqueId())){
            removeDebuffTaskMap.get(entity.getUniqueId()).cancel();
        }

        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status"));
        }

        BukkitTask task = new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(count >= time){
                    this.cancel();
                    removeCrowsDebuff(entity);
                }

                count++;
            }
        }.runTaskTimer(main, 0, 1);

        removeDebuffTaskMap.put(entity.getUniqueId(), task);
    }

    //task to remove debuff instead

    public double getIncreasedDamageAmount(LivingEntity entity){
        if(hasDebuff.containsKey(entity.getUniqueId())){
            if(hasDebuff.get(entity.getUniqueId())){
                return 0.1;
            }
        }

        return 0;
    }

    public void removeCrowsDebuff(LivingEntity entity){
        hasDebuff.remove(entity.getUniqueId());

        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status"));
        }

    }

}
