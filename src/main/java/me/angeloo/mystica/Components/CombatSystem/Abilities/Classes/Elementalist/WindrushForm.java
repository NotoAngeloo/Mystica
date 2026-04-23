package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Elementalist;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
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

public class WindrushForm extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final CooldownManager cooldownManager;

    private final Heat heat;

    private final int baseCooldown = 15;


    public WindrushForm(Mystica main, AbilityManager manager){
        super("windrush_form");
        this.main = main;
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        this.heat = manager.getHeat();
        cooldownManager = manager.getCooldownManager();
    }

    @Override
    public boolean use(LivingEntity caster){



        if(!usable(caster)){
            return false;
        }

        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_4_Level_Bonus();


        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 4, (long) (baseCooldown * 1000));
        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
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



    @Override
    public boolean usable(LivingEntity caster){
        return cooldownManager.isReady(caster.getUniqueId(), 4, statusEffectManager.getHastePercent(caster));
    }

}
