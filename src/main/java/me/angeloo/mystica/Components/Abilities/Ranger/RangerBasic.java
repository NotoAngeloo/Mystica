package me.angeloo.mystica.Components.Abilities.Ranger;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
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

    private final Focus focus;

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
        focus = rangerAbilities.getFocus();
    }

    public void useBasic(LivingEntity caster){

        if(!basicStageMap.containsKey(caster.getUniqueId())){
            basicStageMap.put(caster.getUniqueId(), 1);
        }

        if(getIfBasicRunning(caster)){
            return;
        }


        double totalRange = getRange(caster);

        targetManager.setTargetToNearestValid(caster, totalRange);

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

        if(distance > totalRange){
            return;
        }


        if(distance<1){
            return;
        }

        executeBasic(caster);

    }

    private double getRange(LivingEntity caster){
        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(caster);
        return  baseRange + extraRange;
    }

    private void executeBasic(LivingEntity caster){


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

                if(distance<1){
                    stopBasicRunning(caster);
                    return;
                }

                if(MythicBukkit.inst().getAPIHelper().isMythicMob(caster.getUniqueId())){
                    AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(caster).getEntity();
                    MythicBukkit.inst().getAPIHelper().getMythicMobInstance(caster).signalMob(abstractEntity, "basic");
                }

                tryToRemoveBasicStage(caster);
                switch (getStage(caster)){
                    case 1:{
                        basicStage1(caster, 2);
                        break;
                    }
                    case 2:{
                        basicStage1(caster, 3);
                        break;
                    }
                    case 3:{
                        basicStage2(caster);
                        break;
                    }
                    case 4:{
                        basicStage3(caster);
                        break;
                    }
                }

                combatManager.startCombatTimer(caster);
            }
        }.runTaskTimer(main, 0, 10);
        basicRunning.put(caster.getUniqueId(), task);


    }

    private void tryToRemoveBasicStage(LivingEntity caster){

        if(removeBasicStageTaskMap.containsKey(caster.getUniqueId())){
            removeBasicStageTaskMap.get(caster.getUniqueId()).cancel();
        }

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){
                basicStageMap.remove(caster.getUniqueId());
            }
        }.runTaskLater(main, 50);

        removeBasicStageTaskMap.put(caster.getUniqueId(), task);

    }

    private void basicStage3(LivingEntity caster){
        basicStageMap.put(caster.getUniqueId(), 1);
    }

    private void basicStage1(LivingEntity caster, int newStage){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        basicStageMap.put(caster.getUniqueId(), newStage);

        Location start = caster.getLocation();
        start.subtract(0, 1, 0);
        ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
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
                double distanceThisTick = Math.min(distance, 1);

                if(distanceThisTick!=0){
                    current.add(direction.normalize().multiply(distanceThisTick));
                }

                current.setDirection(direction);

                armorStand.teleport(current);

                if (distance <= 1) {

                    cancelTask();

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);

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

    private void basicStage2(LivingEntity caster){


        LivingEntity target = targetManager.getPlayerTarget(caster);

        basicStageMap.put(caster.getUniqueId(), 4);

        Location start = caster.getLocation();
        start.subtract(0, 1, 0);
        ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
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
                double distanceThisTick = Math.min(distance, 1);

                if(distanceThisTick!=0){
                    current.add(direction.normalize().multiply(distanceThisTick));
                }

                current.setDirection(direction);

                armorStand.teleport(current);

                if (distance <= 1) {

                    cancelTask();

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster);

                    if(rallyingCry.getIfBuffTime(caster) > 0){
                        if(profileManager.getAnyProfile(target).getIsMovable()){
                            Vector awayDirection = target.getLocation().toVector().subtract(caster.getLocation().toVector()).normalize();
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
        double skillDamage = 10;
        double skillLevel = profileManager.getAnyProfile(caster).getStats().getLevel();
        if(rallyingCry.getIfBuffTime(caster) > 0){
            skillDamage = skillDamage * 1.25;
        }

        return focus.calculateFocusMultipliedDamage(caster, skillDamage) + ((int)(skillLevel/3));
    }

    private int getStage(LivingEntity caster){
        return basicStageMap.getOrDefault(caster.getUniqueId(), 1);
    }
}
