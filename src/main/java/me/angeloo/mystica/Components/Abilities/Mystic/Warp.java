package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Warp {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public Warp(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        combatManager.startCombatTimer(player);

        double maxDistance = 8 + buffAndDebuffManager.getTotalRangeModifier(player);

        Location playerLoc = player.getEyeLocation();
        Location newLoc = playerLoc.clone();

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        if(subclass.equalsIgnoreCase("chaos")){
            player.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, playerLoc, 50, .5, 1, .5, 0);
        }
        else{
            player.getWorld().spawnParticle(Particle.FALLING_OBSIDIAN_TEAR, playerLoc, 50, .5, 1, .5, 0);
        }

        LivingEntity target = targetManager.getPlayerTarget(player);

        if(target != null){
            double distance = player.getLocation().distance(target.getLocation());

            if(distance <= maxDistance){
                player.teleport(target);
                return;
            }
        }


        Vector direction = playerLoc.getDirection().normalize();

        while (maxDistance > 0) {
            newLoc.add(direction);
            if (!newLoc.getBlock().isPassable()) {
                newLoc.subtract(direction.multiply(2));
                break;
            }
            maxDistance -= 1;
        }

        newLoc.setY(newLoc.getY());
        player.teleport(newLoc);

        abilityReadyInMap.put(player.getUniqueId(), 13);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);

            }
        }.runTaskTimer(main, 0,20);

    }

    public int getCooldown(Player player){

        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }


}