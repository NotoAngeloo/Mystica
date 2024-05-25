package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.CustomEvents.StatusUpdateEvent;
import me.angeloo.mystica.Managers.BuffAndDebuffManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Immobile {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final Stun stun;
    private final Sleep sleep;

    private final Map<UUID, BukkitTask> removeImmobileTaskMap = new HashMap<>();
    private final Map<UUID, Boolean> immobileMap = new HashMap<>();
    private final Map<UUID, BukkitTask> removeMap = new HashMap<>();

    public Immobile(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        stun = new Stun(main, this);
        sleep = new Sleep(main, this);
    }

    public void applyImmobile(LivingEntity entity, int time){
        if(!profileManager.getAnyProfile(entity).getIsMovable()){
            return;
        }

        if(profileManager.getAnyProfile(entity).getIfObject()){
           return;
        }

        immobileMap.put(entity.getUniqueId(), true);

        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
        }

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
                    removeImmobile(entity);
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


        if(removeMap.containsKey(entity.getUniqueId())){
            removeMap.get(entity.getUniqueId()).cancel();
        }

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(!stun.getIfStun(entity) && !sleep.getIfSleep(entity)){
                    immobileMap.remove(entity.getUniqueId());
                    if(!(entity instanceof Player)){

                        if(!profileManager.getAnyProfile(entity).getIfDead()){
                            entity.setAI(true);
                        }

                    }

                    if(entity instanceof Player){
                        Player player = (Player) entity;
                        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
                    }
                    this.cancel();
                }

            }
        }.runTaskTimer(main, 0, 1);

        removeMap.put(entity.getUniqueId(), task);

    }

    public Stun getStun(){return stun;}
    public Sleep getSleep(){return sleep;}

}
