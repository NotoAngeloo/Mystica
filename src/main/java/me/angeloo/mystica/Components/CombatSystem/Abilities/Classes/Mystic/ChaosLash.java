package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Mystic;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.PlayerStateManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.Enums.SubClass;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class ChaosLash extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final AbilityManager abilityManager;
    private final CooldownManager cooldownManager;
    private final PlayerStateManager playerStateManager;


    public ChaosLash(Mystica main, AbilityManager manager){
        super("chaos_lash");
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = manager;
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = manager.getCooldownManager();
        playerStateManager = manager.getPlayerStateManager();

    }

    private final int baseCooldown = 11;
    private final double baseRange = 20;
    private final double baseDamage = 50;

    @Override
    public boolean use(LivingEntity caster){

        double totalRange = getRange(caster);

        targetManager.setTargetToNearestValid(caster, totalRange);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return false;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 6, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private double getRange(LivingEntity caster){
        double extraRange = statusEffectManager.getAdditionalRange(caster);
        return baseRange + extraRange;
    }

    private void execute(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(playerStateManager.get(target.getUniqueId()).has("plague_curse")){
            lookup.get(PlayerClass.Mystic, SubClass.Chaos, -1).onExternalTrigger(caster, 2);
        }



        double castTime = 15;
        castTime = castTime - statusEffectManager.getHastePercent(caster);

        double skillDamage = getSkillDamage(caster);

        skillDamage = skillDamage / castTime;

        abilityManager.setCasting(caster, true);
        double finalSkillDamage = skillDamage;
        double finalCastTime = castTime;
        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone();
            final Set<ArmorStand> allStands = new HashSet<>();
            int count = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                        return;
                    }
                }

                if(!statusEffectManager.canCast(caster)){
                    cancelTask();
                    return;
                }

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation();
                    targetLoc = targetLoc.subtract(0,1,0);
                    targetWasLoc = targetLoc.clone();
                }

                Location start = caster.getLocation();
                start.subtract(0, 1, 0);

                double distanceToTarget = start.distance(targetWasLoc);

                if(distanceToTarget>getRange(caster)){
                    cancelTask();
                    return;
                }

                ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
                armorStand.setInvisible(true);
                armorStand.setGravity(false);
                armorStand.setCollidable(false);
                armorStand.setInvulnerable(true);
                armorStand.setMarker(true);

                EntityEquipment entityEquipment = armorStand.getEquipment();

                ItemStack boltItem = new ItemStack(Material.SPECTRAL_ARROW);
                ItemMeta meta = boltItem.getItemMeta();
                assert meta != null;
                meta.setCustomModelData(9);
                boltItem.setItemMeta(meta);
                assert entityEquipment != null;
                entityEquipment.setHelmet(boltItem);

                allStands.add(armorStand);

                new BukkitRunnable() {
                    final double initialDistance = armorStand.getLocation().distance(target.getLocation());
                    final double halfDistance = initialDistance/2;
                    double traveled = 0;
                    Location targetWasLoc = target.getLocation().clone().subtract(0,1,0);
                    @Override
                    public void run() {

                        if(targetStillValid(target)){
                            Location targetLoc = target.getLocation().clone().subtract(0,1,0);
                            targetWasLoc = targetLoc.clone();
                        }

                        Location current = armorStand.getLocation();

                        if (!sameWorld(current, targetWasLoc)) {
                            cancelTask();
                            return;
                        }

                        Vector direction = targetWasLoc.toVector().subtract(current.toVector());
                        double distance = current.distance(targetWasLoc);
                        double distanceThisTick = Math.min(distance, 1);
                        current.add(direction.normalize().multiply(distanceThisTick));
                        traveled = traveled + distanceThisTick;

                        if(traveled < halfDistance){
                            current.subtract(direction.clone().crossProduct(new Vector(0,1,0).normalize().multiply(distanceThisTick)));
                        }

                        armorStand.teleport(current);


                        if (distance <= 1) {
                            cancelTask();

                            armorStand.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, current.add(0,2,0), 1, 0, 0, 0, 0);

                            boolean crit = damageCalculator.checkIfCrit(caster, 0);
                            double damage = damageCalculator.calculateDamage(caster, target, "Magical", finalSkillDamage, crit);

                            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                            changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);

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

                    private boolean sameWorld(Location loc1, Location loc2) {
                        return loc1.getWorld().equals(loc2.getWorld());
                    }

                    private void cancelTask() {
                        this.cancel();
                        armorStand.remove();
                    }
                }.runTaskTimer(main, 0L, 1);

                double percent = ((double) count / finalCastTime) * 100;

                if(caster instanceof Player){
                    abilityManager.setCastBar((Player) caster, percent);
                }


                if(count >= finalCastTime){
                    cancelTask();
                }

                count++;
            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){

                    if(!((Player) target).isOnline()){
                        return false;
                    }

                }

                return !target.isDead();
            }

            private void cancelTask(){
                this.cancel();
                removeStands();
                abilityManager.setCasting(caster, false);

                if(caster instanceof Player){
                    abilityManager.setCastBar((Player) caster, 0);
                }

            }

            private void removeStands(){
                for(ArmorStand stand : allStands){
                    stand.remove();
                }
            }

        }.runTaskTimer(main, 0, 6);

    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_6_Level_Bonus();
        return baseDamage + ((int)(skillLevel/3));
    }

    @Override
    public boolean usable(LivingEntity caster, LivingEntity target){
        if(target != null){
            if(target instanceof Player){
                if(!pvpManager.pvpLogic(caster, (Player) target)){
                    return false;
                }
            }

            if(!(target instanceof Player)){
                if(!pveChecker.pveLogic(target)){
                    return false;
                }
            }

            double distance = caster.getLocation().distance(target.getLocation());

            if(distance > getRange(caster)){
                return false;
            }

            if(distance<1){
                return false;
            }

        }

        if(target == null){
            return false;
        }

        return cooldownManager.isReady(caster.getUniqueId(), 6, statusEffectManager.getHastePercent(caster));
    }

}
