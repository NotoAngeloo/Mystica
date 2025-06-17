package me.angeloo.mystica.Components.Abilities.ShadowKnight;

import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
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

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();


    //player, who they infected
    private final Map<UUID, LivingEntity> infectionTarget = new HashMap<>();
    //player, timeleft
    private final Map<UUID, Integer> infectionTime = new HashMap<>();
    //player, task
    private final Map<UUID, BukkitTask> infectionTask = new HashMap<>();


    private final Map<UUID, Boolean> enhanced = new HashMap<>();
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

    private final double range = 10;

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        targetManager.setTargetToNearestValid(caster, range + buffAndDebuffManager.getTotalRangeModifier(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 3);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 1);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 1);
            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        Location start = caster.getLocation();
        start.subtract(0, 1, 0);


        ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
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
            int count = 0;
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

                caster.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, current.add(0,1,0), 1, 0, 0, 0, 0);


                if (distance <= 1) {
                    cancelTask();
                    startOrResetInfection(caster, target);
                }

                if(count>100){
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

            private boolean sameWorld(Location loc1, Location loc2) {
                return loc1.getWorld().equals(loc2.getWorld());
            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
            }
        }.runTaskTimer(main, 0, 1);
    }

    private void startOrResetInfection(LivingEntity caster, LivingEntity entity){

        infectionTarget.put(caster.getUniqueId(), entity);
        infectionTime.put(caster.getUniqueId(), getDuration());

        if(infectionTask.containsKey(caster.getUniqueId())){
            infectionTask.get(caster.getUniqueId()).cancel();
        }

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                int timeLeft = getPlayerInfectionTime(caster);

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

                double skillDamage = getSkillDamage(caster);

                if(getIfEnhanced(caster)){
                    skillDamage = skillDamage * 2;
                }


                boolean crit = damageCalculator.checkIfCrit(caster, 0);
                double damage = damageCalculator.calculateDamage(caster, entity, "Physical", skillDamage, crit);
                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(entity, caster));
                changeResourceHandler.subtractHealthFromEntity(entity, damage, caster, crit);

                //Bukkit.getLogger().info("damaging");

                timeLeft --;

                infectionTime.put(caster.getUniqueId(), timeLeft);

                if(caster instanceof Player){
                    Player player = (Player) caster;
                    Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status"));
                }


            }

            private void cancelTask(){
                this.cancel();
                infectionTime.remove(caster.getUniqueId());
                infectionTarget.remove(caster.getUniqueId());
                infectionTask.remove(caster.getUniqueId());
                if(caster instanceof Player){
                    Player player = (Player) caster;
                    Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status"));
                }
            }

        }.runTaskTimer(main, 20, 20);

        infectionTask.put(caster.getUniqueId(), task);
    }

    public int getPlayerInfectionTime(LivingEntity caster){
        return infectionTime.getOrDefault(caster.getUniqueId(), 0);
    }



    public boolean getIfEnhanced(LivingEntity caster){
        return enhanced.getOrDefault(caster, false);
    }

    public boolean getIfThisPlayerInfectThisEntity(LivingEntity caster, LivingEntity entity){
        if(infectionTarget.containsKey(caster.getUniqueId())){
            return infectionTarget.get(caster.getUniqueId()) == entity;
        }

        return false;
    }

    public void infectionEnhancement(LivingEntity caster, LivingEntity entity){
        enhanced.put(caster.getUniqueId(), true);
        startOrResetInfection(caster, entity);

        if(enhancedTaskMap.containsKey(caster.getUniqueId())){
            enhancedTaskMap.get(caster.getUniqueId()).cancel();
        }

        if(caster instanceof Player){
            Player player = (Player) caster;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status"));
        }

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){
                enhanced.remove(caster.getUniqueId());
                if(caster instanceof Player){
                    Player player = (Player) caster;
                    Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status"));
                }
            }
        }.runTaskLater(main, 20*10);

        enhancedTaskMap.put(caster.getUniqueId(), task);
    }


    public double soulReapToRemove(LivingEntity caster){

        double damage = 6;

        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_1_Level_Bonus();

        double time = getPlayerInfectionTime(caster);

        double total = damage * skillLevel * time;

        infectionTime.remove(caster.getUniqueId());
        infectionTarget.remove(caster.getUniqueId());
        infectionTask.remove(caster.getUniqueId());

        return total;
    }

    public void removeEnhancement(LivingEntity caster){
        enhanced.put(caster.getUniqueId(), false);
    }


    public int getCooldown(LivingEntity caster){
        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_1_Level_Bonus();
        return 5 + ((int)(skillLevel/3));
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

            if(distance > range + buffAndDebuffManager.getTotalRangeModifier(caster)){
                return false;
            }

            if(distance<1){
                return false;
            }
        }

        if(target == null){
            return false;
        }

        return getCooldown(caster) <= 0;
    }

    public int getDuration(){
        return 11;
    }

}
