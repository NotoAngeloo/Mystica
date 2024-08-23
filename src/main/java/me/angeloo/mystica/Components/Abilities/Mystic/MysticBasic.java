package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.Components.Abilities.MysticAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.*;
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

public class MysticBasic {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Consolation consolation;
    private final EvilSpirit evilSpirit;

    private final Map<UUID, BukkitTask> basicRunning = new HashMap<>();

    public MysticBasic(Mystica main, AbilityManager manager, MysticAbilities mysticAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        evilSpirit = mysticAbilities.getEvilSpirit();
        consolation = mysticAbilities.getConsolation();
    }

    public void useBasic(LivingEntity caster){

        String subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        if(getIfBasicRunning(caster)){
            return;
        }

        if(subclass.equalsIgnoreCase("chaos")){
            executeBasicChaos(caster);
            return;
        }

        executeBasic(caster);

    }

    private double getRange(LivingEntity caster){
        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(caster);
        return baseRange + extraRange;
    }

    private void executeBasicChaos(LivingEntity caster){

        targetManager.setTargetToNearestValid(caster, getRange(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(target == null){
            return;
        }

        if (target instanceof Player) {
            if (!pvpManager.pvpLogic(caster, (Player) target)) {
                return;
            }
        }

        if(!(target instanceof Player)){
            if(!pveChecker.pveLogic(target)){
                return;
            }
        }

        double distance = caster.getLocation().distance(target.getLocation());

        if(distance > getRange(caster)){
            return;
        }

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(buffAndDebuffManager.getIfBasicInterrupt(caster)){
                    this.cancel();
                    stopBasicRunning(caster);
                    return;
                }

                if(targetManager.getPlayerTarget(caster) != null){
                    if(profileManager.getAnyProfile(targetManager.getPlayerTarget(caster)).getIfDead()){
                        this.cancel();
                        stopBasicRunning(caster);
                        return;
                    }
                }


                double totalRange = getRange(caster);

                targetManager.setTargetToNearestValid(caster, totalRange);

                LivingEntity target = targetManager.getPlayerTarget(caster);

                if(target == null){
                    stopBasicRunning(caster);
                    return;
                }

                if (target instanceof Player) {
                    if (!pvpManager.pvpLogic(caster, (Player) target)) {
                        stopBasicRunning(caster);
                        return;
                    }
                }

                if(!(target instanceof Player)){
                    if(!pveChecker.pveLogic(target)){
                        stopBasicRunning(caster);
                        return;
                    }
                }

                double distance = caster.getLocation().distance(target.getLocation());

                if(distance > totalRange){
                    stopBasicRunning(caster);
                    return;
                }

                basicStageChaos(caster);
                combatManager.startCombatTimer(caster);
            }
        }.runTaskTimer(main, 0, 15);
        basicRunning.put(caster.getUniqueId(), task);


    }

    private void basicStageChaos(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        boolean evilSpirit = this.evilSpirit.getIfEvilSpirit(caster);

        Location start = caster.getLocation();
        start.subtract(0, 1, 0);
        ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack bolt = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta meta = bolt.getItemMeta();
        assert meta != null;


        if(!evilSpirit){
            meta.setCustomModelData(2);
        }
        else{
            meta.setCustomModelData(3);
        }

        bolt.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(bolt);


        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone().subtract(0,1,0);
            @Override
            public void run(){

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
                double distanceThisTick = Math.min(distance, .75);
                current.add(direction.normalize().multiply(distanceThisTick));
                current.setDirection(direction);

                armorStand.teleport(current);

                if(evilSpirit){
                    caster.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, current, 1, 0, 0, 0, 0);
                }

                if (distance <= 1) {

                    cancelTask();


                    if(evilSpirit){
                        aoeAttack();
                        return;
                    }

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = damageCalculator.calculateDamage(caster, target, "Magical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster);

                }

            }

            private void aoeAttack(){

                Set<LivingEntity> hitBySkill = new HashSet<>();

                BoundingBox hitBox = new BoundingBox(
                        targetWasLoc.getX() - 4,
                        targetWasLoc.getY() - 2,
                        targetWasLoc.getZ() - 4,
                        targetWasLoc.getX() + 4,
                        targetWasLoc.getY() + 4,
                        targetWasLoc.getZ() + 4
                );

                double increment = (2 * Math.PI) / 16; // angle between particles

                for (int i = 0; i < 16; i++) {
                    double angle = i * increment;
                    double x = targetWasLoc.getX() + (4 * Math.cos(angle));
                    double z = targetWasLoc.getZ() + (4 * Math.sin(angle));
                    Location loc = new Location(targetWasLoc.getWorld(), x, targetWasLoc.clone().add(0,1,0).getY(), z);

                    caster.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, loc, 1, 0, 0, 0, 0);
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
                    double damage = (damageCalculator.calculateDamage(caster, livingEntity, "Magical", getSkillDamage(caster), crit));

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

    private void executeBasic(LivingEntity caster){

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){


                if(targetManager.getPlayerTarget(caster) != null){
                    if(profileManager.getAnyProfile(caster).getIfDead()){
                        this.cancel();
                        basicRunning.remove(caster.getUniqueId());
                        return;
                    }
                }


                basicStage(caster);
                combatManager.startCombatTimer(caster);
            }
        }.runTaskTimer(main, 0, 15);
        basicRunning.put(caster.getUniqueId(), task);


    }

