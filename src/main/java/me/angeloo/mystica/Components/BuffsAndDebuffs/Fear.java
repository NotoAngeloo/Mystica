package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.BarType;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Fear {

    private final Mystica main;

    private final Map<UUID, BukkitTask> removeFearTaskMap = new HashMap<>();
    private final Map<UUID, Boolean> fearMap = new HashMap<>();

    public Fear(Mystica main){
        this.main = main;
    }

    public void applyFear(LivingEntity entity, int time){
        fearMap.put(entity.getUniqueId(), true);

        if(removeFearTaskMap.containsKey(entity.getUniqueId())){
            removeFearTaskMap.get(entity.getUniqueId()).cancel();
        }

        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Status, false));
        }

        if(time == 0){
            return;
        }

        Vector direction = entity.getLocation().getDirection().normalize().multiply(0.2);

        int angle = (int) (Math.random() * 360);
        double radians = Math.toRadians(angle);

        direction.rotateAroundY(radians);
        direction.setY(0);

        BukkitTask task = new BukkitRunnable(){

            int count = 0;
            @Override
            public void run(){

                //also move them around
                entity.setVelocity(direction);

                if(count >= time){
                    this.cancel();
                    removeFear(entity);
                }

                count++;
            }
        }.runTaskTimerAsynchronously(main, 0, 1);

        removeFearTaskMap.put(entity.getUniqueId(), task);
    }

    public boolean getFear(LivingEntity entity){
        return fearMap.getOrDefault(entity.getUniqueId(), false);
    }

    public void removeFear(LivingEntity entity){
        fearMap.remove(entity.getUniqueId());
        if(entity instanceof Player){
            Bukkit.getScheduler().runTask(main,()->{
                Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent((Player)entity, BarType.Status, false));
            });
        }
    }

}
