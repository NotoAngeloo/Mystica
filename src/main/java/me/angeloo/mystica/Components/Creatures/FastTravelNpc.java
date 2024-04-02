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

public class FastTravelNpc {

    private final Mystica main;
    private final ProfileManager profileManager;

    public FastTravelNpc(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
    }

    public void spawnAll() {
        spawn1();
        spawn2();
        spawn3();
        spawn4();
    }

    private void spawn1(){

        World world = Bukkit.getWorld("world");
        assert world != null;

        Location spawnLoc = new Location(world, 70, 99, -361, 40, 0);

        new BukkitRunnable(){
            @Override
            public void run(){

                BoundingBox hitBox = new BoundingBox(
                        spawnLoc.getX() - 20,
                        spawnLoc.getY() - 20,
                        spawnLoc.getZ() - 20,
                        spawnLoc.getX() + 20,
                        spawnLoc.getY() + 20,
                        spawnLoc.getZ() + 20
                );

                for(Entity entity : world.getNearbyEntities(hitBox)){
                    if(entity instanceof Player){
                        try {
                            MythicBukkit.inst().getAPIHelper().spawnMythicMob("FastTravelNpc", spawnLoc);
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

    private void spawn2(){

        World world = Bukkit.getWorld("world");
        assert world != null;

        Location spawnLoc = new Location(world, 554, 93, -119, 40, 0);

        new BukkitRunnable(){
            @Override
            public void run(){

                BoundingBox hitBox = new BoundingBox(
                        spawnLoc.getX() - 20,
                        spawnLoc.getY() - 20,
                        spawnLoc.getZ() - 20,
                        spawnLoc.getX() + 20,
                        spawnLoc.getY() + 20,
                        spawnLoc.getZ() + 20
                );

                for(Entity entity : world.getNearbyEntities(hitBox)){
                    if(entity instanceof Player){
                        try {
                            MythicBukkit.inst().getAPIHelper().spawnMythicMob("FastTravelNpc", spawnLoc);
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

    private void spawn3(){

        World world = Bukkit.getWorld("world");
        assert world != null;

        Location spawnLoc = new Location(world, -119, 69, 106, -50, 0);

        new BukkitRunnable(){
            @Override
            public void run(){

                BoundingBox hitBox = new BoundingBox(
                        spawnLoc.getX() - 20,
                        spawnLoc.getY() - 20,
                        spawnLoc.getZ() - 20,
                        spawnLoc.getX() + 20,
                        spawnLoc.getY() + 20,
                        spawnLoc.getZ() + 20
                );

                for(Entity entity : world.getNearbyEntities(hitBox)){
                    if(entity instanceof Player){
                        try {
                            MythicBukkit.inst().getAPIHelper().spawnMythicMob("FastTravelNpc", spawnLoc);
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

    private void spawn4(){

        World world = Bukkit.getWorld("world");
        assert world != null;

        Location spawnLoc = new Location(world, -103, 71, 381, -120, 0);

        new BukkitRunnable(){
            @Override
            public void run(){

                BoundingBox hitBox = new BoundingBox(
                        spawnLoc.getX() - 20,
                        spawnLoc.getY() - 20,
                        spawnLoc.getZ() - 20,
                        spawnLoc.getX() + 20,
                        spawnLoc.getY() + 20,
                        spawnLoc.getZ() + 20
                );

                for(Entity entity : world.getNearbyEntities(hitBox)){
                    if(entity instanceof Player){
                        try {
                            MythicBukkit.inst().getAPIHelper().spawnMythicMob("FastTravelNpc", spawnLoc);
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
