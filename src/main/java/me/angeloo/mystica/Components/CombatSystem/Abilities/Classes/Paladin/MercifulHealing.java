package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Paladin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityMarkManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.PaladinAbilities;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.PlayerStateManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl.Root;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.BarType;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MercifulHealing extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final DamageCalculator damageCalculator;
    private final TargetManager targetManager;
    private final PveChecker pveChecker;
    private final PvpManager pvpManager;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final AbilityManager abilityManager;
    private final CooldownManager cooldownManager;
    private final AbilityMarkManager abilityMarkManager;
    private final PlayerStateManager playerStateManager;

    private final Purity purity;

    public MercifulHealing(Mystica main, AbilityManager manager){
        super("merciful_healing");
        this.main = main;
        profileManager = main.getProfileManager();
        damageCalculator = main.getDamageCalculator();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        abilityManager = manager;
        cooldownManager = manager.getCooldownManager();
        purity = manager.getPurity();
        abilityMarkManager = manager.getAbilityMarkManager();
        playerStateManager = manager.getPlayerStateManager();
    }

    private final int baseCooldown = 7;
    private final int healPower = 10;

    @Override
    public void use(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        if(target == null){
            target = caster;
        }

        execute(caster, target);

        cooldownManager.start(caster.getUniqueId(), 2, (long) (baseCooldown * 1000));

    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private double getRange(LivingEntity caster){
        double baseRange = 10;
        double extraRange = statusEffectManager.getAdditionalRange(caster);
        return baseRange + extraRange;
    }

    private void execute(LivingEntity caster, LivingEntity target){

        abilityManager.setCasting(caster, true);
        int castTime = 20;

        if(playerStateManager.get(caster.getUniqueId()).has("move_cast")){
            playerStateManager.get(caster.getUniqueId()).remove("move_cast");
        }
        else{
            statusEffectManager.applyEffect(caster, new Root(), castTime, null);
        }

        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone();
            int ran = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();
                        abilityManager.setCasting(caster, false);
                        statusEffectManager.removeEffect(caster, "root");
                    }
                }

                if(!statusEffectManager.canCast(caster)){
                    this.cancel();
                    abilityManager.setCasting(caster, false);
                    statusEffectManager.removeEffect(caster, "root");
                    return;
                }

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation();
                    targetLoc = targetLoc.subtract(0,1,0);
                    targetWasLoc = targetLoc.clone();
                }

                double distanceToTarget = caster.getLocation().distance(targetWasLoc);

                if(distanceToTarget>getRange(caster)){
                    this.cancel();
                    abilityManager.setCasting(caster, false);
                    statusEffectManager.removeEffect(caster, "root");
                    return;
                }

                double percent = ((double) ran / castTime) * 100;
                abilityManager.setCastBar(caster, percent);

                if(ran >= castTime){
                    this.cancel();
                    abilityManager.setCasting(caster, false);
                    healTarget(target);
                    statusEffectManager.removeEffect(caster, "root");
                }

                ran++;
            }

            private void healTarget(LivingEntity target){



                boolean crit = damageCalculator.checkIfCrit(caster, 0);

                double healAmount = damageCalculator.calculateHealing(caster, getHealPower(caster), crit);

                if(abilityMarkManager.getTargets(caster).contains(target)){
                    markHealInstead(caster, healAmount);
                    playerStateManager.get(caster.getUniqueId()).remove("move_cast");
                    return;
                }


                changeResourceHandler.addHealthToEntity(target, healAmount, caster);
                playerStateManager.get(caster.getUniqueId()).remove("move_cast");

                Location center = target.getLocation().clone().add(0,1,0);

                double increment = (2 * Math.PI) / 16; // angle between particles

                for (int i = 0; i < 16; i++) {
                    double angle = i * increment;
                    double x = center.getX() + (1 * Math.cos(angle));
                    double z = center.getZ() + (1 * Math.sin(angle));
                    Location loc = new Location(center.getWorld(), x, (center.getY()), z);

                    target.getWorld().spawnParticle(Particle.HEART, loc, 1,0, 0, 0, 0);
                }

            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){

                    if(!((Player) target).isOnline()){
                        return false;
                    }

                }

                return !target.isDead();
            }

        }.runTaskTimer(main, 0, 1);

    }

    private void markHealInstead(LivingEntity caster, double healAmount){

        Set<LivingEntity> affected = abilityMarkManager.getTargets(caster);

        for(LivingEntity thisPlayer : affected){
            changeResourceHandler.addHealthToEntity(thisPlayer, healAmount, caster);

            Location center = thisPlayer.getLocation().clone().add(0,1,0);

            double increment = (2 * Math.PI) / 16; // angle between particles

            for (int i = 0; i < 16; i++) {
                double angle = i * increment;
                double x = center.getX() + (1 * Math.cos(angle));
                double z = center.getZ() + (1 * Math.sin(angle));
                Location loc = new Location(center.getWorld(), x, (center.getY()), z);

                thisPlayer.getWorld().spawnParticle(Particle.HEART, loc, 1,0, 0, 0, 0);
            }
        }

    }

    public double getHealPower(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_2_Level_Bonus();
        double damage = healPower + ((int)(skillLevel/3));

        if(purity.active(caster)){
            damage = damage * 3;
            purity.reset(caster);
        }

        return damage;
    }


    public boolean usable(LivingEntity caster, LivingEntity target){
        if(target != null){

            if(!(target instanceof Player)){

                if(pveChecker.pveLogic(target)){
                    target = caster;
                }
            }

            double distance = caster.getLocation().distance(target.getLocation());

            if(distance > getRange(caster)){
                return false;
            }

            if (profileManager.getAnyProfile(target).getIfDead()) {
                target = caster;
            }

            if(target instanceof Player){
                if(pvpManager.pvpLogic(caster, (Player) target)){
                    target = caster;
                }
            }

            if(target == caster){
                return true;
            }

        }


        return cooldownManager.isReady(caster.getUniqueId(), 2, statusEffectManager.getHastePercent(caster));
    }

}
