package me.angeloo.mystica.Components.Abilities.Ranger;

import me.angeloo.mystica.Components.Abilities.RangerAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RangerBasic {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final RallyingCry rallyingCry;

    private final Map<UUID, BukkitTask> basicRunning = new HashMap<>();

    private final Map<UUID, Integer> basicStageMap = new HashMap<>();

    private final Map<UUID, BukkitTask> removeBasicStageTaskMap = new HashMap<>();

    public RangerBasic(Mystica main, AbilityManager manager, RangerAbilities rangerAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        rallyingCry = rangerAbilities.getRallyingCry();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
    }

    public void useBasic(Player player){

        if(!basicStageMap.containsKey(player.getUniqueId())){
            basicStageMap.put(player.getUniqueId(), 1);
        }

        if(getIfBasicRunning(player)){
            return;
        }


        double totalRange = getRange(player);

        targetManager.setTargetToNearestValid(player, totalRange);

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

        if(distance > totalRange){
            return;
        }

        executeBasic(player);

    }

    private double getRange(Player player){
        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        return  baseRange + extraRange;
    }

    private void executeBasic(Player player){


        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

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


                tryToRemoveBasicStage(player);
                switch (basicStageMap.get(player.getUniqueId())){
                    case 1:{
                        basicStage1(player, 2);
                        break;
                    }
                    case 2:{
                        basicStage1(player, 3);
                        break;
                    }
                    case 3:{
                        basicStage2(player);
                        break;
                    }
                    case 4:{
                        basicStage3(player);
                        break;
                    }
                }

                combatManager.startCombatTimer(player);
            }
        }.runTaskTimer(main, 0, 10);
        basicRunning.put(player.getUniqueId(), task);


    }

    private void tryToRemoveBasicStage(Player player){

        if(removeBasicStageTaskMap.containsKey(player.getUniqueId())){
            removeBasicStageTaskMap.get(player.getUniqueId()).cancel();
        }

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){
                basicStageMap.remove(player.getUniqueId());
            }
        }.runTaskLater(main, 50);

        removeBasicStageTaskMap.put(player.getUniqueId(), task);

    }

    private void basicStage3(Player player){
        basicStageMap.put(player.getUniqueId(), 1);
    }

    private void basicStage1(Player player, int newStage){

        LivingEntity target = targetManager.getPlayerTarget(player);

        basicStageMap.put(player.getUniqueId(), newStage);

        Location start = player.getLocation();
        start.subtract(0, 1, 0);
        ArmorStand armorStand = player.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta meta = arrow.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(1);
        arrow.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(arrow);


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
                double distanceThisTick = Math.min(distance, 1);

                if(distanceThisTick!=0){
                    current.add(direction.normalize().multiply(distanceThisTick));
                }

                current.setDirection(direction);

                armorStand.teleport(current);

                if (distance <= 1) {

                    cancelTask();

                    boolean crit = damageCalculator.checkIfCrit(player, 0);
                    double damage = damageCalculator.calculateDamage(player, target, "Physical", finalSkillDamage, crit);

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

    private void basicStage2(Player player){


        LivingEntity target = targetManager.getPlayerTarget(player);

        basicStageMap.put(player.getUniqueId(), 4);

        Location start = player.getLocation();
        start.subtract(0, 1, 0);
        ArmorStand armorStand = player.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta meta = arrow.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(1);
        arrow.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(arrow);



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
                double distanceThisTick = Math.min(distance, 1);

                if(distanceThisTick!=0){
                    current.add(direction.normalize().multiply(distanceThisTick));
                }

                current.setDirection(direction);

                armorStand.teleport(current);

                if (distance <= 1) {

                    cancelTask();

                    boolean crit = damageCalculator.checkIfCrit(player, 0);
                    double damage = damageCalculator.calculateDamage(player, target, "Physical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, player);

                    if(rallyingCry.getIfBuffTime(player) > 0){
                        if(profileManager.getAnyProfile(target).getIsMovable()){
                            Vector awayDirection = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                            Vector velocity = awayDirection.multiply(.75).add(new Vector(0, .5, 0));
                            target.setVelocity(velocity);
                            buffAndDebuffManager.getKnockUp().applyKnockUp(target);
                        }
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
        double skillDamage = 10;
        double skillLevel = profileManager.getAnyProfile(player).getStats().getLevel();
        if(rallyingCry.getIfBuffTime(player) > 0){
            skillDamage = skillDamage * 1.25;
        }

        return skillDamage + ((int)(skillLevel/3));
    }
}
