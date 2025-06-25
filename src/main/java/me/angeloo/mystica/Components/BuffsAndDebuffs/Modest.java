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

public class Modest {

    private final Mystica main;

    private final Map<UUID, BukkitTask> removeModestTaskMap = new HashMap<>();
    private final Map<UUID, Double> modestMap = new HashMap<>();

    public Modest(Mystica main){
        this.main = main;
    }

    public void apply(LivingEntity entity, double multiplier, int time){

        double currentMultiplier = getMultiplier(entity);

        if(multiplier < currentMultiplier){
            return;
        }

        modestMap.put(entity.getUniqueId(), multiplier);

        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status", false));
        }

        if(removeModestTaskMap.containsKey(entity.getUniqueId())){
            removeModestTaskMap.get(entity.getUniqueId()).cancel();
        }

        BukkitTask task = new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(count >= time){
                    this.cancel();
                    removeModest(entity);
                }

                count++;
            }
        }.runTaskTimer(main, 0, 1);

        removeModestTaskMap.put(entity.getUniqueId(), task);
    }

    public double getMultiplier(LivingEntity entity){
        return modestMap.getOrDefault(entity.getUniqueId(), 0.0);
    }

    public void removeModest(LivingEntity entity){
        modestMap.remove(entity.getUniqueId());

        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status", false));
        }

    }

}
