package me.angeloo.mystica.Components.Abilities.Elementalist;

import me.angeloo.mystica.Components.Abilities.ElementalistAbilities;
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

public class FieryMagma {

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

    private final FieryWing fieryWing;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public FieryMagma(Mystica main, AbilityManager manager, ElementalistAbilities elementalistAbilities){
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

        abilityReadyInMap.put(caster.getUniqueId(), 10);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 2);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 2);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        Location end = target.getLocation();
        Location playerLoc = caster.getLocation();
        Vector spawnDirection = end.toVector().subtract(playerLoc.toVector());
        Location spawnLoc = target.getLocation().clone().add(spawnDirection.clone().normalize().multiply(10));
        spawnLoc.add(0,10,0);

        ArmorStand armorStand = caster.getWorld().spawn(spawnLoc, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack meteorItem = new ItemStack(Material.DRAGON_BREATH);
        ItemMeta meta = meteorItem.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(5);
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

                if(distanceThisTick!=0){
                    current.add(direction.normalize().multiply(distanceThisTick));
                }

                armorStand.teleport(current);

                armorStand.getWorld().spawnParticle(Particle.LAVA, current.clone().add(0,2,0), 1);

                if (distance <= 1) {

                    fieryWing.addInflame(caster);


                    cancelTask();

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = damageCalculator.calculateDamage(caster, target, "Magical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster);

                    if(target instanceof Player){
                        buffAndDebuffManager.getGenericShield().removeShields(target);
                    }

                    startBurningTask();
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

            private void startBurningTask(){

                double burn = finalSkillDamage * .1;

                new BukkitRunnable(){
                    int ticks = 0;
                    @Override
                    public void run(){

                        if(profileManager.getIfResetProcessing(target)){
                            this.cancel();
                            return;
                        }

                        if(target.isDead()){
                            this.cancel();
                            return;
                        }

                        if(target instanceof Player){
                            if(!((Player)target).isOnline()){
                                this.cancel();
                                return;
                            }

                            if(profileManager.getAnyProfile(target).getIfDead()){
                                this.cancel();
                                return;
                            }
                        }

                        boolean crit = damageCalculator.checkIfCrit(caster, 0);
                        double tickDamage = damageCalculator.calculateDamage(caster, target, "Magical", burn, crit);

                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                        changeResourceHandler.subtractHealthFromEntity(target, tickDamage, caster);

                        ticks ++;


                        if(ticks >=3){
                            this.cancel();
                            //explode
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

                                target.getWorld().spawnParticle(Particle.FLAME, loc, 1,0, 0, 0, 0);
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

                                boolean crit2 = damageCalculator.checkIfCrit(caster, 0);
                                double damage = (damageCalculator.calculateDamage(caster, livingEntity, "Magical", finalSkillDamage, crit2));

                                //pvp logic
                                if(entity instanceof Player){
                                    if(pvpManager.pvpLogic(caster, (Player) entity)){
                                        changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster);
                                    }
                                    continue;
                                }

                                if(pveChecker.pveLogic(livingEntity)){
                                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, caster));
                                    changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster);
                                }

                            }

                        }
                    }
                }.runTaskTimer(main, 0, 20);

            }


        }.runTaskTimer(main, 0, 1);

    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_2_Level_Bonus();

        return 20 + ((int)(skillLevel/3));
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
        }

        if(target == null){
            return false;
        }

        return getCooldown(caster) <= 0;
    }
}
