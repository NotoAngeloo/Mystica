package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Mystic;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class Warp {

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final StatusEffectManager statusEffectManager;
    private final CooldownManager cooldownManager;

    public Warp(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        statusEffectManager = main.getStatusEffectManager();
        cooldownManager = manager.getCooldownManager();
    }

    private final int abilityNumber = 5;
    private final int baseCooldown = 13;
    private final double baseRange = 8;

    public void use(LivingEntity caster){


        if(!usable(caster)){
            return;
        }

        double maxDistance = baseRange + statusEffectManager.getAdditionalRange(caster);

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

        cooldownManager.start(caster.getUniqueId(), abilityNumber, (long) (baseCooldown * 1000));

    }



    public boolean usable(LivingEntity caster){
        return cooldownManager.isReady(caster.getUniqueId(), abilityNumber, statusEffectManager.getHastePercent(caster));
    }

}
