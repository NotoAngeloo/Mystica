package me.angeloo.mystica.Components.Creatures;

import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

public class HansNpc {

    private final Mystica main;
    private final ProfileManager profileManager;

    public HansNpc(Mystica main, ProfileManager profileManager){
        this.main = main;
        this.profileManager = profileManager;
    }

    public void spawn() throws InvalidMobTypeException {

        World world = Bukkit.getWorld("world");
        assert world != null;

        Location spawnLoc = new Location(world, 9.5, 125, -332, -90, 0);


        new BukkitRunnable(){
            @Override
            public void run(){

                BoundingBox hitBox = new BoundingBox(
                        spawnLoc.getX() - 50,
                        spawnLoc.getY() - 50,
                        spawnLoc.getZ() - 50,
                        spawnLoc.getX() + 50,
                        spawnLoc.getY() + 50,
                        spawnLoc.getZ() + 50
                );

                for(Entity entity : world.getNearbyEntities(hitBox)){
                    if(entity instanceof Player){
                        try {
                            MythicBukkit.inst().getAPIHelper().spawnMythicMob("HansNpc", spawnLoc);
                        } catch (InvalidMobTypeException e) {
                            throw new RuntimeException(e);
                        }
                        removeNearbyEntities();
                        this.cancel();
                    }
                }

            }

            private void removeNearbyEntities(){

                for(Entity entity : world.getNearbyEntities(spawnLoc, 2, 2, 2)){
                    if(entity instanceof Player){
                        continue;
                    }

                    if(!(entity instanceof LivingEntity)){
                        continue;
                    }

                    LivingEntity livingEntity = (LivingEntity) entity;

                    if(!profileManager.getAnyProfile(livingEntity).getImmortality()){
                        livingEntity.remove();
                    }
                }

            }

        }.runTaskTimer(main, 0, 20);

    }

}
