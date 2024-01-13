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

public class GenericDamageReduction {

    private final Mystica main;
    private final Map<UUID, Double> reductionAmount = new HashMap<>();
    private final Map<UUID, BukkitTask> removeReduction = new HashMap<>();

    //LOWER IS BETTER

    public GenericDamageReduction(Mystica main){
        this.main = main;
    }

    public void applyDamageReduction(LivingEntity entity, double amount, int time){

        double currentAmount = getReduction(entity);

        if(amount > currentAmount){
            return;
        }

        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
        }

        if(removeReduction.containsKey(entity.getUniqueId())){
            removeReduction.get(entity.getUniqueId()).cancel();
        }

        if(time == 0){
            return;
        }

        reductionAmount.put(entity.getUniqueId(), amount);

        BukkitTask task = new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(count >= time){
                    this.cancel();
                    removeReduction(entity);
                }

                count++;
            }
        }.runTaskTimer(main, 0, 1);

        removeReduction.put(entity.getUniqueId(), task);


    }

    public double getReduction(LivingEntity entity){
        return reductionAmount.getOrDefault(entity.getUniqueId(), 1.0);
    }

    public void removeReduction(LivingEntity entity){
        reductionAmount.remove(entity.getUniqueId());
        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
        }
    }
}
