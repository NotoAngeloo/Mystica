package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.CustomEvents.UltimateStatusChageEvent;
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

public class ArcaneMissiles {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final AbilityManager abilityManager;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public ArcaneMissiles(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = manager;
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
    }

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        double totalRange = getRange(caster);

        targetManager.setTargetToNearestValid(caster, totalRange);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }


        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), getSkillCooldown());
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getPlayerCooldown(caster) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = getPlayerCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);
                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                if(caster instanceof Player){
                    Bukkit.getScheduler().runTask(main, () ->{
                        Bukkit.getServer().getPluginManager().callEvent(new UltimateStatusChageEvent((Player) caster));
                    });
                }

            }
        }.runTaskTimerAsynchronously(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private double getRange(LivingEntity caster){
        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(caster);
        return baseRange + extraRange;
    }

    private void execute(LivingEntity caster){
        LivingEntity target = targetManager.getPlayerTarget(caster);

        double skillDamage = getSkillDamage(caster);

        double ticks = 10;
        ticks = ticks - buffAndDebuffManager.getHaste().getHasteLevel(caster);

        skillDamage = skillDamage / ticks;


        double finalSkillDamage = skillDamage;
        double finalTicks = ticks;
        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone();
            final Set<ArmorStand> allStands = new HashSet<>();
            int count = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                        return;
                    }
                }

                if(buffAndDebuffManager.getIfInterrupt(caster)){
                    cancelTask();
                    return;
                }

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation();
                    targetLoc = targetLoc.subtract(0,1,0);
                    targetWasLoc = targetLoc.clone();
                }

                Location start = caster.getLocation();
                start.subtract(0, 1, 0);

                double distanceToTarget = start.distance(targetWasLoc);

                if(distanceToTarget>getRange(caster)){
                    cancelTask();
                    return;
                }

                Vector direction = caster.getLocation().getDirection().normalize();
                Location spawn1Loc = start.clone();
                spawn1Loc.subtract(direction.clone().crossProduct(new Vector(0,1,0).normalize().multiply(.5)));
                Location spawn2Loc = start.clone();
                spawn2Loc.add(direction.clone().crossProduct(new Vector(0,1,0).normalize().multiply(.5)));

                ArmorStand armorStand = caster.getWorld().spawn(spawn1Loc, ArmorStand.class);
                armorStand.setInvisible(true);
                armorStand.setGravity(false);
                armorStand.setCollidable(false);
                armorStand.setInvulnerable(true);
                armorStand.setMarker(true);

                EntityEquipment entityEquipment = armorStand.getEquipment();

                ItemStack boltItem = new ItemStack(Material.SPECTRAL_ARROW);
                ItemMeta meta = boltItem.getItemMeta();
                assert meta != null;
                meta.setCustomModelData(1);
                boltItem.setItemMeta(meta);
                assert entityEquipment != null;
                entityEquipment.setHelmet(boltItem);

                allStands.add(armorStand);

                ArmorStand armorStand2 = caster.getWorld().spawn(spawn2Loc, ArmorStand.class);
                armorStand2.setInvisible(true);
                armorStand2.setGravity(false);
                armorStand2.setCollidable(false);
                armorStand2.setInvulnerable(true);
                armorStand2.setMarker(true);

                EntityEquipment entityEquipment2 = armorStand2.getEquipment();
                assert entityEquipment2 != null;
                entityEquipment2.setHelmet(boltItem);

                allStands.add(armorStand2);

                new BukkitRunnable() {
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

                        org.bukkit.util.Vector direction = targetWasLoc.toVector().subtract(current.toVector());
                        double distance = current.distance(targetWasLoc);
                        double distanceThisTick = Math.min(distance, 1);
                        current.add(direction.normalize().multiply(distanceThisTick));

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

                new BukkitRunnable() {
                    Location targetWasLoc = target.getLocation().clone().subtract(0,1,0);
                    @Override
                    public void run() {

                        if(targetStillValid(target)){
                            Location targetLoc = target.getLocation().clone().subtract(0,1,0);
                            targetWasLoc = targetLoc.clone();
                        }

                        Location current = armorStand2.getLocation();

                        if (!sameWorld(current, targetWasLoc)) {
                            cancelTask();
                            return;
                        }

                        org.bukkit.util.Vector direction = targetWasLoc.toVector().subtract(current.toVector());
                        double distance = current.distance(targetWasLoc);
                        double distanceThisTick = Math.min(distance, 1);
                        current.add(direction.normalize().multiply(distanceThisTick));

                        armorStand2.teleport(current);


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
                        armorStand2.remove();
                    }
                }.runTaskTimer(main, 0L, 1);

                if(count >= finalTicks){
                    cancelTask();
                }


                count++;
            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){

                    if(!((Player) target).isOnline()){
                        return false;
                    }

                }

                return !target.isDead();
            }

            private void cancelTask(){
                this.cancel();
                removeStands();
                abilityManager.setCasting(caster, false);

                if(caster instanceof Player){
                    abilityManager.setCastBar((Player) caster, 0);
                }

            }

            private void removeStands(){
                for(ArmorStand stand : allStands){
                    stand.remove();
                }
            }



        }.runTaskTimer(main, 0, 6);
    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getStats().getLevel();
        return 70 + ((int)(skillLevel/3));
    }

    public int getSkillCooldown(){
        return 15;
    }

    public int getPlayerCooldown(LivingEntity caster){

        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

    public boolean usable(LivingEntity caster, LivingEntity target){
        if(target != null){
            if(target instanceof Player){
                if(!pvpManager.pvpLogic(caster, (Player) target)){
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
        }

        if(target == null){
            return false;
        }

        return getPlayerCooldown(caster) <= 0;
    }

}
