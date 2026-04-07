package me.angeloo.mystica.Components.CombatSystem.Abilities.Mystic;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.Hud.CooldownDisplayer;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.SubClass;
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
    private final TargetManager targetManager;
    private final StatusEffectManager statusEffectManager;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public Warp(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        statusEffectManager = main.getStatusEffectManager();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)){
            return;
        }

        double maxDistance = 8 + statusEffectManager.getAdditionalRange(caster);

        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_4_Level_Bonus();

        maxDistance = maxDistance + ((int)(skillLevel/3));

        Location playerLoc = caster.getEyeLocation();
        Location newLoc = playerLoc.clone();

        SubClass subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        if(subclass.equals(SubClass.Chaos)){
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
                    if(subclass.equals(SubClass.Chaos)){
                        cooldownDisplayer.displayCooldown(caster,4);
                    }
                    else{
                        cooldownDisplayer.displayCooldown(caster,5);
                    }
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - statusEffectManager.getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);

                if(subclass.equals(SubClass.Chaos)){
                    cooldownDisplayer.displayCooldown(caster,4);
                }
                else{
                    cooldownDisplayer.displayCooldown(caster,5);
                }


            }
        }.runTaskTimerAsynchronously(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

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
