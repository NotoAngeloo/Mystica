package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

public class HealthAbsorb {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final AbilityManager abilityManager;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public HealthAbsorb(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = manager;
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        double totalRange = getRange(caster);

        targetManager.setTargetToNearestValid(caster, totalRange);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }


        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();;
        }

        abilityReadyInMap.put(caster.getUniqueId(), 20);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 8);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 8);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private double getRange(LivingEntity caster){
        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(caster);
        return baseRange + extraRange;
    }

    private void execute(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        double castTime = 5;
        castTime = castTime - buffAndDebuffManager.getHaste().getHasteLevel(caster);
        castTime = castTime * 20;

        double skillDamage = getSkillDamage(caster);

        skillDamage = skillDamage / castTime;

        abilityManager.setCasting(caster, true);

        double finalSkillDamage = skillDamage;
        double finalCastTime = castTime;
        new BukkitRunnable(){
            final List<ArmorStand> armorStands = new ArrayList<>();
            int ran = 0;

            Vector initialDirection;
            boolean up = true;
            int angle = 0;
            double height = 0;
            final double radius = 4;
            @Override
            public void run(){

                if(targetInvalid(target)){
                    cancelTask();
                    return;
                }

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                        return;
                    }
                }

                if(buffAndDebuffManager.getIfInterrupt(caster)){
                    cancelTask();
                    return;
                }

                Location playerLoc = caster.getLocation().clone();

                if(profileManager.getAnyProfile(caster).getIfDead()){
                    cancelTask();
                    return;
                }

                Location targetLoc = target.getLocation().clone().subtract(0,1,0);

                double distanceToTarget = playerLoc.distance(targetLoc);

                if(distanceToTarget>getRange(caster)){
                    cancelTask();
                    return;
                }

                if (initialDirection == null) {
                    initialDirection = playerLoc.getDirection().setY(0).normalize();
                }

                Vector rotation = initialDirection.clone();
                double radians = Math.toRadians(angle);
                rotation.rotateAroundY(radians);
                playerLoc.setDirection(rotation);

                double x = playerLoc.getX() + rotation.getX() * radius;
                double z = playerLoc.getZ() + rotation.getZ() * radius;

                double x2 = playerLoc.getX() - rotation.getX() * radius;
                double z2 = playerLoc.getZ() - rotation.getZ() * radius;

                Location particleLoc = new Location(playerLoc.getWorld(), x, playerLoc.getY() + height, z);
                Location particleLoc2 = new Location(playerLoc.getWorld(), x2, playerLoc.getY() + height, z2);

                caster.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, particleLoc, 1, 0, 0, 0, 0);
                caster.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, particleLoc2, 1, 0, 0, 0, 0);


                if(up){
                    height += .1;
                }
                else{
                    height -= .1;
                }

                angle += 5;

                if(height >= 4){
                    up = false;
                }

                if(height < 0){
                    up = true;
                }


                ArmorStand armorStand = caster.getWorld().spawn(targetLoc, ArmorStand.class);
                armorStand.setInvisible(true);
                armorStand.setGravity(false);
                armorStand.setCollidable(false);
                armorStand.setInvulnerable(true);
                armorStand.setMarker(true);

                EntityEquipment entityEquipment = armorStand.getEquipment();

                ItemStack absorbItem = new ItemStack(Material.SPECTRAL_ARROW);
                ItemMeta meta = absorbItem.getItemMeta();
                assert meta != null;
                meta.setCustomModelData(10);
                absorbItem.setItemMeta(meta);
                assert entityEquipment != null;
                entityEquipment.setHelmet(absorbItem);

                armorStands.add(armorStand);

                new BukkitRunnable(){
                    final Location current = targetLoc.clone();
                    @Override
                    public void run(){

                        if(caster instanceof Player){
                            if(!((Player)caster).isOnline()){
                                cancelTask();
                                return;
                            }
                        }

                        if(targetInvalid(target)){
                            this.cancel();
                            return;
                        }

                        Location playerLoc = caster.getLocation().clone().subtract(0,1,0);

                        Vector direction = playerLoc.toVector().subtract(current.toVector());
                        double distance = current.distance(playerLoc);
                        double distanceThisTick = Math.min(distance, .75);
                        current.add(direction.normalize().multiply(distanceThisTick));
                        current.setDirection(direction);

                        armorStand.teleport(current);

                        if (distance <= 1) {
                            this.cancel();
                            armorStand.remove();
                        }

                    }
                }.runTaskTimer(main, 0, 1);

                if(ran%20==0){
                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = damageCalculator.calculateDamage(caster, target, "Magical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);
                    changeResourceHandler.addHealthToEntity(caster, damage, caster);

                    if(target instanceof Player){
                        buffAndDebuffManager.getGenericShield().removeShields(target);
                    }
                }

                double percent = ((double) ran /(20*5)) * 100;

                abilityManager.setCastBar(caster, percent);

                ran++;

                if(ran >= finalCastTime){
                    cancelTask();
                }

            }

            private boolean targetInvalid(LivingEntity target){

                if(target instanceof Player){

                    if(!((Player) target).isOnline()){

                        return true;
                    }
                }
                return target.isDead();
            }

            private void cancelTask() {
                this.cancel();
                removeArmorStands(armorStands);
                abilityManager.setCasting(caster, false);
                abilityManager.setCastBar(caster, 0);
            }


            private void removeArmorStands(List<ArmorStand> stands){

                if(armorStands.isEmpty()){
                    return;
                }

                for(ArmorStand stand : stands){
                    stand.remove();
                }
            }

        }.runTaskTimer(main, 0L, 1);

    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_8_Level_Bonus();
        return 25 + ((int)(skillLevel/3));
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

        return getCooldown(caster) <= 0;
    }

}
