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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

public class ChaosLash {

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

    private final PlagueCurse plagueCurse;
    private final EvilSpirit evilSpirit;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public ChaosLash(Mystica main, AbilityManager manager, MysticAbilities mysticAbilities){
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
        evilSpirit = mysticAbilities.getEvilSpirit();
        plagueCurse = mysticAbilities.getPlagueCurse();
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

        if(getCooldown(player) > 0){
            return;
        }

        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);

        execute(player);

        if(cooldownTask.containsKey(player.getUniqueId())){
            cooldownTask.get(player.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(player.getUniqueId(), 11);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(player) <= 0){
                    cooldownDisplayer.displayCooldown(player, 6);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(player) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);

                cooldownDisplayer.displayCooldown(player, 6);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(player.getUniqueId(), task);

    }

    private double getRange(Player player){
        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        return baseRange + extraRange;
    }

    private void execute(Player player){

        LivingEntity target = targetManager.getPlayerTarget(player);

        if(plagueCurse.getIfCursed(target)){
            evilSpirit.addChaosShard(player, 2);
        }


        double castTime = 15;
        castTime = castTime - buffAndDebuffManager.getHaste().getHasteLevel(player);

        double skillDamage = getSkillDamage(player);

        skillDamage = skillDamage / castTime;

        abilityManager.setCasting(player, true);
        double finalSkillDamage = skillDamage;
        double finalCastTime = castTime;
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

                ArmorStand armorStand = player.getWorld().spawn(start, ArmorStand.class);
                armorStand.setInvisible(true);
                armorStand.setGravity(false);
                armorStand.setCollidable(false);
                armorStand.setInvulnerable(true);
                armorStand.setMarker(true);

                EntityEquipment entityEquipment = armorStand.getEquipment();

                ItemStack boltItem = new ItemStack(Material.SPECTRAL_ARROW);
                ItemMeta meta = boltItem.getItemMeta();
                assert meta != null;
                meta.setCustomModelData(9);
                boltItem.setItemMeta(meta);
                assert entityEquipment != null;
                entityEquipment.setHelmet(boltItem);

                allStands.add(armorStand);

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

                            armorStand.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, current.add(0,2,0), 1, 0, 0, 0, 0);

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

                double percent = ((double) count / finalCastTime) * 100;

                abilityManager.setCastBar(player, percent);

                if(count >= finalCastTime){
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

    public double getSkillDamage(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_6_Level_Bonus();
        return 50 + ((int)(skillLevel/3));
    }

    public double getCost(){
        return 20;
    }

    public int getCooldown(Player player){

        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public void resetCooldown(Player player){
        abilityReadyInMap.remove(player.getUniqueId());
    }

}
