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
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class Dreadfall {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public Dreadfall(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    private final double range = 20;

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        targetManager.setTargetToNearestValid(caster, range+ buffAndDebuffManager.getTotalRangeModifier(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }


        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 10);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 4);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 4);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        boolean arcane = profileManager.getAnyProfile(caster).getPlayerSubclass().equalsIgnoreCase("arcane master");

        LivingEntity target = targetManager.getPlayerTarget(caster);

        Location spawnLoc = target.getLocation().clone().add(0,10,0);

        ArmorStand armorStand = caster.getWorld().spawn(spawnLoc, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack meteorItem = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta meta = meteorItem.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(11);
        meteorItem.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(meteorItem);



        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone().subtract(0,1,0);
            int count = 0;
            @Override
            public void run(){

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation();
                    targetLoc = targetLoc.subtract(0,1,0);
                    targetWasLoc = targetLoc.clone();
                }

                Location current = armorStand.getLocation();

                if (!sameWorld(current, targetWasLoc)) {
                    cancelTask();
                    return;
                }

                Vector direction = targetWasLoc.toVector().subtract(current.toVector());
                double distance = current.distance(targetWasLoc);
                double distanceThisTick = Math.min(distance, .75);
                current.add(direction.normalize().multiply(distanceThisTick));

                armorStand.teleport(current);

                armorStand.getWorld().spawnParticle(Particle.SPELL_WITCH, current.add(0,4,0), 1, 0, 0, 0, 0);

                if (distance <= 1) {

                    cancelTask();

                    Set<LivingEntity> hitBySkill = new HashSet<>();

                    BoundingBox hitBox = new BoundingBox(
                            target.getLocation().getX() - 4,
                            target.getLocation().getY() - 2,
                            target.getLocation().getZ() - 4,
                            target.getLocation().getX() + 4,
                            target.getLocation().getY() + 4,
                            target.getLocation().getZ() + 4
                    );

                    double increment = (2 * Math.PI) / 16; // angle between particles

                    for (int i = 0; i < 16; i++) {
                        double angle = i * increment;
                        double x = target.getLocation().getX() + (4 * Math.cos(angle));
                        double z = target.getLocation().getZ() + (4 * Math.sin(angle));
                        Location loc = new Location(target.getWorld(), x, (target.getLocation().getY()), z);

                        caster.getWorld().spawnParticle(Particle.SPELL_WITCH, loc, 1,0, 0, 0, 0);
                    }

                    for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

                        if(entity == caster){
                            continue;
                        }

                        if(!(entity instanceof LivingEntity)){
                            continue;
                        }

                        if(entity instanceof ArmorStand){
                            continue;
                        }

                        LivingEntity livingEntity = (LivingEntity) entity;

                        if(hitBySkill.contains(livingEntity)){
                            continue;
                        }

                        hitBySkill.add(livingEntity);

                        boolean crit = damageCalculator.checkIfCrit(caster, 0);
                        double damage = damageCalculator.calculateDamage(caster, target, "Magical", finalSkillDamage, crit);

                        //pvp logic
                        if(entity instanceof Player){
                            if(pvpManager.pvpLogic(caster, (Player) entity)){
                                changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster);

                                if(profileManager.getAnyProfile(livingEntity).getIsMovable()){
                                    Vector velocity = (new Vector(0, .5, 0));
                                    livingEntity.setVelocity(velocity);
                                    buffAndDebuffManager.getKnockUp().applyKnockUp(livingEntity);
                                }

                                if(arcane && crit){
                                    double fifteenPercent = (double) profileManager.getAnyProfile(caster).getTotalAttack() * .15;
                                    changeResourceHandler.subtractHealthFromEntity(target, fifteenPercent, caster);
                                }

                            }
                            continue;
                        }

                        if(pveChecker.pveLogic(livingEntity)){
                            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, caster));
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster);

                            if(profileManager.getAnyProfile(livingEntity).getIsMovable()){
                                Vector velocity = (new Vector(0, .5, 0));
                                livingEntity.setVelocity(velocity);
                                buffAndDebuffManager.getKnockUp().applyKnockUp(livingEntity);
                            }

                            if(arcane && crit){
                                double fifteenPercent = (double) profileManager.getAnyProfile(caster).getTotalAttack() * .15;
                                changeResourceHandler.subtractHealthFromEntity(target, fifteenPercent, caster);
                            }

                        }

                    }

                }

                if(count>100){
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

            private boolean sameWorld(Location loc1, Location loc2) {
                return loc1.getWorld().equals(loc2.getWorld());
            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
            }




        }.runTaskTimer(main, 0, 1);

    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_4_Level_Bonus();

        return 50 + ((int)(skillLevel/3));
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

            if(distance > range+ buffAndDebuffManager.getTotalRangeModifier(caster)){
                return false;
            }
        }

        if(target == null){
            return false;
        }

        return getCooldown(caster) <= 0;
    }

}
