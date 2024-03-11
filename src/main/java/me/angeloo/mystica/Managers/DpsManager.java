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


    private final Map<UUID, BukkitTask> savedTask = new HashMap<>();
    private final Map<UUID, Double> damageSlot = new HashMap<>();
    private final Map<UUID, LinkedList<Double>> allSaved = new HashMap<>();

    public DpsManager(Mystica main){
        this.main = main;
    }

    //divine by list length
    public double getRawDps(Player player){

        if(!allSaved.containsKey(player.getUniqueId())){
            return 0;
        }

        return getAllSaved(player) / (double) allSaved.get(player.getUniqueId()).size();

    }

    public int getRoundedDps(Player player){
        if(!allSaved.containsKey(player.getUniqueId())){
            return 0;
        }

        return (int) Math.round(getAllSaved(player) / (double) allSaved.get(player.getUniqueId()).size());
    }



    public void addToDamageDealt(Player player, double damage){

        double saved = damageSlot.getOrDefault(player.getUniqueId(), 0.0);

        saved = saved + damage;

        damageSlot.put(player.getUniqueId(), saved);

        startTask(player);
    }

    private void startTask(Player player){

        if(savedTask.containsKey(player.getUniqueId())){
            return;
        }

        BukkitTask task = new BukkitRunnable(){
            int ran = 0;
            @Override
            public void run(){
                addSaved(player, getSaved(player));
                clearSaved(player);

                if(ran>=10 && getAllSaved(player)==0.0){
                    this.cancel();
                    removeAllSaved(player);
                    savedTask.remove(player.getUniqueId());
                }

                Bukkit.getServer().getPluginManager().callEvent(new BoardValueUpdateEvent(player));

                ran++;
            }
        }.runTaskTimer(main, 20, 20);


        savedTask.put(player.getUniqueId(), task);
    }

    public double getAllSaved(Player player){

        if(!allSaved.containsKey(player.getUniqueId())){
            return 0.0;
        }

        LinkedList<Double> values = allSaved.get(player.getUniqueId());

        double sum = 0;
        for(Double value : values){
            sum+=value;
        }

        //Bukkit.getLogger().info(String.valueOf(sum));
        //Bukkit.getLogger().info(String.valueOf(values));

        return sum;
    }

    private void addSaved(Player player, double amount){

        LinkedList<Double> values = allSaved.getOrDefault(player.getUniqueId(), new LinkedList<>());

        values.add(amount);

        if(values.size()>3){
            values.removeFirst();
        }

        allSaved.put(player.getUniqueId(), values);
    }

    private void clearSaved(Player player){
        damageSlot.remove(player.getUniqueId());
    }

    private void removeAllSaved(Player player){
        allSaved.remove(player.getUniqueId());
    }

    private double getSaved(Player player){
        return damageSlot.getOrDefault(player.getUniqueId(), 0.0);
    }

    public void removeDps(Player player){
        if(savedTask.containsKey(player.getUniqueId())){
            savedTask.get(player.getUniqueId()).cancel();
        }

        allSaved.remove(player.getUniqueId());
        damageSlot.remove(player.getUniqueId());
    }

}
