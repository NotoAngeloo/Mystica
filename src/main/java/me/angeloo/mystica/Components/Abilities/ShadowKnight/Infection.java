package me.angeloo.mystica.Components.Abilities.ShadowKnight;

import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.CustomEvents.StatusUpdateEvent;
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

import java.util.HashMap;

import java.util.Map;
import java.util.UUID;

public class Infection {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    //who is infected, who started it, time left
    //private final Map<UUID, Map<Player, Integer>> infections = new HashMap<>();

    //player, who they infected
    private final Map<UUID, LivingEntity> infectionTarget = new HashMap<>();
    //player, timeleft
    private final Map<UUID, Integer> infectionTime = new HashMap<>();
    //player, task
    private final Map<UUID, BukkitTask> infectionTask = new HashMap<>();


    private final Map<Player, Boolean> enhanced = new HashMap<>();
    private final Map<UUID, BukkitTask> enhancedTaskMap = new HashMap<>();

    public Infection(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
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

        double baseRange = 10;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        double totalRange = baseRange + extraRange;

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

        abilityReadyInMap.put(player.getUniqueId(), 3);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    cooldownDisplayer.displayCooldown(player, 1);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 1);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        LivingEntity target = targetManager.getPlayerTarget(player);

        Location start = player.getLocation();
        start.subtract(0, 1, 0);


        ArmorStand armorStand = player.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack boltItem = new ItemStack(Material.REDSTONE);
        ItemMeta meta = boltItem.getItemMeta();
        assert meta != null;

        meta.setCustomModelData(4);

        boltItem.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(boltItem);


        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone();
            @Override
            public void run(){

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation();
                    targetLoc = targetLoc.subtract(0,1,0);
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

                if(distanceThisTick!=0){
                    current.add(direction.normalize().multiply(distanceThisTick));
                }

                current.setDirection(direction);

                armorStand.teleport(current);

                player.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, current.add(0,1,0), 1, 0, 0, 0, 0);


                if (distance <= 1) {
                    cancelTask();
                    startOrResetInfection(player, target);
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

    private void startOrResetInfection(Player player, LivingEntity entity){

        infectionTarget.put(player.getUniqueId(), entity);
        infectionTime.put(player.getUniqueId(), 11);

        if(infectionTask.containsKey(player.getUniqueId())){
            infectionTask.get(player.getUniqueId()).cancel();
        }

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                int timeLeft = getPlayerInfectionTime(player);

                if(timeLeft <=0 ){
                    cancelTask();
                    return;
                }

                if(entity.isDead()){
                    cancelTask();
                    return;
                }

                if(entity instanceof Player){

                    if(profileManager.getAnyProfile(entity).getIfDead()){
                        cancelTask();
                        return;
                    }

                    if(!((Player) entity).isOnline()){
                        cancelTask();
                        return;
                    }
                }

                if(profileManager.getIfResetProcessing(entity)){
                    cancelTask();
                    return;
                }

                double skillDamage = 7;
                double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                        profileManager.getAnyProfile(player).getSkillLevels().getSkill_1_Level_Bonus();
                skillDamage = skillDamage + ((int)(skillLevel/10));

                if(getIfEnhanced(player)){
                    skillDamage = skillDamage * 2;
                }


                boolean crit = damageCalculator.checkIfCrit(player, 0);
                double damage = damageCalculator.calculateDamage(player, entity, "Physical", skillDamage, crit);
                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(entity, player));
                changeResourceHandler.subtractHealthFromEntity(entity, damage, player);

                //Bukkit.getLogger().info("damaging");

                timeLeft --;

                infectionTime.put(player.getUniqueId(), timeLeft);

                Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));

            }

            private void cancelTask(){
                this.cancel();
                infectionTime.remove(player.getUniqueId());
                infectionTarget.remove(player.getUniqueId());
                infectionTask.remove(player.getUniqueId());
                Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
            }

        }.runTaskTimer(main, 20, 20);

        infectionTask.put(player.getUniqueId(), task);
    }

    public int getPlayerInfectionTime(Player player){
        return infectionTime.getOrDefault(player.getUniqueId(), 0);
    }



    public boolean getIfEnhanced(Player player){
        return enhanced.getOrDefault(player, false);
    }

    public boolean getIfThisPlayerInfectThisEntity(Player player, LivingEntity entity){
        if(infectionTarget.containsKey(player.getUniqueId())){
            return infectionTarget.get(player.getUniqueId()) == entity;
        }

        return false;
    }

    public void infectionEnhancement(Player player, LivingEntity entity){
        enhanced.put(player, true);
        startOrResetInfection(player, entity);

        if(enhancedTaskMap.containsKey(player.getUniqueId())){
            enhancedTaskMap.get(player.getUniqueId()).cancel();
        }

        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){
                enhanced.remove(player);
                Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
            }
        }.runTaskLater(main, 20*10);

        enhancedTaskMap.put(player.getUniqueId(), task);
    }


    public double soulReapToRemove(Player player){

        double damage = 6;

        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_1_Level_Bonus();

        double time = getPlayerInfectionTime(player);

        double total = damage * skillLevel * time;

        infectionTime.remove(player.getUniqueId());
        infectionTarget.remove(player.getUniqueId());
        infectionTask.remove(player.getUniqueId());

        return total;
    }

    public void removeEnhancement(Player player){
        enhanced.put(player, false);
    }


    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }


}
