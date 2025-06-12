package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArmorBreak {

    private final Mystica main;
    private final Map<UUID, Integer> stacks = new HashMap<>();
    private final Map<UUID, Integer> timeLeft = new HashMap<>();
    private final Map<UUID, BukkitTask> removeTaskMap = new HashMap<>();

    public ArmorBreak(Mystica main){
        this.main = main;
    }

    public void applyArmorBreak(LivingEntity entity){

        int stack = getStacks(entity);

        stack++;

        if(stack == 3){
            Location center = entity.getLocation();

            BoundingBox hitBox = new BoundingBox(
                    center.getX() - 20,
                    center.getY() - 20,
                    center.getZ() - 20,
                    center.getX() + 20,
                    center.getY() + 20,
                    center.getZ() + 20
            );

            for (Entity thisEntity : entity.getWorld().getNearbyEntities(hitBox)) {

                if(!(thisEntity instanceof Player)){
                    continue;
                }

                Player player = (Player) thisEntity;

                player.sendMessage(entity.getName() + "'s armor has shattered. Secondary tank, take aggro!");
            }
        }

        stacks.put(entity.getUniqueId(), stack);
        timeLeft.put(entity.getUniqueId(), getDuration());

        if(removeTaskMap.containsKey(entity.getUniqueId())){
            removeTaskMap.get(entity.getUniqueId()).cancel();
        }

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                int time = getTimeLeft(entity);

                time--;

                timeLeft.put(entity.getUniqueId(), time);

                if(entity instanceof Player){
                    Player player = (Player) entity;
                    Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status"));
                }

                if(time<= 0){
                    this.cancel();
                    removeArmorBreak(entity);
                }


            }
        }.runTaskTimer(main, 20, 20);


        removeTaskMap.put(entity.getUniqueId(), task);

    }

    public int getStacks(LivingEntity entity){
        return stacks.getOrDefault(entity.getUniqueId(), 0);
    }

    public int getTimeLeft(LivingEntity entity){
        return timeLeft.getOrDefault(entity.getUniqueId(), 0);
    }

    public void removeArmorBreak(LivingEntity entity){
        stacks.remove(entity.getUniqueId());
        timeLeft.remove(entity.getUniqueId());
        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status"));
        }
    }

    public int getDuration(){
        return 10;
    }

}
