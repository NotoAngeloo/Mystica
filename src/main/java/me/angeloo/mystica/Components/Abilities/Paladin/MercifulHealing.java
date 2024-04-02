package me.angeloo.mystica.Components.Abilities.Paladin;

import me.angeloo.mystica.Components.Abilities.PaladinAbilities;
import me.angeloo.mystica.CustomEvents.StatusUpdateEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageCalculator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MercifulHealing {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final DamageCalculator damageCalculator;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final AbilityManager abilityManager;
    private final CooldownDisplayer cooldownDisplayer;

    private final JusticeMark justiceMark;

    private final Map<UUID, Boolean> moveCast = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public MercifulHealing(Mystica main, AbilityManager manager, PaladinAbilities paladinAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        damageCalculator = main.getDamageCalculator();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        abilityManager = manager;
        cooldownDisplayer= new CooldownDisplayer(main, manager);
        justiceMark = paladinAbilities.getJusticeMark();
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }


        LivingEntity target = targetManager.getPlayerTarget(player);

        if(target != null){

            if(!(target instanceof Player)){
                target = player;
            }

            double distance = player.getLocation().distance(target.getLocation());

            if(distance > getRange(player)){
                return;
            }

            if (profileManager.getAnyProfile(target).getIfDead()) {
                target = player;
            }

            if(pvpManager.pvpLogic(player, (Player) target)){
                target = player;
            }

        }

        if(target == null){
            target = player;
        }

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        double cost = 20;

        if(profileManager.getAnyProfile(player).getCurrentMana()<cost){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, cost);

        combatManager.startCombatTimer(player);

        execute(player, target);

        abilityReadyInMap.put(player.getUniqueId(), 7);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    cooldownDisplayer.displayCooldown(player, 2);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 2);

            }
        }.runTaskTimer(main, 0,20);

    }

    private double getRange(Player player){
        double baseRange = 10;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        return baseRange + extraRange;
    }

    private void execute(Player player, LivingEntity target){

        abilityManager.setCasting(player, true);
        int castTime = 20;

        if(getMoveCast(player)){
            unQueueMoveCast(player);
        }
        else{
            buffAndDebuffManager.getImmobile().applyImmobile(player, castTime);
        }

        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone();
            int ran = 0;
            @Override
            public void run(){

                if(!player.isOnline() || buffAndDebuffManager.getIfInterrupt(player)){
                    this.cancel();
                    abilityManager.setCasting(player, false);
                    buffAndDebuffManager.getImmobile().removeImmobile(player);
                    return;
                }

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation();
                    targetLoc = targetLoc.subtract(0,1,0);
                    targetWasLoc = targetLoc.clone();
                }

                double distanceToTarget = player.getLocation().distance(targetWasLoc);

                if(distanceToTarget>getRange(player)){
                    this.cancel();
                    abilityManager.setCasting(player, false);
                    buffAndDebuffManager.getImmobile().removeImmobile(player);
                    return;
                }

                double percent = ((double) ran / castTime) * 100;
                abilityManager.setCastBar(player, percent);

                if(ran >= castTime){
                    this.cancel();
                    abilityManager.setCasting(player, false);
                    healTarget(target);
                    buffAndDebuffManager.getImmobile().removeImmobile(player);
                }

                ran++;
            }

            private void healTarget(LivingEntity target){

                double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                        profileManager.getAnyProfile(player).getSkillLevels().getSkill_2_Level_Bonus();
                double healPercent = 10;
                healPercent = healPercent+ ((int)(skillLevel/10));

                boolean crit = damageCalculator.checkIfCrit(player, 0);

                double healAmount = damageCalculator.calculateHealing(target, player, healPercent, crit);

                if(justiceMark.markProc(player, target)){
                    markHealInstead(player, healAmount);
                    unQueueMoveCast(player);
                    return;
                }

                changeResourceHandler.addHealthToEntity(target, healAmount, player);
                unQueueMoveCast(player);

                Location center = target.getLocation().clone().add(0,1,0);

                double increment = (2 * Math.PI) / 16; // angle between particles

                for (int i = 0; i < 16; i++) {
                    double angle = i * increment;
                    double x = center.getX() + (1 * Math.cos(angle));
                    double z = center.getZ() + (1 * Math.sin(angle));
                    Location loc = new Location(center.getWorld(), x, (center.getY()), z);

                    target.getWorld().spawnParticle(Particle.HEART, loc, 1,0, 0, 0, 0);
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

        }.runTaskTimer(main, 0, 1);

    }

    private void markHealInstead(Player player, double healAmount){

        List<LivingEntity> affected = justiceMark.getMarkedTargets(player);

        for(LivingEntity thisPlayer : affected){
            changeResourceHandler.addHealthToEntity(thisPlayer, healAmount, player);

            Location center = thisPlayer.getLocation().clone().add(0,1,0);

            double increment = (2 * Math.PI) / 16; // angle between particles

            for (int i = 0; i < 16; i++) {
                double angle = i * increment;
                double x = center.getX() + (1 * Math.cos(angle));
                double z = center.getZ() + (1 * Math.sin(angle));
                Location loc = new Location(center.getWorld(), x, (center.getY()), z);

                thisPlayer.getWorld().spawnParticle(Particle.HEART, loc, 1,0, 0, 0, 0);
            }
        }

    }

    public void queueMoveCast(Player player){
        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
        moveCast.put(player.getUniqueId(), true);
    }
    public void unQueueMoveCast(Player player){
        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
        moveCast.remove(player.getUniqueId());
    }
    private boolean getMoveCast(Player player){
        return moveCast.getOrDefault(player.getUniqueId(), false);
    }

    public int getCooldown(Player player){

        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
