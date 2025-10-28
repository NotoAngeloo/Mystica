package me.angeloo.mystica.Components.Creatures;

import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;

public class ArchbishopNpc {

    private final Mystica main;
    private final ProfileManager profileManager;
    private BukkitTask spawnTask;

    public ArchbishopNpc(Mystica main, ProfileManager profileManager){
        this.main = main;
        this.profileManager = profileManager;
    }

    public void spawn() throws InvalidMobTypeException {

        World world = Bukkit.getWorld("world");
        assert world != null;

        Location spawnLoc = new Location(world, -11.5, 127, -251.5, 180, 0);


        this.spawnTask = new BukkitRunnable(){
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

                Bukkit.getScheduler().runTask(main, ()->{
                    for(Entity entity : world.getNearbyEntities(hitBox)){
                        if(entity instanceof Player){
                            try {
                                MythicBukkit.inst().getAPIHelper().spawnMythicMob("ArchbishopNpc", spawnLoc);
                            } catch (InvalidMobTypeException e) {
                                throw new RuntimeException(e);
                            }

                            removeNearbyEntities();
                            this.cancel();
                        }
                    }
                });



            }

            private void removeNearbyEntities(){

                for(Entity entity : world.getNearbyEntities(spawnLoc, 2, 2, 2)){
                    if(entity instanceof Player){
                        continue;
                    }

                    if(!(entity instanceof LivingEntity livingEntity)){
                        continue;
                    }

                    if(!profileManager.getAnyProfile(livingEntity).getImmortality()){
                        livingEntity.remove();
                    }
                }

            }

        }.runTaskTimerAsynchronously(main, 0, 20);
    }

    public BukkitTask getSpawnTask(){
        return this.spawnTask;
    }
}
