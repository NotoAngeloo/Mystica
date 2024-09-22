package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
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
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public Warp(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        ChangeResourceHandler changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)){
            return;
        }

        combatManager.startCombatTimer(caster);

        double maxDistance = 8 + buffAndDebuffManager.getTotalRangeModifier(caster);

        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_4_Level_Bonus();

        maxDistance = maxDistance + ((int)(skillLevel/3));

        Location playerLoc = caster.getEyeLocation();
        Location newLoc = playerLoc.clone();

        String subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        if(subclass.equalsIgnoreCase("chaos")){
            caster.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, playerLoc, 50, .5, 1, .5, 0);
        }
        else{
            caster.getWorld().spawnParticle(Particle.FALLING_OBSIDIAN_TEAR, playerLoc, 50, .5, 1, .5, 0);
        }

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(target != null){
            double distance = caster.getLocation().distance(target.getLocation());

            if(distance <= maxDistance){
                caster.teleport(target);
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
        caster.teleport(newLoc);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 13);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    if(subclass.equalsIgnoreCase("chaos")){
                        cooldownDisplayer.displayCooldown(caster,4);
                    }
                    else{
                        cooldownDisplayer.displayCooldown(caster,5);
                    }
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);

                if(subclass.equalsIgnoreCase("chaos")){
                    cooldownDisplayer.displayCooldown(caster,4);
                }
                else{
                    cooldownDisplayer.displayCooldown(caster,5);
                }


            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    public double getCost(){
        return 5;
    }

    public int getCooldown(LivingEntity caster){

        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

    public boolean usable(LivingEntity caster){
        return getCooldown(caster) <= 0;
    }

}
