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

public class Haste {

    private final Mystica main;

    private final Map<UUID, Integer> hasteLevel = new HashMap<>();
    private final Map<UUID, BukkitTask> removeHasteTaskMap = new HashMap<>();

    public Haste(Mystica main){
        this.main = main;
    }

    public void applyHaste(LivingEntity entity, int level, int duration){

        int currentLevel = getHasteLevel(entity);

        if(currentLevel > level) {
            return;
        }

        hasteLevel.put(entity.getUniqueId(), level);

        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status", false));
        }

        if(removeHasteTaskMap.containsKey(entity.getUniqueId())){
            removeHasteTaskMap.get(entity.getUniqueId()).cancel();
        }

        BukkitTask task = new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(count >= duration){
                    this.cancel();
                    removeHaste(entity);
                }

                count++;
            }
        }.runTaskTimerAsynchronously(main, 0, 1);


        removeHasteTaskMap.put(entity.getUniqueId(), task);
    }

    public int getHasteLevel(LivingEntity entity){
        return hasteLevel.getOrDefault(entity.getUniqueId(), 0);
    }

    public void removeHaste(LivingEntity entity){
        hasteLevel.remove(entity.getUniqueId());

        if(entity instanceof Player){
            Bukkit.getScheduler().runTask(main,()->{
                Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent((Player)entity, "status", false));
            });
        }

    }

    public boolean getIfHaste(Player player){
        return getHasteLevel(player) > 0;
    }

}
