package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Mystic;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class ArcaneMissiles extends BaseAbility {

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

    public ArcaneMissiles(Mystica main, AbilityManager manager){
        super("arcane_missiles");
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = manager;
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = main.getCooldownManager();
    }

    private final int baseCooldown = 15;
    private final int baseDamage = 70;
    private final double baseRange = 20;

    @Override
    public boolean use(LivingEntity caster){

        double totalRange = getRange(caster);

        targetManager.setTargetToNearestValid(caster, totalRange);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return false;
        }


        execute(caster);

        cooldownManager.start(caster.getUniqueId(), -1, (long) (baseCooldown * 1000));

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

        double skillDamage = getSkillDamage(caster);

        double ticks = 10;
        ticks = ticks - statusEffectManager.getHastePercent(caster);

        skillDamage = skillDamage / ticks;


        double finalSkillDamage = skillDamage;
        double finalTicks = ticks;
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

                Vector direction = caster.getLocation().getDirection().normalize();
                Location spawn1Loc = start.clone();
                spawn1Loc.subtract(direction.clone().crossProduct(new Vector(0,1,0).normalize().multiply(.5)));
                Location spawn2Loc = start.clone();
                spawn2Loc.add(direction.clone().crossProduct(new Vector(0,1,0).normalize().multiply(.5)));

                ArmorStand armorStand = caster.getWorld().spawn(spawn1Loc, ArmorStand.class);
                armorStand.setInvisible(true);
                armorStand.setGravity(false);
                armorStand.setCollidable(false);
                armorStand.setInvulnerable(true);
                armorStand.setMarker(true);

                EntityEquipment entityEquipment = armorStand.getEquipment();

                ItemStack boltItem = new ItemStack(Material.SPECTRAL_ARROW);
                ItemMeta meta = boltItem.getItemMeta();
                assert meta != null;
                meta.setCustomModelData(1);
                boltItem.setItemMeta(meta);
                assert entityEquipment != null;
                entityEquipment.setHelmet(boltItem);

                allStands.add(armorStand);

                ArmorStand armorStand2 = caster.getWorld().spawn(spawn2Loc, ArmorStand.class);
                armorStand2.setInvisible(true);
                armorStand2.setGravity(false);
                armorStand2.setCollidable(false);
                armorStand2.setInvulnerable(true);
                armorStand2.setMarker(true);

                EntityEquipment entityEquipment2 = armorStand2.getEquipment();
                assert entityEquipment2 != null;
                entityEquipment2.setHelmet(boltItem);

                allStands.add(armorStand2);

                new BukkitRunnable() {
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

                        armorStand.teleport(current);


                        if (distance <= 1) {
                            cancelTask();

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

                new BukkitRunnable() {
                    Location targetWasLoc = target.getLocation().clone().subtract(0,1,0);
                    @Override
                    public void run() {

                        if(targetStillValid(target)){
                            Location targetLoc = target.getLocation().clone().subtract(0,1,0);
                            targetWasLoc = targetLoc.clone();
                        }

                        Location current = armorStand2.getLocation();

                        if (!sameWorld(current, targetWasLoc)) {
                            cancelTask();
                            return;
                        }

                        Vector direction = targetWasLoc.toVector().subtract(current.toVector());
                        double distance = current.distance(targetWasLoc);
                        double distanceThisTick = Math.min(distance, 1);
                        current.add(direction.normalize().multiply(distanceThisTick));

                        armorStand2.teleport(current);


                        if (distance <= 1) {
                            cancelTask();

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
                        armorStand2.remove();
                    }
                }.runTaskTimer(main, 0L, 1);

                if(count >= finalTicks){
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
            }

            private void removeStands(){
                for(ArmorStand stand : allStands){
                    stand.remove();
                }
            }



        }.runTaskTimer(main, 0, 6);
    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getStats().getLevel();
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

        return cooldownManager.isReady(caster.getUniqueId(), -1, statusEffectManager.getHastePercent(caster));
    }

}
