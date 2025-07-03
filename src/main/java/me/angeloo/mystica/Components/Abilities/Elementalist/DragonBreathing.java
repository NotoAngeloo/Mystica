package me.angeloo.mystica.Components.Abilities.Elementalist;

import me.angeloo.mystica.Components.Abilities.ElementalistAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.Hud.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Logic.PveChecker;
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

public class DragonBreathing {

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

    private final Heat heat;
    private final FieryWing fieryWing;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public DragonBreathing(Mystica main, AbilityManager manager, ElementalistAbilities elementalistAbilities){
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
        heat = elementalistAbilities.getHeat();
        fieryWing = elementalistAbilities.getFieryWing();

    }

    private final double range = 20;

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }


        targetManager.setTargetToNearestValid(caster, range + buffAndDebuffManager.getTotalRangeModifier(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }


        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 16);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 6);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;

                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 6);
            }
        }.runTaskTimerAsynchronously(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        heat.addHeat(caster, 15);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        Location spawnStart = caster.getLocation().clone();
        spawnStart.subtract(0, 1.5, 0);

        Location start = caster.getLocation().clone();
        Location end = target.getLocation();
        Vector direction = end.toVector().subtract(start.toVector());
        Location spawnLoc = start.clone().subtract(direction.clone().normalize().multiply(5));
        spawnLoc.add(0, 5, 0);


        ArmorStand armorStand = caster.getWorld().spawn(spawnLoc, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack dragonItem = new ItemStack(Material.DRAGON_BREATH);
        ItemMeta meta = dragonItem.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(10);
        dragonItem.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(dragonItem);


        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            int count = 0;
            final Location end = target.getLocation().add(0, 5, 0);
            final Set<LivingEntity> hitBySkill = new HashSet<>();
            boolean inflamed = false;
            @Override
            public void run(){

                Location current = armorStand.getLocation();

                Vector direction = end.toVector().subtract(current.toVector());
                current.setDirection(direction);
                double distance = current.distance(end);
                double distanceThisTick = Math.min(distance, .5);

                if(distanceThisTick!=0){
                    current.add(direction.normalize().multiply(distanceThisTick));
                }

                armorStand.teleport(current);

                Location loc = armorStand.getLocation();

                Vector dir = new Vector(0, -1, 0).multiply(Math.tan(Math.toRadians(45)));
                dir = dir.add(direction.normalize());

                double xOffset = 5 * direction.getX() / direction.length();
                double zOffset = 5 * direction.getZ() / direction.length();
                loc.add(xOffset, 0, zOffset);

                for (int i = 0; i < 5; i++) {
                    loc.add(dir);

                    if(i==4){
                        double increment = (2 * Math.PI) / 16;
                        int radius = 2;

                        for (int j = 0; j < 16; j++) {
                            double angle = j * increment;
                            double x = loc.getX() + (radius * Math.cos(angle));
                            double z = loc.getZ() + (radius * Math.sin(angle));
                            Location jloc = new Location(loc.getWorld(), x, loc.getY(), z);
                            caster.getWorld().spawnParticle(Particle.LAVA, jloc, 1, 0, 0, 0, 0);
                        }

                        BoundingBox hitBox = new BoundingBox(
                                loc.getX() - radius,
                                loc.getY() - 2,
                                loc.getZ() - radius,
                                loc.getX() + radius,
                                loc.getY() + 4,
                                loc.getZ() + radius
                        );

                        for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

                            if(entity == caster){
                                continue;
                            }

                            if(entity == armorStand){
                                continue;
                            }

                            if(!(entity instanceof LivingEntity)){
                                continue;
                            }

                            LivingEntity livingEntity = (LivingEntity) entity;

                            if(hitBySkill.contains(livingEntity)){
                                continue;
                            }

                            hitBySkill.add(livingEntity);

                            if(!inflamed){
                                fieryWing.addInflame(caster);
                                inflamed = true;
                            }

                            boolean crit = damageCalculator.checkIfCrit(caster, 0);
                            double damage = (damageCalculator.calculateDamage(caster, livingEntity, "Magical", finalSkillDamage, crit));

                            //pvp logic
                            if(entity instanceof Player){
                                if(pvpManager.pvpLogic(caster, (Player) entity)){
                                    changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                                    burnTask(livingEntity);

                                    if(profileManager.getAnyProfile(livingEntity).getIsMovable()){
                                        Vector awayDirection = entity.getLocation().toVector().subtract(loc.toVector()).normalize();
                                        Vector velocity = awayDirection.multiply(.5).add(new Vector(0, .5, 0));
                                        livingEntity.setVelocity(velocity);
                                        buffAndDebuffManager.getKnockUp().applyKnockUp(livingEntity);
                                    }

                                }
                                continue;
                            }

                            if(pveChecker.pveLogic(livingEntity)){
                                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, caster));
                                changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                                burnTask(livingEntity);

                                if(profileManager.getAnyProfile(livingEntity).getIsMovable()){
                                    Vector awayDirection = entity.getLocation().toVector().subtract(loc.toVector()).normalize();
                                    Vector velocity = awayDirection.multiply(.5).add(new Vector(0, .5, 0));
                                    livingEntity.setVelocity(velocity);
                                    buffAndDebuffManager.getKnockUp().applyKnockUp(livingEntity);
                                }

                            }
                        }
                    }
                }

                if(distance <=1){
                    cancelTask();
                }

                if(count>=100){
                    cancelTask();
                }

                count++;
            }

            private void burnTask(LivingEntity entity){
                double burnDamage = finalSkillDamage * .1;
                new BukkitRunnable(){
                    int ticks = 0;
                    @Override
                    public void run(){

                        if(profileManager.getIfResetProcessing(target)){
                            this.cancel();
                            return;
                        }

                        if(entity.isDead()){
                            this.cancel();
                            return;
                        }

                        if(entity instanceof Player){
                            if(!((Player)entity).isOnline()){
                                this.cancel();
                                return;
                            }

                            if(profileManager.getAnyProfile(entity).getIfDead()){
                                this.cancel();
                                return;
                            }
                        }

                        boolean crit = damageCalculator.checkIfCrit(caster, 0);
                        double tickDamage = damageCalculator.calculateDamage(caster, entity, "Magical", burnDamage, crit);

                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(entity, caster));
                        changeResourceHandler.subtractHealthFromEntity(entity, tickDamage, caster, crit);

                        ticks ++;

                        if(ticks >=5){
                            this.cancel();
                        }
                    }
                }.runTaskTimer(main, 0, 20);

            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
            }

        }.runTaskTimer(main, 0, 1);

    }

    public  double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_6_Level_Bonus();

        return 35 + ((int)(skillLevel/3));
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

            if(distance > range + buffAndDebuffManager.getTotalRangeModifier(caster)){
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
