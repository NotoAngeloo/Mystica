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

    public void applyBuff(Player player){
        buffActiveMap.put(player.getUniqueId(), 11);

        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));

        new BukkitRunnable(){
            @Override
            public void run(){

                Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));

                if(buffActiveMap.get(player.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int left = buffActiveMap.get(player.getUniqueId()) - 1;

                buffActiveMap.put(player.getUniqueId(), left);
            }
        }.runTaskTimer(main, 0, 20);

    }


    public int getIfBuffTime(Player player){
        return buffActiveMap.getOrDefault(player.getUniqueId(), 0);
    }


    public void removeBuff(LivingEntity entity){
        buffActiveMap.remove(entity.getUniqueId());

        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
        }

    }

}
