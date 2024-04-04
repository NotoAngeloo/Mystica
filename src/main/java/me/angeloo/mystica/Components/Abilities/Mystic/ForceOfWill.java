package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ForceOfWill {

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

    public ForceOfWill(Mystica main, AbilityManager manager){
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


        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 15);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    cooldownDisplayer.displayCooldown(player, 3);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 3);

            }
        }.runTaskTimer(main, 0,20);
    }

    private void execute(Player player){

        LivingEntity target = targetManager.getPlayerTarget(player);



        abilityManager.setCasting(player, true);
        double castTime = 4;
        castTime = castTime - buffAndDebuffManager.getHaste().getHasteLevel(player);
        castTime = castTime * 20;

        double skillDamage = getSkillDamage(player)/castTime;

        double finalCastTime = castTime;
        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone();
            int count = 0;
            @Override
            public void run(){

                if(!player.isOnline() || buffAndDebuffManager.getIfInterrupt(player)){
                    this.cancel();
                    abilityManager.setCasting(player, false);
                    abilityManager.setCastBar(player, 0);
                    return;
                }

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation();
                    targetWasLoc = targetLoc.clone();
                }

                Location start = player.getLocation().clone();

                double distanceToTarget = start.distance(targetWasLoc);

                if(distanceToTarget>getRange(player)){
                    this.cancel();
                    abilityManager.setCasting(player, false);
                    abilityManager.setCastBar(player, 0);
                    return;
                }

                if (!sameWorld(start, targetWasLoc)) {
                    this.cancel();
                    abilityManager.setCasting(player, false);
                    abilityManager.setCastBar(player, 0);
                    return;
                }

                Location current = player.getLocation().clone().add(0,.5,0);

                for(double i = 0; i<distanceToTarget;i+=.75){

                    Vector direction = targetWasLoc.toVector().subtract(start.toVector());
                    double distanceThisTick = Math.min(distanceToTarget, .75);
                    current.add(direction.normalize().multiply(distanceThisTick));

                    player.getWorld().spawnParticle(Particle.SPELL_WITCH, current, 1, 0, 0, 0, 0);

                }

                double percent = ((double) count / finalCastTime) * 100;

                abilityManager.setCastBar(player, percent);

                if(count >= finalCastTime){
                    this.cancel();
                    abilityManager.setCasting(player, false);
                    abilityManager.setCastBar(player, 0);
                }

                boolean crit = damageCalculator.checkIfCrit(player, 0);
                double damage = damageCalculator.calculateDamage(player, target, "Magical", skillDamage, crit);
                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                changeResourceHandler.subtractHealthFromEntity(target, damage, player);

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

        }.runTaskTimer(main, 0, 1);

    }

    private double getRange(Player player){
        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        return baseRange + extraRange;
    }

    public double getCost(){
        return 5;
    }

    public double getSkillDamage(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_3_Level_Bonus();
        return 20 + ((int)(skillLevel/10));
    }

    public int getCooldown(Player player){

        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }
}
