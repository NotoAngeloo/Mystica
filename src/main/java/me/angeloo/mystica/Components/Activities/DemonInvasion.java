package me.angeloo.mystica.Components.Activities;

import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;

import static me.angeloo.mystica.Mystica.questColor;

public class DemonInvasion {

    private final Mystica main;
    private final ProfileManager profileManager;

    private final World world = Bukkit.getWorld("world");

    private final List<ChaosPortal> activePortals = new ArrayList<>();

    public DemonInvasion(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
    }

    public void spawnDemonPortals(){

        removeCurrentPortals();

        Bukkit.broadcastMessage(ChatColor.of(questColor) + "Chaos Portals have appeared in our world.");

        assert world != null;
        //future, have a list of potential spawn locs. pick them randomly, also selecting random difficulty. save them to a list of active portals

        Location spawnLoc = new Location(world, 284, 74, -251, 0, 0);

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
                        Entity portal;

                        try {
                            portal = MythicBukkit.inst().getAPIHelper().spawnMythicMob("DemonPortal", spawnLoc);
                        } catch (InvalidMobTypeException e) {
                            throw new RuntimeException(e);
                        }
                        removeNearbyEntities();
                        this.cancel();

                        TextDisplay portalText = world.spawn(spawnLoc.add(0,3,0), TextDisplay.class);

                        portalText.setText("Demon Invasion" +
                                "\n\n" +
                                "Difficulty: Easy" +
                                "\n\n" +
                                "Recommended Players: 1");
                        portalText.setCustomName("demon_portal_easy");

                        ChaosPortal activePortal = new ChaosPortal(spawnLoc, portal, portalText);

                        activePortals.add(activePortal);
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

    private void removeCurrentPortals(){
        for(ChaosPortal portal : activePortals){
            portal.despawn();
        }
        activePortals.clear();
    }

}
