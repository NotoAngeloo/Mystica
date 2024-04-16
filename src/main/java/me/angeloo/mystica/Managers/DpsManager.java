package me.angeloo.mystica.Managers;

import me.angeloo.mystica.CustomEvents.BoardValueUpdateEvent;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
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

    public double getRawDps(Player player){

        if(!totalDamage.containsKey(player.getUniqueId())){
            return 0;
        }

        return getSaved(player) / getTime(player);

    }

    public int getRoundedDps(Player player){

        //Bukkit.getLogger().info("Calculation Result of " + getSaved(player) + " / " + getTime(player));
        //Bukkit.getLogger().info("Result: " + (int) Math.round(getSaved(player)) / getTime(player));
        return (int) (Math.round(getSaved(player) / getTime(player)));
    }



    public void addToDamageDealt(Player player, double damage){

        double saved = totalDamage.getOrDefault(player.getUniqueId(), 0.0);
        saved = saved + damage;
        totalDamage.put(player.getUniqueId(), saved);

        startTask(player);
        lastAdded.put(player.getUniqueId(), System.currentTimeMillis());
    }

    private void addTime(Player player){

        int time = getTime(player);
        time+=1;
        totalTime.put(player.getUniqueId(), time);

    }

    private void startTask(Player player){

        if(savedTask.containsKey(player.getUniqueId())){
            return;
        }

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                addTime(player);

                long currentTime = System.currentTimeMillis();
                long lastAdded = getLastAdded(player);

                if(currentTime - lastAdded >= dpsTime){
                    this.cancel();
                    removeDps(player);
                }

                Bukkit.getServer().getPluginManager().callEvent(new BoardValueUpdateEvent(player));

            }
        }.runTaskTimer(main, 20, 20);


        savedTask.put(player.getUniqueId(), task);
    }

    private int getTime(Player player){
        return totalTime.getOrDefault(player.getUniqueId(), 1);
    }

    private double getSaved(Player player){
        return totalDamage.getOrDefault(player.getUniqueId(), 0.0);
    }

    public void removeDps(Player player){
        if(savedTask.containsKey(player.getUniqueId())){
            savedTask.get(player.getUniqueId()).cancel();
        }

        savedTask.remove(player.getUniqueId());
        totalDamage.remove(player.getUniqueId());
        totalTime.remove(player.getUniqueId());
    }

    private long getLastAdded(Player player){

        if(!lastAdded.containsKey(player.getUniqueId())){
            lastAdded.put(player.getUniqueId(), System.currentTimeMillis());
        }

        return lastAdded.get(player.getUniqueId());
    }

}
