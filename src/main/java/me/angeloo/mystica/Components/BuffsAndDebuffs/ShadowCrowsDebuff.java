package me.angeloo.mystica.Components.BuffsAndDebuffs;


import me.angeloo.mystica.Mystica;
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

        BukkitTask task = new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(count >= time){
                    this.cancel();
                    hasDebuff.remove(entity.getUniqueId());
                }

                count++;
            }
        }.runTaskTimer(main, 0, 20);

        removeDebuffTaskMap.put(entity.getUniqueId(), task);
    }

    //task to remove debuff instead

    public double getIncreasedDamageAmount(LivingEntity entity){
        if(hasDebuff.containsKey(entity.getUniqueId())){
            if(hasDebuff.get(entity.getUniqueId())){
                return 1.1;
            }
        }

        return 0;
    }

    public void removeCrowsDebuff(Player player){
        hasDebuff.remove(player.getUniqueId());
        //Bukkit.getLogger().info("remove buff");
    }

}
