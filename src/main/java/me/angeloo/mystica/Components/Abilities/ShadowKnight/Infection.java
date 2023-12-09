package me.angeloo.mystica.Components.Abilities.ShadowKnight;

import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
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

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    //who is infected, who started it, time left
    private final Map<UUID, Map<Player, Integer>> infections = new HashMap<>();
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
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double baseRange = 8;
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

        abilityReadyInMap.put(player.getUniqueId(), 2);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        LivingEntity target = targetManager.getPlayerTarget(player);

        Location start = player.getLocation();
        start.subtract(0, 1, 0);


        ArmorStand armorStand = start.getWorld().spawn(start, ArmorStand.class);
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
                current.add(direction.normalize().multiply(distanceThisTick));
                current.setDirection(direction);

                armorStand.teleport(current);

                current.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, current.add(0,1,0), 1, 0, 0, 0, 0);


                if (distance <= 1) {
                    cancelTask();

                    //start or reset infection time
                    //infection needs 1. who is infected, 2. who infected them, 3. if it is enhanced

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

        if(!infections.containsKey(entity.getUniqueId())){
            infections.put(entity.getUniqueId(), new HashMap<>());
        }

        Map<Player, Integer> thisInfectionInstance = infections.get(entity.getUniqueId());

        if(!thisInfectionInstance.containsKey(player)){
            startInfectionTask(player, entity);
        }

        thisInfectionInstance.put(player, 10);

        infections.put(entity.getUniqueId(), thisInfectionInstance);

    }

    private void startInfectionTask(Player player, LivingEntity entity){

        new BukkitRunnable(){
            @Override
            public void run(){

                int timeLeft = getTimeLeftOfThisInfection(entity, player);

                if(timeLeft <=0 ){
                    this.cancel();
                    infections.get(entity.getUniqueId()).remove(player);
                    return;
                }

                if(entity.isDead()){
                    this.cancel();
                    infections.get(entity.getUniqueId()).remove(player);
                    return;
                }

                if(profileManager.getAnyProfile(entity).getIfDead()){
                    this.cancel();
                    infections.get(entity.getUniqueId()).remove(player);
                    return;
                }

                if(entity instanceof Player){
                    if(!((Player) entity).isOnline()){
                        this.cancel();
                        infections.get(entity.getUniqueId()).remove(player);
                        return;
                    }
                }

                double skillDamage = 3;

                if(getIfEnhanced(player)){
                    skillDamage = skillDamage * 2;
                }

                double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_1_Level() +
                        profileManager.getAnyProfile(player).getSkillLevels().getSkill_1_Level_Bonus();

                boolean crit = damageCalculator.checkIfCrit(player, 0);
                double damage = damageCalculator.calculateDamage(player, entity, "Physical", skillDamage * skillLevel, crit);
                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(entity, player));
                changeResourceHandler.subtractHealthFromEntity(entity, damage, player);

                timeLeft --;

                infections.get(entity.getUniqueId()).put(player, timeLeft);

            }
        }.runTaskTimer(main, 20, 20);

    }

    public int getTimeLeftOfThisInfection(LivingEntity entity, Player player){

        if(!infections.containsKey(entity.getUniqueId())){
            return 0;
        }

        Map<Player, Integer> thisInfectionInstance = infections.get(entity.getUniqueId());

        return thisInfectionInstance.getOrDefault(player, 0);
    }

    public boolean getIfEnhanced(Player player){
        return enhanced.getOrDefault(player, false);
    }

    public boolean getIfThisPlayerInfectThisEntity(Player player, LivingEntity entity){

        if(!infections.containsKey(entity.getUniqueId())){
            return false;
        }

        Map<Player, Integer> entityMap = infections.get(entity.getUniqueId());

        if(!entityMap.containsKey(player)){
            return false;
        }

        return entityMap.get(player) > 0;
    }

    public void infectionEnhancement(Player player, LivingEntity entity){
        enhanced.put(player, true);
        startOrResetInfection(player, entity);

        if(enhancedTaskMap.containsKey(player.getUniqueId())){
            enhancedTaskMap.get(player.getUniqueId()).cancel();
        }

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){
                enhanced.remove(player);
            }
        }.runTaskLater(main, 20*10);

        enhancedTaskMap.put(player.getUniqueId(), task);
    }


    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }


}