    private void basicStage(LivingEntity caster){
        LivingEntity target;

        boolean shepard = profileManager.getAnyProfile(caster).getPlayerSubclass().equalsIgnoreCase("shepard");

        boolean healing = false;

        if(targetManager.getPlayerTarget(caster) == null){
            target = caster;
            healing = true;
            //Bukkit.getLogger().info(caster.getName() + " target null");
        }
        else{
            target = targetManager.getPlayerTarget(caster);
            //Bukkit.getLogger().info(caster.getName() + " target " + target.getName());
        }

        if(target != caster){
            if(target.isDead()){
                targetManager.setPlayerTarget(caster, null);
                stopBasicRunning(caster);
                return;
            }

            if(target instanceof Player){
                if(!pvpManager.pvpLogic(caster, (Player) target)){
                    healing = true;
                    //Bukkit.getLogger().info(caster.getName() + " target not in pvp");
                }
                else{
                    boolean targetDeathStatus = profileManager.getAnyProfile(target).getIfDead();

                    if(targetDeathStatus){
                        targetManager.setPlayerTarget(caster, null);
                        stopBasicRunning(caster);
                        return;
                    }
                }


            }

            if(!(target instanceof Player)){
                if(!pveChecker.pveLogic(target)){
                    healing = true;
                    //Bukkit.getLogger().info(caster.getName() + " target not in pve");
                }
            }
        }

        Location playerLocation = caster.getLocation();
        Location targetLocation = target.getLocation();

        double distance = playerLocation.distance(targetLocation);

        if (distance > getRange(caster)) {
            stopBasicRunning(caster);
            return;
        }


        if(!healing){
            //Bukkit.getLogger().info(caster.getName() + " damaging");
            Location start = caster.getLocation();
            start.subtract(0, 1, 0);
            ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
            armorStand.setInvisible(true);
            armorStand.setGravity(false);
            armorStand.setCollidable(false);
            armorStand.setInvulnerable(true);
            armorStand.setMarker(true);

            EntityEquipment entityEquipment = armorStand.getEquipment();

            ItemStack bolt = new ItemStack(Material.SPECTRAL_ARROW);
            ItemMeta meta = bolt.getItemMeta();
            assert meta != null;

            meta.setCustomModelData(1);

            bolt.setItemMeta(meta);
            assert entityEquipment != null;
            entityEquipment.setHelmet(bolt);


            double finalSkillDamage = getSkillDamage(caster);
            new BukkitRunnable(){
                Location targetWasLoc = target.getLocation().clone().subtract(0,1,0);
                @Override
                public void run(){

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
                    double distanceThisTick = Math.min(distance, .75);
                    current.add(direction.normalize().multiply(distanceThisTick));
                    current.setDirection(direction);

                    armorStand.teleport(current);

                    if (distance <= 1) {

                        cancelTask();

                        boolean crit = damageCalculator.checkIfCrit(caster, 0);
                        double damage = damageCalculator.calculateDamage(caster, target, "Magical", finalSkillDamage, crit);

                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                        changeResourceHandler.subtractHealthFromEntity(target, damage, caster);

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
            }.runTaskTimer(main, 0, 1);
        }
        else{

            //Bukkit.getLogger().info(caster.getName() + " healing");

            double healPower = 1;
            boolean crit = damageCalculator.checkIfCrit(caster, 0);
            double healAmount  = damageCalculator.calculateHealing(caster, healPower, crit);

            changeResourceHandler.addHealthToEntity(target, healAmount, caster);
            //Bukkit.getLogger().info("adding " + healAmount + " to " + target);

            if(shepard){
                consolation.apply(caster, target);
            }


            Location center = target.getLocation().clone().add(0,1,0);

            double increment = (2 * Math.PI) / 16; // angle between particles

            for (int i = 0; i < 16; i++) {
                double angle = i * increment;
                double x = center.getX() + (1 * Math.cos(angle));
                double z = center.getZ() + (1 * Math.sin(angle));
                Location loc = new Location(center.getWorld(), x, (center.getY()), z);

                target.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
            }



        }
    }

    private boolean getIfBasicRunning(LivingEntity caster){
        return basicRunning.containsKey(caster.getUniqueId());
    }

    public void stopBasicRunning(LivingEntity caster){
        if(basicRunning.containsKey(caster.getUniqueId())){
            basicRunning.get(caster.getUniqueId()).cancel();
            basicRunning.remove(caster.getUniqueId());
        }
    }


    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getStats().getLevel();

       return 5 + ((int)(skillLevel/3));
    }

    public double getEvilSpiritDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getStats().getLevel();
        return 40 + ((int)(skillLevel/3));
    }

}
