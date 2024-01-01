package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.Components.Abilities.MysticAbilities;
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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
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
    private final CooldownDisplayer cooldownDisplayer;

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
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double totalRange = getRange(player);

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

        abilityReadyInMap.put(player.getUniqueId(), 15);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    cooldownDisplayer.displayUltimateCooldown(player);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayUltimateCooldown(player);
            }
        }.runTaskTimer(main, 0,20);

    }

    private double getRange(Player player){
        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        return baseRange + extraRange;
    }

    private void execute(Player player){
        LivingEntity target = targetManager.getPlayerTarget(player);

        double skillDamage = 8;
        double skillLevel = profileManager.getAnyProfile(player).getStats().getLevel();
        skillDamage = skillDamage + ((int)(skillLevel/10));

        double finalSkillDamage = skillDamage;
        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone();
            final Set<ArmorStand> allStands = new HashSet<>();
            int count = 0;
            @Override
            public void run(){

                if(!player.isOnline() || buffAndDebuffManager.getIfInterrupt(player)){
                    cancelTask();
                    return;
                }

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation();
                    targetLoc = targetLoc.subtract(0,1,0);
                    targetWasLoc = targetLoc.clone();
                }

                Location start = player.getLocation();
                start.subtract(0, 1, 0);

                double distanceToTarget = start.distance(targetWasLoc);

                if(distanceToTarget>getRange(player)){
                    cancelTask();
                    return;
                }

                Vector direction = player.getLocation().getDirection().normalize();
                Location spawn1Loc = start.clone();
                spawn1Loc.subtract(direction.clone().crossProduct(new Vector(0,1,0).normalize().multiply(.5)));
                Location spawn2Loc = start.clone();
                spawn2Loc.add(direction.clone().crossProduct(new Vector(0,1,0).normalize().multiply(.5)));

                ArmorStand armorStand = player.getWorld().spawn(spawn1Loc, ArmorStand.class);
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

                ArmorStand armorStand2 = player.getWorld().spawn(spawn2Loc, ArmorStand.class);
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
                        armorStand2.remove();
                    }
                }.runTaskTimer(main, 0L, 1);

                if(count >= 10){
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
                abilityManager.setCasting(player, false);
                abilityManager.setCastBar(player, 0);
            }

            private void removeStands(){
                for(ArmorStand stand : allStands){
                    stand.remove();
                }
            }



        }.runTaskTimer(main, 0, 6);
    }

    public int getCooldown(Player player){

        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
