package me.angeloo.mystica.Managers;

import me.angeloo.mystica.CustomEvents.BoardValueUpdateEvent;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class DpsManager {

    private final Mystica main;

    private final int dpsTime = 20000;

    private final Map<UUID, Long> lastAdded = new HashMap<>();
    private final Map<UUID, Double> totalDamage = new HashMap<>();
    private final Map<UUID, Integer> totalTime = new HashMap<>();
    private final Map<UUID, BukkitTask> savedTask = new HashMap<>();


    public DpsManager(Mystica main){
        this.main = main;
    }

    public double getRawDps(LivingEntity entity){

        if(!totalDamage.containsKey(entity.getUniqueId())){
            return 0;
        }

        return getSaved(entity) / getTime(entity);

    }

    public int getRoundedDps(LivingEntity entity){

        //Bukkit.getLogger().info("Calculation Result of " + getSaved(player) + " / " + getTime(player));
        //Bukkit.getLogger().info("Result: " + (int) Math.round(getSaved(player)) / getTime(player));
        return (int) (Math.round(getSaved(entity) / getTime(entity)));
    }



    public void addToDamageDealt(LivingEntity entity, double damage){

        double saved = totalDamage.getOrDefault(entity.getUniqueId(), 0.0);
        saved = saved + damage;
        totalDamage.put(entity.getUniqueId(), saved);

        startTask(entity);
        lastAdded.put(entity.getUniqueId(), System.currentTimeMillis());
    }

    private void addTime(LivingEntity entity){

        int time = getTime(entity);
        time+=1;
        totalTime.put(entity.getUniqueId(), time);

    }

    private void startTask(LivingEntity entity){

        if(savedTask.containsKey(entity.getUniqueId())){
            return;
        }

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                addTime(entity);

                long currentTime = System.currentTimeMillis();
                long lastAdded = getLastAdded(entity);

                if(currentTime - lastAdded >= dpsTime){
                    this.cancel();
                    removeDps(entity);
                }

                if(entity instanceof Player){
                    Bukkit.getServer().getPluginManager().callEvent(new BoardValueUpdateEvent((Player)entity));
                }



            }
        }.runTaskTimer(main, 20, 20);


        savedTask.put(entity.getUniqueId(), task);
    }

    private int getTime(LivingEntity entity){
        return totalTime.getOrDefault(entity.getUniqueId(), 1);
    }

    private double getSaved(LivingEntity entity){
        return totalDamage.getOrDefault(entity.getUniqueId(), 0.0);
    }

    public void removeDps(LivingEntity entity){
        if(savedTask.containsKey(entity.getUniqueId())){
            savedTask.get(entity.getUniqueId()).cancel();
        }

        savedTask.remove(entity.getUniqueId());
        totalDamage.remove(entity.getUniqueId());
        totalTime.remove(entity.getUniqueId());
    }

    private long getLastAdded(LivingEntity entity){

        if(!lastAdded.containsKey(entity.getUniqueId())){
            lastAdded.put(entity.getUniqueId(), System.currentTimeMillis());
        }

        return lastAdded.get(entity.getUniqueId());
    }

}
