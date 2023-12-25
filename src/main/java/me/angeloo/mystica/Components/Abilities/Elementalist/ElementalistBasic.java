package me.angeloo.mystica.Components.Abilities.Elementalist;


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
    private final Map<UUID, Boolean> basicReadyMap = new HashMap<>();

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

    public void use(Player player){

        if(!basicStageMap.containsKey(player.getUniqueId())){
            basicStageMap.put(player.getUniqueId(), 1);
        }

        if(!basicReadyMap.containsKey(player.getUniqueId())){
            basicReadyMap.put(player.getUniqueId(), true);
        }

        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        double totalRange = baseRange + extraRange;

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

        if(!basicReadyMap.get(player.getUniqueId())){
            return;
        }

        tryToRemoveBasicStage(player);

        executeBasic(player);

    }



    private void executeBasic(Player player){

        basicReadyMap.put(player.getUniqueId(), false);



        switch (basicStageMap.get(player.getUniqueId())){
            case 1:{
                basicStage1(player);
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        basicReadyMap.put(player.getUniqueId(), true);
                    }
                }.runTaskLater(main, 10);
                break;
            }
            case 2:{
                basicStage2(player);
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        basicReadyMap.put(player.getUniqueId(), true);
                    }
                }.runTaskLater(main, 10);
                break;
            }
            case 3:{
                basicStage3(player);
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        basicReadyMap.put(player.getUniqueId(), true);
                    }
                }.runTaskLater(main, 20);
                break;
            }
        }


        combatManager.startCombatTimer(player);
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

    private void basicStage1(Player player){

        LivingEntity target = targetManager.getPlayerTarget(player);

        basicStageMap.put(player.getUniqueId(), 2);

        Location start = player.getLocation();
        start.subtract(0, 1, 0);
        ArmorStand armorStand = player.getWorld().spawn(start, ArmorStand.class);
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

        double skillDamage = 1;
        double skillLevel = profileManager.getAnyProfile(player).getStats().getLevel();

        skillDamage = skillDamage + ((int)(skillLevel/10));

        double finalSkillDamage = skillDamage;
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
                current.add(direction.normalize().multiply(distanceThisTick));
                traveled = traveled + distanceThisTick;

                if(traveled < halfDistance){
                    current.subtract(direction.clone().crossProduct(new Vector(0,1,0).normalize().multiply(distanceThisTick)));
                }

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
        }.runTaskTimer(main, 0L, 1);

    }

    private void basicStage2(Player player){

        LivingEntity target = targetManager.getPlayerTarget(player);

        basicStageMap.put(player.getUniqueId(), 3);

        Location start = player.getLocation();
        start.subtract(0, 1, 0);
        ArmorStand armorStand = player.getWorld().spawn(start, ArmorStand.class);
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

        double skillDamage = 1;
        double skillLevel = profileManager.getAnyProfile(player).getStats().getLevel();

        skillDamage = skillDamage + ((int)(skillLevel/10));

        double finalSkillDamage = skillDamage;
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
                current.add(direction.normalize().multiply(distanceThisTick));
                traveled = traveled + distanceThisTick;

                if(traveled < halfDistance){
                    current.add(direction.clone().crossProduct(new Vector(0,1,0).normalize().multiply(distanceThisTick)));
                }

                armorStand.teleport(current);

                if (distance <= 1) {
                    cancelTask();

                    double level = profileManager.getAnyProfile(player).getStats().getLevel();

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
        }.runTaskTimer(main, 0L, 1);

    }

    private void basicStage3(Player player){

        LivingEntity target = targetManager.getPlayerTarget(player);

        basicStageMap.put(player.getUniqueId(), 1);

        double skillDamage = 1;
        double skillLevel = profileManager.getAnyProfile(player).getStats().getLevel();

        skillDamage = skillDamage + ((int)(skillLevel/10));

        double finalSkillDamage = skillDamage;
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

                Location playerLoc = player.getLocation().clone().subtract(0,1,0);

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
                        current.add(direction.normalize().multiply(distanceThisTick));

                        ArmorStand armorStand = player.getWorld().spawn(current, ArmorStand.class);
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
                        current.add(direction.normalize().multiply(distanceThisTick));
                        thisStand.teleport(current);
                    }

                }

                ran++;

                if(ran >=20){
                    cancelTask();

                    double level = profileManager.getAnyProfile(player).getStats().getLevel();

                    boolean crit = damageCalculator.checkIfCrit(player, 0);
                    double damage = damageCalculator.calculateDamage(player, target, "Magical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, player);

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


}
