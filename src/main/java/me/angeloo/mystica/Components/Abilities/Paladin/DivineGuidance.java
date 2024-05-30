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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;

public class DivineGuidance {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public DivineGuidance(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)){
            return;
        }

        changeResourceHandler.subTractManaFromEntity(caster, getCost());

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 12);
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

        Set<LivingEntity> hitBySkill = new HashSet<>();

        Location start = caster.getLocation();

        Vector direction = caster.getLocation().getDirection().setY(0).normalize();
        Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();

        Location h1spawn = start.clone().add(direction.clone().multiply(4)).setDirection(crossProduct);

        ArmorStand hammer = caster.getWorld().spawn(h1spawn.clone().subtract(0,5,0), ArmorStand.class);
        hammer.setInvisible(true);
        hammer.setGravity(false);
        hammer.setCollidable(false);
        hammer.setInvulnerable(true);
        hammer.setMarker(true);

        EntityEquipment entityEquipment = hammer.getEquipment();

        ItemStack item = new ItemStack(Material.SUGAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(4);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setItemInMainHand(item);

        hammer.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));

        hammer.teleport(h1spawn);

        Location h2spawn = start.clone().subtract(direction.clone().multiply(4)).setDirection(crossProduct);

        ArmorStand hammer2 = caster.getWorld().spawn(h2spawn.clone().subtract(0,5,0), ArmorStand.class);
        hammer2.setInvisible(true);
        hammer2.setGravity(false);
        hammer2.setCollidable(false);
        hammer2.setInvulnerable(true);
        hammer2.setMarker(true);

        EntityEquipment entityEquipment2 = hammer2.getEquipment();
        assert entityEquipment2 != null;
        entityEquipment2.setItemInMainHand(item);

        hammer2.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));

        hammer2.teleport(h2spawn);

        Location h3spawn = start.clone().add(crossProduct.clone().multiply(4)).setDirection(direction);

        ArmorStand hammer3 = caster.getWorld().spawn(h3spawn.clone().subtract(0,5,0), ArmorStand.class);
        hammer3.setInvisible(true);
        hammer3.setGravity(false);
        hammer3.setCollidable(false);
        hammer3.setInvulnerable(true);
        hammer3.setMarker(true);

        EntityEquipment entityEquipment3 = hammer3.getEquipment();
        assert entityEquipment3 != null;
        entityEquipment3.setItemInMainHand(item);

        hammer3.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));

        hammer3.teleport(h3spawn);

        Location h4spawn = start.clone().subtract(crossProduct.clone().multiply(4)).setDirection(direction);

        ArmorStand hammer4 = caster.getWorld().spawn(h4spawn.clone().subtract(0,5,0), ArmorStand.class);
        hammer4.setInvisible(true);
        hammer4.setGravity(false);
        hammer4.setCollidable(false);
        hammer4.setInvulnerable(true);
        hammer4.setMarker(true);

        EntityEquipment entityEquipment4 = hammer4.getEquipment();
        assert entityEquipment4 != null;
        entityEquipment4.setItemInMainHand(item);

        hammer4.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));

        hammer4.teleport(h4spawn);


        BoundingBox hitBox = new BoundingBox(
                start.getX() - 4,
                start.getY() - 2,
                start.getZ() - 4,
                start.getX() + 4,
                start.getY() + 4,
                start.getZ() + 4
        );

        List<LivingEntity> validEntities = new ArrayList<>();

        for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

            if(!(entity instanceof LivingEntity)){
                continue;
            }

            if(entity instanceof ArmorStand){
                continue;
            }

            LivingEntity hitEntity = (LivingEntity) entity;

            if(entity instanceof Player){
                if(pvpManager.pvpLogic(caster, (Player)hitEntity)){
                    continue;
                }
            }

            boolean deathStatus = profileManager.getAnyProfile(hitEntity).getIfDead();

            if(deathStatus){
                continue;
            }

            validEntities.add(hitEntity);
        }

        validEntities.sort(Comparator.comparingDouble(p -> profileManager.getAnyProfile(p).getCurrentHealth()
                /(double)profileManager.getAnyProfile(p).getTotalHealth()));

        List<LivingEntity> affected = validEntities.subList(0, Math.min(3, validEntities.size()));

        for(LivingEntity thisEntity : affected){
            double healPower = 5;
            boolean crit = damageCalculator.checkIfCrit(caster, 0);
            double healAmount = damageCalculator.calculateHealing(caster, healPower, crit);
            changeResourceHandler.addHealthToEntity(thisEntity, healAmount, caster);

            Location center = thisEntity.getLocation().clone().add(0,1,0);

            double increment = (2 * Math.PI) / 16; // angle between particles

            for (int i = 0; i < 16; i++) {
                double angle = i * increment;
                double x = center.getX() + (1 * Math.cos(angle));
                double z = center.getZ() + (1 * Math.sin(angle));
                Location loc = new Location(center.getWorld(), x, (center.getY()), z);

                caster.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
            }
        }

        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            Vector initialDirection;
            double angle = 0;
            double eulerAngle = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                        return;
                    }
                }

                if (initialDirection == null) {
                    initialDirection = caster.getLocation().getDirection().setY(0).normalize();
                }

                Location center = caster.getLocation();

                if(angle%100==0){
                    double increment = (2 * Math.PI) / 16; // angle between particles

                    for (int i = 0; i < 16; i++) {
                        double angle = i * increment;
                        double x = center.getX() + (4 * Math.cos(angle));
                        double y = center.getY() + 1;
                        double z = center.getZ() + (4 * Math.sin(angle));
                        Location loc = new Location(center.getWorld(), x, y, z);

                        caster.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
                    }

                }

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

                    if(hitBySkill.contains(livingEntity)){
                        continue;
                    }

                    hitBySkill.add(livingEntity);

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = damageCalculator.calculateDamage(caster, livingEntity, "Physical", finalSkillDamage, crit);

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

                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);
                direction.rotateAroundY(radians);
                Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();

                Location h1Loc = center.clone().add(direction.clone().multiply(4)).setDirection(crossProduct);
                hammer.teleport(h1Loc);
                hammer.setRightArmPose(new EulerAngle(Math.toRadians(eulerAngle), Math.toRadians(0), Math.toRadians(0)));

                Location h2Loc = center.clone().subtract(direction.clone().multiply(4)).setDirection(crossProduct);
                hammer2.teleport(h2Loc);
                hammer2.setRightArmPose(new EulerAngle(Math.toRadians(eulerAngle), Math.toRadians(0), Math.toRadians(0)));

                Location h3Loc = center.clone().add(crossProduct.clone().multiply(4)).setDirection(direction);
                hammer3.teleport(h3Loc);
                hammer3.setRightArmPose(new EulerAngle(Math.toRadians(eulerAngle), Math.toRadians(0), Math.toRadians(0)));

                Location h4Loc = center.clone().subtract(crossProduct.clone().multiply(4)).setDirection(direction);
                hammer4.teleport(h4Loc);
                hammer4.setRightArmPose(new EulerAngle(Math.toRadians(eulerAngle), Math.toRadians(0), Math.toRadians(0)));

                if(angle>360){
                    cancelTask();
                }

                angle+=10;
                eulerAngle+=5;
            }

            private void cancelTask(){
                this.cancel();
                hammer.remove();
                hammer2.remove();
                hammer3.remove();
                hammer4.remove();
            }

        }.runTaskTimer(main, 0, 1);

    }

    public double getCost(){
        return 5;
    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_2_Level_Bonus();

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

    public boolean usable(LivingEntity caster){
        if(getCooldown(caster) > 0){
            return false;
        }

        if(profileManager.getAnyProfile(caster).getCurrentMana()<getCost()){
            return false;
        }

        return true;
    }

}
