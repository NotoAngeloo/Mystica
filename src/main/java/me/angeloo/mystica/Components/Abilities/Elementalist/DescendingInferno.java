package me.angeloo.mystica.Components.Abilities.Elementalist;

import me.angeloo.mystica.Components.Abilities.ElementalistAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
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
import org.bukkit.util.Vector;

import java.util.*;

public class DescendingInferno {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final ChangeResourceHandler changeResourceHandler;
    private final BuffAndDebuffManager buffAndDebuffManager;

    private final FieryWing fieryWing;
    private final ElementalBreath elementalBreath;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public DescendingInferno(Mystica main, AbilityManager manager, ElementalistAbilities elementalistAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();

        fieryWing = elementalistAbilities.getFieryWing();
        elementalBreath = elementalistAbilities.getElementalBreath();

    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        double totalRange = baseRange + extraRange;

        targetManager.setTargetToNearestValid(player, totalRange);

        LivingEntity target = targetManager.getPlayerTarget(player);

        if(target != null){
            if(target instanceof Player){
                if(!pvpManager.pvpLogic(player, (Player) target)){
                    return;
                }
            }

            if(!(target instanceof Player)){
                if(!pveChecker.pveLogic(target)){
                    return;
                }
            }

            double distance = player.getLocation().distance(target.getLocation());

            if(distance > totalRange){
                return;
            }
        }

        if(target == null){
            return;
        }

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        combatManager.startCombatTimer(player);


        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 10);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;

                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);

            }
        }.runTaskTimer(main, 0,20);

    }

    //now if elemental breath, explode as well
    private void execute(Player player){

        boolean conjurer = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("conjurer");

        LivingEntity target = targetManager.getPlayerTarget(player);

        Location start = player.getLocation();
        start.subtract(0, 1, 0);

        ItemStack fireballItem = new ItemStack(Material.DRAGON_BREATH);
        ItemMeta meta = fireballItem.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(1);
        fireballItem.setItemMeta(meta);

        ArmorStand armorStandLeft = start.getWorld().spawn(start, ArmorStand.class);
        armorStandLeft.setInvisible(true);
        armorStandLeft.setGravity(false);
        armorStandLeft.setCollidable(false);
        armorStandLeft.setInvulnerable(true);
        armorStandLeft.setMarker(true);

        EntityEquipment entityEquipmentLeft = armorStandLeft.getEquipment();

        assert entityEquipmentLeft != null;
        entityEquipmentLeft.setHelmet(fireballItem);

        ArmorStand armorStandRight = start.getWorld().spawn(start, ArmorStand.class);
        armorStandRight.setInvisible(true);
        armorStandRight.setGravity(false);
        armorStandRight.setCollidable(false);
        armorStandRight.setInvulnerable(true);
        armorStandRight.setMarker(true);

        EntityEquipment entityEquipmentRight = armorStandRight.getEquipment();

        assert entityEquipmentRight != null;
        entityEquipmentRight.setHelmet(fireballItem);

        ArmorStand armorStandMiddle = start.getWorld().spawn(start, ArmorStand.class);
        armorStandMiddle.setInvisible(true);
        armorStandMiddle.setGravity(false);
        armorStandMiddle.setCollidable(false);
        armorStandMiddle.setInvulnerable(true);
        armorStandMiddle.setMarker(true);

        EntityEquipment entityEquipmentMiddle = armorStandMiddle.getEquipment();

        assert entityEquipmentMiddle != null;
        entityEquipmentMiddle.setHelmet(fireballItem);

        double skillDamage = 5;
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_3_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_3_Level_Bonus();

        skillDamage = skillDamage + ((int)(skillLevel/10));

        if(conjurer){

            double maxMana = profileManager.getAnyProfile(player).getTotalMana();
            double currentMana = profileManager.getAnyProfile(player).getCurrentMana();

            double percent = maxMana/currentMana;

            skillDamage = skillDamage * (1 + percent);
        }


        double finalSkillDamage = skillDamage;
        new BukkitRunnable() {
            final double initialDistance = armorStandMiddle.getLocation().distance(target.getLocation());
            final double halfDistance = initialDistance/2;
            double traveledLeft = 0;
            double traveledRight = 0;
            Location targetWasLoc = target.getLocation().clone().subtract(0,1,0);
            boolean leftActive = true;
            boolean rightActive = true;
            boolean middleActive =  true;
            boolean inflamedAlready = false;
            boolean needToInflame = false;
            @Override
            public void run() {

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation();
                    targetLoc = targetLoc.subtract(0,1,0);
                    targetWasLoc = targetLoc.clone();
                }

                if(leftActive){
                    Location currentLeft = armorStandLeft.getLocation();
                    Vector directionLeft = targetWasLoc.toVector().subtract(currentLeft.toVector());
                    double distanceLeft = currentLeft.distance(targetWasLoc);
                    double distanceThisTickLeft = Math.min(distanceLeft, 1);
                    currentLeft.add(directionLeft.normalize().multiply(distanceThisTickLeft));
                    traveledLeft = traveledLeft + distanceThisTickLeft;

                    if(traveledLeft < halfDistance){
                        currentLeft.subtract(directionLeft.clone().crossProduct(new Vector(0,1,0).normalize().multiply(distanceThisTickLeft)));
                    }

                    armorStandLeft.teleport(currentLeft);
                    player.getWorld().spawnParticle(Particle.FLAME, currentLeft.clone().add(0,1,0), 1, 0, 0, 0, 0);

                    if (distanceLeft <= 1) {
                        armorStandLeft.remove();
                        leftActive = false;
                        needToInflame = true;

                        boolean crit = damageCalculator.checkIfCrit(player, 0);
                        double damage = damageCalculator.calculateDamage(player, target, "Magical", finalSkillDamage, crit);

                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                        changeResourceHandler.subtractHealthFromEntity(target, damage, player);

                        if(elementalBreath.getIfBuffTime(player) > 0){
                            explodeFireball(player);
                        }
                    }
                }

                if(rightActive){
                    Location currentRight = armorStandRight.getLocation();
                    Vector directionRight = targetWasLoc.toVector().subtract(currentRight.toVector());
                    double distanceRight = currentRight.distance(targetWasLoc);
                    double distanceThisTickRight = Math.min(distanceRight, 1);
                    currentRight.add(directionRight.normalize().multiply(distanceThisTickRight));
                    traveledRight = traveledRight + distanceThisTickRight;

                    if(traveledRight < halfDistance){
                        currentRight.add(directionRight.clone().crossProduct(new Vector(0,1,0).normalize().multiply(distanceThisTickRight)));
                    }

                    armorStandRight.teleport(currentRight);
                    player.getWorld().spawnParticle(Particle.FLAME, currentRight.clone().add(0,1,0), 1, 0, 0, 0, 0);

                    if (distanceRight <= 1) {
                        armorStandRight.remove();
                        rightActive = false;
                        needToInflame = true;

                        boolean crit = damageCalculator.checkIfCrit(player, 0);
                        double damage = damageCalculator.calculateDamage(player, target, "Magical", finalSkillDamage, crit);

                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                        changeResourceHandler.subtractHealthFromEntity(target, damage, player);

                        if(elementalBreath.getIfBuffTime(player) > 0){
                            explodeFireball(player);
                        }
                    }
                }

                if(middleActive){
                    Location currentMiddle = armorStandMiddle.getLocation();
                    Vector direction = targetWasLoc.toVector().subtract(currentMiddle.toVector());
                    double distance = currentMiddle.distance(targetWasLoc);
                    double distanceThisTick = Math.min(distance, .75);
                    currentMiddle.add(direction.normalize().multiply(distanceThisTick));
                    armorStandMiddle.teleport(currentMiddle);
                    player.getWorld().spawnParticle(Particle.FLAME, currentMiddle.clone().add(0,1,0), 1, 0, 0, 0, 0);

                    if (distance <= 1) {
                        armorStandMiddle.remove();
                        middleActive = false;
                        needToInflame = true;

                        boolean crit = damageCalculator.checkIfCrit(player, 0);
                        double damage = damageCalculator.calculateDamage(player, target, "Magical", finalSkillDamage, crit);

                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                        changeResourceHandler.subtractHealthFromEntity(target, damage, player);

                        if(elementalBreath.getIfBuffTime(player) > 0){
                            explodeFireball(player);
                        }
                    }
                }

                if(needToInflame && !inflamedAlready){

                    inflamedAlready = true;

                    fieryWing.addInflame(player);
                }

                //check if all 3 are gone before canceling
                if(!leftActive && !middleActive && !rightActive){
                    this.cancel();
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

            private void explodeFireball(Player player){

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

                    target.getWorld().spawnParticle(Particle.FLAME, loc, 1, 0, 0, 0, 0);
                }

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

                    if(hitBySkill.contains(livingEntity)){
                        continue;
                    }

                    hitBySkill.add(livingEntity);

                    boolean crit = damageCalculator.checkIfCrit(player, 0);
                    double damage = (damageCalculator.calculateDamage(player, livingEntity, "Magical", finalSkillDamage, crit));

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

        }.runTaskTimer(main, 0L, 1);

    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }
}
