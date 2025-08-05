package me.angeloo.mystica.Components.Abilities.Elementalist;


import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Logic.PveChecker;
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

import java.util.*;

public class ElementalistBasic {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, Integer> basicStageMap = new HashMap<>();

    private final Map<UUID, BukkitTask> basicRunning = new HashMap<>();

    private final Map<UUID, BukkitTask> removeBasicStageTaskMap = new HashMap<>();

    public ElementalistBasic(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
    }

    public void use(LivingEntity caster){


        if(!basicStageMap.containsKey(caster.getUniqueId())){
            basicStageMap.put(caster.getUniqueId(), 1);
        }

        if(getIfBasicRunning(caster)){
            return;
        }


        double totalRange = getRange(caster);

        targetManager.setTargetToNearestValid(caster, totalRange);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }


        executeBasic(caster);

    }

    private double getRange(LivingEntity caster){
        double range = 20;
        return  range + buffAndDebuffManager.getTotalRangeModifier(caster);
    }

    private void executeBasic(LivingEntity caster){

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                LivingEntity target = targetManager.getPlayerTarget(caster);

                if(target == null || target.isDead()){
                    this.cancel();
                    stopBasicRunning(caster);
                    return;
                }

                if(buffAndDebuffManager.getIfBasicInterrupt(caster)){
                    this.cancel();
                    stopBasicRunning(caster);
                    return;
                }

                if(profileManager.getAnyProfile(targetManager.getPlayerTarget(caster)).getIfDead() || profileManager.getAnyProfile(caster).getIfDead()){
                    this.cancel();
                    stopBasicRunning(caster);
                    return;
                }

                double totalRange = getRange(caster);

                targetManager.setTargetToNearestValid(caster, totalRange);

                target = targetManager.getPlayerTarget(caster);

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
                        basicStage1(caster);
                        break;
                    }
                    case 2:{
                        basicStage2(caster);
                        break;
                    }
                    case 3:{
                        basicStage3(caster);
                        break;
                    }
                    case 4:{
                        basicStage4(caster);
                        break;
                    }
                }
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

    private void basicStage1(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        basicStageMap.put(caster.getUniqueId(), 2);

        Location start = caster.getLocation();
        start.subtract(0, 1, 0);
        ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack fireballItem = new ItemStack(Material.DRAGON_BREATH);
        ItemMeta meta = fireballItem.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(1);
        fireballItem.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(fireballItem);


        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable() {
            final double initialDistance = armorStand.getLocation().distance(target.getLocation());
            final double halfDistance = initialDistance/2;
            double traveled = 0;
            Location targetWasLoc = target.getLocation().clone().subtract(0,1,0);
            @Override
            public void run() {

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
                    traveled = traveled + distanceThisTick;
                }

                if(traveled < halfDistance){
                    current.subtract(direction.clone().crossProduct(new Vector(0,1,0).normalize().multiply(distanceThisTick)));
                }

                armorStand.teleport(current);

                if (distance <= 1) {
                    cancelTask();

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = damageCalculator.calculateDamage(caster, target, "Magical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);

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
        }.runTaskTimer(main, 0L, 1);

    }

    private void basicStage2(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        basicStageMap.put(caster.getUniqueId(), 3);

        Location start = caster.getLocation();
        start.subtract(0, 1, 0);
        ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack iceBallItem = new ItemStack(Material.DRAGON_BREATH);
        ItemMeta meta = iceBallItem.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(2);
        iceBallItem.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(iceBallItem);


        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable() {
            final double initialDistance = armorStand.getLocation().distance(target.getLocation());
            final double halfDistance = initialDistance/2;
            double traveled = 0;
            Location targetWasLoc = target.getLocation().clone().subtract(0,1,0);
            @Override
            public void run() {

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
                current.setDirection(direction);
                double distance = current.distance(targetWasLoc);
                double distanceThisTick = Math.min(distance, 1);

                if(distanceThisTick!=0){
                    current.add(direction.normalize().multiply(distanceThisTick));
                    traveled = traveled + distanceThisTick;
                }


                if(traveled < halfDistance){
                    current.add(direction.clone().crossProduct(new Vector(0,1,0).normalize().multiply(distanceThisTick)));
                }

                armorStand.teleport(current);

                if (distance <= 1) {
                    cancelTask();

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = damageCalculator.calculateDamage(caster, target, "Magical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);

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
        }.runTaskTimer(main, 0L, 1);

    }

    private void basicStage4(LivingEntity caster){
        basicStageMap.put(caster.getUniqueId(), 1);
    }

    private void basicStage3(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        basicStageMap.put(caster.getUniqueId(), 4);


        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone().subtract(0,1,0);
            final List<ArmorStand> armorStands = new ArrayList<>();
            int ran = 0;
            @Override
            public void run(){

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation().clone().subtract(0,1,0);
                    targetWasLoc = targetLoc.clone();
                }

                Location playerLoc = caster.getLocation().clone().subtract(0,1,0);

                if(!sameWorld(playerLoc, targetWasLoc)){
                    cancelTask();
                    return;
                }

                double distance = playerLoc.distance(targetWasLoc);
                Location current = playerLoc.clone();


                if(ran == 0){
                    for(double i = 0; i<distance;i+=.5){

                        Vector direction = targetWasLoc.toVector().subtract(playerLoc.toVector());
                        double distanceThisTick = Math.min(distance, .5);
                        current.setDirection(direction);

                        if(distanceThisTick!=0){
                            current.add(direction.normalize().multiply(distanceThisTick));
                        }


                        ArmorStand armorStand = caster.getWorld().spawn(current, ArmorStand.class);
                        armorStand.setInvisible(true);
                        armorStand.setGravity(false);
                        armorStand.setCollidable(false);
                        armorStand.setInvulnerable(true);
                        armorStand.setMarker(true);

                        EntityEquipment entityEquipment = armorStand.getEquipment();

                        ItemStack lightningItem = new ItemStack(Material.DRAGON_BREATH);
                        ItemMeta meta = lightningItem.getItemMeta();
                        assert meta != null;
                        meta.setCustomModelData(3);
                        lightningItem.setItemMeta(meta);
                        assert entityEquipment != null;
                        entityEquipment.setHelmet(lightningItem);

                        armorStands.add(armorStand);

                    }
                }
                else{

                    Vector direction = targetWasLoc.toVector().subtract(playerLoc.toVector());
                    double distanceThisTick = Math.min(distance, .5);

                    for(ArmorStand thisStand : armorStands){

                        if(distanceThisTick!=0){
                            current.add(direction.normalize().multiply(distanceThisTick));
                        }

                        current.setDirection(direction);
                        thisStand.teleport(current);
                    }

                }

                ran++;

                if(ran >=20){
                    cancelTask();


                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = damageCalculator.calculateDamage(caster, target, "Magical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster,crit);

                }
            }

            private boolean sameWorld(Location loc1, Location loc2) {
                return loc1.getWorld().equals(loc2.getWorld());
            }

            private void cancelTask() {
                this.cancel();
                removeArmorStands(armorStands);
            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){

                    if(!((Player) target).isOnline()){

                        return false;
                    }
                }
                return !target.isDead();
            }

            private void removeArmorStands(List<ArmorStand> stands){

                if(armorStands.isEmpty()){
                    return;
                }

                for(ArmorStand stand : stands){
                    stand.remove();
                }
            }

        }.runTaskTimer(main, 0L, 1);

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

    public boolean usable(LivingEntity caster, LivingEntity target){
        if(target == null){
            return false;
        }

        if (target instanceof Player) {
            if (!pvpManager.pvpLogic(caster, (Player) target)) {
                return false;
            }
        }

        if(!(target instanceof Player)){
            if(!pveChecker.pveLogic(target)){
                return false;
            }
        }

        double distance = caster.getLocation().distance(target.getLocation());

        if(distance > getRange(caster)){
            return false;
        }

        if(distance<1){
            return false;
        }

        return true;
    }

    private int getStage(LivingEntity caster){
        return basicStageMap.getOrDefault(caster.getUniqueId(), 1);
    }

}
