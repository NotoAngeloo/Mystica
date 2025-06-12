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

    private final Map<UUID, Integer> buffActiveMap = new HashMap<>();

    public PierceBuff(Mystica main){
        this.main = main;
    }

    public void applyBuff(LivingEntity entity){
        buffActiveMap.put(entity.getUniqueId(), getDuration());

        if(entity instanceof Player){
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent((Player) entity));
        }


        new BukkitRunnable(){
            @Override
            public void run(){

                if(entity instanceof Player){
                    Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent((Player) entity));
                }


                if(buffActiveMap.get(entity.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int left = buffActiveMap.get(entity.getUniqueId()) - 1;

                buffActiveMap.put(entity.getUniqueId(), left);

                if(entity instanceof Player){
                    Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent((Player) entity));
                }

            }
        }.runTaskTimer(main, 0, 20);

    }


    public int getIfBuffTime(LivingEntity entity){
        return buffActiveMap.getOrDefault(entity.getUniqueId(), 0);
    }

    public int getDuration(){
        return 21;
    }

    public void removeBuff(LivingEntity entity){
        buffActiveMap.remove(entity.getUniqueId());

        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
        }

    }

}
