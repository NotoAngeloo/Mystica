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

    public void useBasic(Player player){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        if(getIfBasicRunning(player)){
            return;
        }

        if(subclass.equalsIgnoreCase("chaos")){
            executeBasicChaos(player);
            return;
        }

        executeBasic(player);

    }

    private double getRange(Player player){
        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        return baseRange + extraRange;
    }

    private void executeBasicChaos(Player player){

        targetManager.setTargetToNearestValid(player, getRange(player));

        LivingEntity target = targetManager.getPlayerTarget(player);

        if(target == null){
            return;
        }

        if (target instanceof Player) {
            if (!pvpManager.pvpLogic(player, (Player) target)) {
                return;
            }
        }

        if(!(target instanceof Player)){
            if(!pveChecker.pveLogic(target)){
                return;
            }
        }

        double distance = player.getLocation().distance(target.getLocation());

        if(distance > getRange(player)){
            return;
        }

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(buffAndDebuffManager.getIfBasicInterrupt(player)){
                    this.cancel();
                    stopBasicRunning(player);
                    return;
                }

                double totalRange = getRange(player);

                targetManager.setTargetToNearestValid(player, totalRange);

                LivingEntity target = targetManager.getPlayerTarget(player);

                if(target == null){
                    stopBasicRunning(player);
                    return;
                }

                if (target instanceof Player) {
                    if (!pvpManager.pvpLogic(player, (Player) target)) {
                        stopBasicRunning(player);
                        return;
                    }
                }

                if(!(target instanceof Player)){
                    if(!pveChecker.pveLogic(target)){
                        stopBasicRunning(player);
                        return;
                    }
                }

                double distance = player.getLocation().distance(target.getLocation());

                if(distance > totalRange){
                    stopBasicRunning(player);
                    return;
                }

                basicStageChaos(player);
                combatManager.startCombatTimer(player);
            }
        }.runTaskTimer(main, 0, 15);
        basicRunning.put(player.getUniqueId(), task);


    }

    private void basicStageChaos(Player player){

        LivingEntity target = targetManager.getPlayerTarget(player);

        boolean evilSpirit = this.evilSpirit.getIfEvilSpirit(player);

        Location start = player.getLocation();
        start.subtract(0, 1, 0);
        ArmorStand armorStand = player.getWorld().spawn(start, ArmorStand.class);
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


        double finalSkillDamage = getSkillDamage(player);
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
                    player.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, current, 1, 0, 0, 0, 0);
                }

                if (distance <= 1) {

                    cancelTask();


                    if(evilSpirit){
                        aoeAttack();
                        return;
                    }

                    boolean crit = damageCalculator.checkIfCrit(player, 0);
                    double damage = damageCalculator.calculateDamage(player, target, "Magical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, player);

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

                    player.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, loc, 1, 0, 0, 0, 0);
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
                    double damage = (damageCalculator.calculateDamage(player, livingEntity, "Magical", getSkillDamage(player), crit));

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

    private void executeBasic(Player player){

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){
                basicStage(player);
                combatManager.startCombatTimer(player);
            }
        }.runTaskTimer(main, 0, 15);
        basicRunning.put(player.getUniqueId(), task);


    }

    private void basicStage(Player player){
        LivingEntity target;

        boolean shepard = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("shepard");

        boolean healing = false;

        if(targetManager.getPlayerTarget(player) == null){
            target = player;
            healing = true;
        }
        else{
            target = targetManager.getPlayerTarget(player);
        }

        if(target != player){
            if(target.isDead()){
                targetManager.setPlayerTarget(player, null);
                stopBasicRunning(player);
                return;
            }

            if(target instanceof Player){
                if(!pvpManager.pvpLogic(player, (Player) target)){
                    healing = true;
                }
                else{
                    boolean targetDeathStatus = profileManager.getAnyProfile(target).getIfDead();

                    if(targetDeathStatus){
                        targetManager.setPlayerTarget(player, null);
                        stopBasicRunning(player);
                        return;
                    }
                }


            }

            if(!(target instanceof Player)){
                if(!pveChecker.pveLogic(target)){
                    stopBasicRunning(player);
                    return;
                }
            }
        }

        Location playerLocation = player.getLocation();
        Location targetLocation = target.getLocation();

        double distance = playerLocation.distance(targetLocation);

        if (distance > getRange(player)) {
            stopBasicRunning(player);
            return;
        }


        if(!healing){
            Location start = player.getLocation();
            start.subtract(0, 1, 0);
            ArmorStand armorStand = player.getWorld().spawn(start, ArmorStand.class);
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


            double finalSkillDamage = getSkillDamage(player);
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

                        boolean crit = damageCalculator.checkIfCrit(player, 0);
                        double damage = damageCalculator.calculateDamage(player, target, "Magical", finalSkillDamage, crit);

                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                        changeResourceHandler.subtractHealthFromEntity(target, damage, player);

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


            double healPower = 1;
            boolean crit = damageCalculator.checkIfCrit(player, 0);
            double healAmount  = damageCalculator.calculateHealing(player, healPower, crit);

            changeResourceHandler.addHealthToEntity(target, healAmount, player);

            if(shepard){
                consolation.apply(player, (Player) target);
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

    private boolean getIfBasicRunning(Player player){
        return basicRunning.containsKey(player.getUniqueId());
    }

    public void stopBasicRunning(Player player){
        if(basicRunning.containsKey(player.getUniqueId())){
            basicRunning.get(player.getUniqueId()).cancel();
            basicRunning.remove(player.getUniqueId());
        }
    }


    public double getSkillDamage(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getStats().getLevel();

       return 5 + ((int)(skillLevel/3));
    }

    public double getEvilSpiritDamage(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getStats().getLevel();
        return 40 + ((int)(skillLevel/3));
    }

}
