package me.angeloo.mystica.Components.Abilities.Paladin;

import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageCalculator;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpiritualGift {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final DamageCalculator damageCalculator;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public SpiritualGift(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        damageCalculator = main.getDamageCalculator();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
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

        double cost = 10;

        if(profileManager.getAnyProfile(player).getCurrentMana()<cost){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, cost);

        combatManager.startCombatTimer(player);

        execute(player, target);

        abilityReadyInMap.put(player.getUniqueId(), 20);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    cooldownDisplayer.displayCooldown(player, 5);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 5);

            }
        }.runTaskTimer(main, 0,20);

    }


    private double getRange(Player player){
        double baseRange = 12;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        return baseRange + extraRange;
    }

    private void execute(Player player, LivingEntity target){

        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_5_Level_Bonus();
        double healPercent = 5;
        healPercent = healPercent +  ((int)(skillLevel/10));

        //every 15 levels is a +1
        int bonusDuration = (int)(skillLevel/15);

        int duration = (5*20) + (bonusDuration*20);

        buffAndDebuffManager.getHaste().applyHaste(target, 3, duration);

        double finalHealPercent = healPercent;
        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(!targetStillValid(target)){
                    this.cancel();
                    return;
                }

                Location center = target.getLocation().clone().add(0,1,0);

                double increment = (2 * Math.PI) / 16; // angle between particles

                for (int i = 0; i < 16; i++) {
                    double angle = i * increment;
                    double x = center.getX() + (1 * Math.cos(angle));
                    double z = center.getZ() + (1 * Math.sin(angle));
                    Location loc = new Location(center.getWorld(), x, (center.getY()), z);

                    target.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
                }

                if(count>=duration){
                    this.cancel();
                    healTarget(target);
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

            private void healTarget(LivingEntity target){

                if(!targetStillValid(target)){
                    return;
                }

                boolean crit = damageCalculator.checkIfCrit(player, 0);
                double healAmount = damageCalculator.calculateHealing(target, player, finalHealPercent, crit);

                changeResourceHandler.addHealthToEntity(target, healAmount, player);
            }

        }.runTaskTimer(main, 0, 1);

    }

    public int getCooldown(Player player){

        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }


}
