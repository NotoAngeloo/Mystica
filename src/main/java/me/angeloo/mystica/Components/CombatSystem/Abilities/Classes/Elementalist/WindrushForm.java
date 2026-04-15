package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Elementalist;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.ElementalistAbilities;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class WindrushForm {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final CooldownManager cooldownManager;

    private final Heat heat;

    private final int abilityNumber = 3;
    private final double baseCooldown = 15;

    public WindrushForm(Mystica main, AbilityManager manager, ElementalistAbilities elementalistAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        heat = elementalistAbilities.getHeat();
        cooldownManager = manager.getCooldownManager();
    }

    public void use(LivingEntity caster){



        if(!usable(caster)){
            return;
        }

        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_4_Level_Bonus();




        execute(caster);

        cooldownManager.start(caster.getUniqueId(), abilityNumber, (long) (baseCooldown * 1000));
    }

    private void execute(LivingEntity caster){

        heat.reduceHeat(caster, 5);

        Location start = caster.getLocation();
        Vector direction = start.getDirection().normalize();

        double forwardPower = 5;
        double jumpPower = .2;
        Vector dashVector = direction.multiply(forwardPower).setY(jumpPower);
        caster.setVelocity(dashVector);

        new BukkitRunnable(){
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();
                        return;
                    }
                }



                caster.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, caster.getLocation(), 1);

                Vector playerVel = caster.getVelocity();
                double speed = playerVel.length();

                if(speed < .5){
                    this.cancel();
                }
            }
        }.runTaskTimer(main, 0, 1);

    }




    public boolean usable(LivingEntity caster){
        return cooldownManager.isReady(caster.getUniqueId(), abilityNumber, statusEffectManager.getHastePercent(caster));
    }

}
