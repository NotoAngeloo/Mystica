package me.angeloo.mystica.Components.Abilities.Paladin;

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

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public DuranceOfTruth(Mystica main, AbilityManager manager){
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
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }


        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 20);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    cooldownDisplayer.displayCooldown(player, 7);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 7);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        double baseRange = 15;

        targetManager.setTargetToNearestValid(player, baseRange);

        LivingEntity target = targetManager.getPlayerTarget(player);

        boolean targeted = false;

        Vector direction = player.getLocation().getDirection().setY(0).normalize();

        if(target != null){

            if(target instanceof Player){
                if(pvpManager.pvpLogic(player, (Player) target)){

                    double distance = player.getLocation().distance(target.getLocation());

                    if(distance < baseRange){
                        targeted = true;
                    }

                }
            }

            if(!(target instanceof Player)){
                if(pveChecker.pveLogic(target)){

                    double distance = player.getLocation().distance(target.getLocation());

                    if(distance < baseRange){
                        targeted = true;
                    }

                }
            }


        }

        if(targeted){
            direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).setY(0).normalize();
        }

        Location start = player.getLocation().clone();
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
        double skillDamage = 5;
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_7_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_7_Level_Bonus();
        skillDamage = skillDamage + ((int)(skillLevel/10));

        Location finalEnd = end;
        double finalSkillDamage = skillDamage;
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

                if(going){
                    Location current = player.getLocation();
                    double distance = current.distance(finalEnd);
                    double distanceThisTick = Math.min(distance, 1);

                    Vector direction = finalEnd.toVector().subtract(current.toVector());

                    current.add(direction.normalize().multiply(distanceThisTick));

                    traveled = traveled + distanceThisTick;

                    if(traveled<half){
                        current.add(0,distanceThisTick,0);
                    }

                    player.teleport(current);

                    if(distance<=1){
                        going = false;
                        center = player.getLocation();
                        spawnShields(center);
                        damage();
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
                        player.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
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

                    for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {

                        if(entity == player){

                            if(count%20==0){
                                double fivePercent = profileManager.getAnyProfile(player).getTotalHealth() * .05;
                                changeResourceHandler.addHealthToEntity(player, fivePercent, player);

                                buffAndDebuffManager.getDamageReduction().applyDamageReduction(player, 0.95, 20);
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
                            if(pvpManager.pvpLogic(player, (Player) entity)){
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

                for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {

                    if(entity == player){
                        continue;
                    }

                    if(!(entity instanceof LivingEntity)){
                        continue;
                    }

                    if(entity instanceof ArmorStand){
                        continue;
                    }

                    LivingEntity livingEntity = (LivingEntity) entity;

                    boolean crit = damageCalculator.checkIfCrit(player, 0);
                    double damage = (damageCalculator.calculateDamage(player, livingEntity, "Physical", finalSkillDamage, crit));

                    //pvp logic
                    if(entity instanceof Player){
                        if(pvpManager.pvpLogic(player, (Player) entity)){
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                        }
                        continue;
                    }

                    if(pveChecker.pveLogic(livingEntity)){
                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, player));
                        changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                    }

                }

            }

            private void spawnShields(Location center){

                ItemStack item = new ItemStack(Material.SUGAR);
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                meta.setCustomModelData(7);
                item.setItemMeta(meta);

                Vector direction = player.getLocation().getDirection().setY(0).normalize();
                Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();


                Location s1spawn = center.clone().add(direction.clone().multiply(4)).setDirection(direction.clone());
                ArmorStand shield = player.getWorld().spawn(s1spawn.clone().subtract(0,5,0), ArmorStand.class);
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
                ArmorStand shield2 = player.getWorld().spawn(s2spawn.clone().subtract(0,5,0), ArmorStand.class);
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
                ArmorStand shield3 = player.getWorld().spawn(s3spawn.clone().subtract(0,5,0), ArmorStand.class);
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

                ArmorStand shield4 = player.getWorld().spawn(s4spawn.clone().subtract(0,5,0), ArmorStand.class);
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

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
