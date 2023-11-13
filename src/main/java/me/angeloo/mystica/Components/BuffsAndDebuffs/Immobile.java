package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Immobile {

    private final Mystica main;
    private final ProfileManager profileManager;

    private final Map<UUID, BukkitTask> removeImmobileTaskMap = new HashMap<>();
    private final Map<UUID, Boolean> immobileMap = new HashMap<>();

    public Immobile(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
    }

    public void applyImmobile(LivingEntity entity, int time){
        if(!profileManager.getAnyProfile(entity).getIsMovable()){
            return;
        }

        if(profileManager.getAnyProfile(entity).getIfObject()){
           return;
        }

        immobileMap.put(entity.getUniqueId(), true);

        if(removeImmobileTaskMap.containsKey(entity.getUniqueId())){
            removeImmobileTaskMap.get(entity.getUniqueId()).cancel();
        }

        if(!(entity instanceof Player)){
                entity.setAI(false);
        }

        if(time == 0){
            return;
        }

        BukkitTask task = new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(count >= time){
                    this.cancel();
                    immobileMap.remove(entity.getUniqueId());

                    if(!(entity instanceof Player)){
                        entity.setAI(true);
                    }

                }

                count++;
            }
        }.runTaskTimer(main, 0, 1);

        removeImmobileTaskMap.put(entity.getUniqueId(), task);
    }

    public boolean getImmobile(LivingEntity entity){
        return immobileMap.getOrDefault(entity.getUniqueId(), false);
    }

    public void removeImmobile(LivingEntity entity){
        immobileMap.remove(entity.getUniqueId());

        if(!(entity instanceof Player)){
            entity.setAI(true);
        }

    }

}
