package me.angeloo.mystica.Components.Abilities.Paladin;

import me.angeloo.mystica.Components.Abilities.PaladinAbilities;
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
import org.bukkit.block.Block;
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
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;

public class DuranceOfTruth {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownDisplayer cooldownDisplayer;

    private final Purity purity;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public DuranceOfTruth(Mystica main, AbilityManager manager, PaladinAbilities paladinAbilities){
        this.main = main;
        targetManager = main.getTargetManager();
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        purity = paladinAbilities.getPurity();
    }

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)){
            return;
        }



        combatManager.startCombatTimer(caster);

        execute(caster);
        purity.skillListAdd(caster, 7);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 20);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 7);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 7);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        double baseRange = 15;

        targetManager.setTargetToNearestValid(caster, baseRange);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        boolean targeted = false;

        Vector direction = caster.getLocation().getDirection().setY(0).normalize();

        if(target != null){

            if(target instanceof Player){
                if(pvpManager.pvpLogic(caster, (Player) target)){

                    double distance = caster.getLocation().distance(target.getLocation());

                    if(distance < baseRange){
                        targeted = true;
                    }


                }
            }

            if(!(target instanceof Player)){
                if(pveChecker.pveLogic(target)){

                    double distance = caster.getLocation().distance(target.getLocation());

                    if(distance < baseRange){
                        targeted = true;
                    }

                }
            }


        }

        if(targeted){
            direction = target.getLocation().toVector().subtract(caster.getLocation().toVector()).setY(0).normalize();
        }

        Location start = caster.getLocation().clone();
        //Location end = start.clone().add(direction.multiply(baseRange));
        Location end = start.clone();

        while (baseRange > 0) {
            end.add(direction);
            if (!end.getBlock().isPassable()) {
                end.subtract(direction.multiply(2));
                break;
            }
            baseRange -= 1;
        }

        if(targeted){
            end = target.getLocation().clone();
        }


        //player.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, end, 0, 0, 0, 0);


        //abilityManager.setSkillRunning(player, true);
        Location finalEnd = end;
        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            final Set<LivingEntity> affected = new HashSet<>();
            final List<ArmorStand> allStands = new ArrayList<>();
            final double length = start.distance(finalEnd);
            final double half = length/2;
            double traveled = 0;
            Location center;
            int count = 0;
            boolean going = true;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        return;
                    }
                }


                if(going){
                    Location current = caster.getLocation();
                    double distance = current.distance(finalEnd);
                    double distanceThisTick = Math.min(distance, 1);

                    Vector direction = finalEnd.toVector().subtract(current.toVector());

                    if(distanceThisTick!=0){
                        current.add(direction.normalize().multiply(distanceThisTick));
                    }

                    traveled = traveled + distanceThisTick;

                    if(traveled<half){
                        current.add(0,distanceThisTick,0);
                    }

                    caster.teleport(current);

                    if(distance<=1){
                        going = false;
                        center = caster.getLocation();
                        spawnShields(center);
                        damage();
                        //abilityManager.setSkillRunning(player, false);
                    }
                }

                if(!going){

                    double increment = (2 * Math.PI) / 16; // angle between particles

                    for (int i = 0; i < 16; i++) {
                        double angle = i * increment;
                        double x = center.getX() + (4 * Math.cos(angle));
                        double y = center.getY() + 1;
                        double z = center.getZ() + (4 * Math.sin(angle));
                        Location loc = new Location(center.getWorld(), x, y, z);
                        caster.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
                    }

                    Set<LivingEntity> hitByThisTick = new HashSet<>();

                    BoundingBox hitBox = new BoundingBox(
                            center.getX() - 4,
                            center.getY() - 2,
                            center.getZ() - 4,
                            center.getX() + 4,
                            center.getY() + 4,
                            center.getZ() + 4
                    );

                    for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

                        if(entity == caster){

                            if(count%20==0){
                                double fivePercent = (profileManager.getAnyProfile(caster).getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(caster)) * .05;
                                changeResourceHandler.addHealthToEntity(caster, fivePercent, caster);

                                buffAndDebuffManager.getDamageReduction().applyDamageReduction(caster, 0.95, 20);
                            }

                            continue;
                        }

                        if(!(entity instanceof LivingEntity)){
                            continue;
                        }

                        if(entity instanceof ArmorStand){
                            continue;
                        }

                        LivingEntity livingEntity = (LivingEntity) entity;

                        //pvp logic
                        if(entity instanceof Player){
                            if(pvpManager.pvpLogic(caster, (Player) entity)){
                                hitByThisTick.add(livingEntity);
                                affected.add(livingEntity);
                            }
                            continue;
                        }

                        if(pveChecker.pveLogic(livingEntity)){
                            hitByThisTick.add(livingEntity);
                            affected.add(livingEntity);
                        }
                    }

                    for(LivingEntity hitEntity : affected){
                        if(hitByThisTick.contains(hitEntity)){
                           continue;
                        }
                        buffAndDebuffManager.getSilence().applySilence(hitEntity, 20*3);
                        affected.remove(hitEntity);
                    }


                    count++;
                }

                if(count >= 6*20){
                    cancelTask();
                }
            }

            private void damage(){

                BoundingBox hitBox = new BoundingBox(
                        center.getX() - 4,
                        center.getY() - 2,
                        center.getZ() - 4,
                        center.getX() + 4,
                        center.getY() + 4,
                        center.getZ() + 4
                );

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

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = (damageCalculator.calculateDamage(caster, livingEntity, "Physical", finalSkillDamage, crit));

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

            private void spawnShields(Location center){

                ItemStack item = new ItemStack(Material.SUGAR);
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                meta.setCustomModelData(7);
                item.setItemMeta(meta);

                Vector direction = caster.getLocation().getDirection().setY(0).normalize();
                Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();


                Location s1spawn = center.clone().add(direction.clone().multiply(4)).setDirection(direction.clone());
                ArmorStand shield = caster.getWorld().spawn(s1spawn.clone().subtract(0,5,0), ArmorStand.class);
                shield.setInvisible(true);
                shield.setGravity(false);
                shield.setCollidable(false);
                shield.setInvulnerable(true);
                shield.setMarker(true);
                EntityEquipment entityEquipment = shield.getEquipment();
                assert entityEquipment != null;
                entityEquipment.setItemInMainHand(item);
                shield.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));

                shield.teleport(s1spawn);

                Location s2spawn = center.clone().subtract(direction.clone().multiply(4)).setDirection(direction.clone().multiply(-1));
                ArmorStand shield2 = caster.getWorld().spawn(s2spawn.clone().subtract(0,5,0), ArmorStand.class);
                shield2.setInvisible(true);
                shield2.setGravity(false);
                shield2.setCollidable(false);
                shield2.setInvulnerable(true);
                shield2.setMarker(true);
                EntityEquipment entityEquipment2 = shield2.getEquipment();
                assert entityEquipment2 != null;
                entityEquipment2.setItemInMainHand(item);
                shield2.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));

                shield2.teleport(s2spawn);

                Location s3spawn = center.clone().add(crossProduct.clone().multiply(4)).setDirection(crossProduct.clone());
                ArmorStand shield3 = caster.getWorld().spawn(s3spawn.clone().subtract(0,5,0), ArmorStand.class);
                shield3.setInvisible(true);
                shield3.setGravity(false);
                shield3.setCollidable(false);
                shield3.setInvulnerable(true);
                shield3.setMarker(true);
                EntityEquipment entityEquipment3 = shield3.getEquipment();
                assert entityEquipment3 != null;
                entityEquipment3.setItemInMainHand(item);
                shield3.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));
                shield3.teleport(s3spawn);

                Location s4spawn = center.clone().subtract(crossProduct.clone().multiply(4)).setDirection(crossProduct.clone().multiply(-1));

                ArmorStand shield4 = caster.getWorld().spawn(s4spawn.clone().subtract(0,5,0), ArmorStand.class);
                shield4.setInvisible(true);
                shield4.setGravity(false);
                shield4.setCollidable(false);
                shield4.setInvulnerable(true);
                shield4.setMarker(true);
                EntityEquipment entityEquipment4 = shield4.getEquipment();
                assert entityEquipment4 != null;
                entityEquipment4.setItemInMainHand(item);
                shield4.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));
                shield4.teleport(s4spawn);

                allStands.add(shield);
                allStands.add(shield2);
                allStands.add(shield3);
                allStands.add(shield4);
            }

            private void cancelTask(){
                this.cancel();
                for (ArmorStand stand : allStands){
                    stand.remove();
                }
            }

        }.runTaskTimer(main, 0, 1);

    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_7_Level_Bonus();
        return (purity.calculatePurityPercentDamage(caster, 7, 25)) + ((int)(skillLevel/3));
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

    public boolean usable(LivingEntity caster){
        if(getCooldown(caster) > 0){
            return false;
        }

        Block block = caster.getLocation().subtract(0,1,0).getBlock();

        return block.getType() != Material.AIR;
    }

}
