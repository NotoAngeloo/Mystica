package me.angeloo.mystica.Components.Abilities.None;

import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Bandage {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final TargetManager targetManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public Bandage(Mystica main, AbilityManager manager){
        this.main = main;
        this.abilityManager = manager;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    private final double cost = 20;

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }


        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        if(target == null){
            target = caster;
        }

        changeResourceHandler.subTractManaFromEntity(caster, cost);

        combatManager.startCombatTimer(caster);

        execute(caster, target);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 16);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 6);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 6);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster, LivingEntity target){

        abilityManager.setCasting(caster, true);
        int castTime = 40;

        if(caster instanceof Player){
            ((Player)caster).setWalkSpeed(.03f);
        }


        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone();
            int ran = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        abilityManager.setCasting(caster, false);
                        ((Player)caster).setWalkSpeed(.2f);
                    }
                }

                if(buffAndDebuffManager.getIfInterrupt(caster)){
                    this.cancel();
                    abilityManager.setCasting(caster, false);

                    if(caster instanceof Player){
                        ((Player)caster).setWalkSpeed(.2f);
                    }

                    return;
                }

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation();
                    targetLoc = targetLoc.subtract(0,1,0);
                    targetWasLoc = targetLoc.clone();
                }

                double distanceToTarget = caster.getLocation().distance(targetWasLoc);

                if(distanceToTarget>8){
                    this.cancel();
                    abilityManager.setCasting(caster, false);

                    if(caster instanceof Player){
                        ((Player)caster).setWalkSpeed(.2f);
                    }
                    return;
                }

                if(ran%2==0){
                    healTarget(target);
                }


                double percent = ((double) ran / castTime) * 100;
                abilityManager.setCastBar(caster, percent);

                if(ran >= castTime){
                    this.cancel();
                    abilityManager.setCasting(caster, false);

                    if(caster instanceof Player){
                        ((Player)caster).setWalkSpeed(.2f);
                    }
                }


                ran++;
            }

            private void healTarget(LivingEntity target){

                double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                        profileManager.getAnyProfile(caster).getSkillLevels().getSkill_6_Level_Bonus();
                double healPercent = 1;
                healPercent = healPercent + ((int)(skillLevel/10));

                double healAmount = profileManager.getAnyProfile(target).getTotalHealth() * (healPercent / 100);
                
                changeResourceHandler.addHealthToEntity(target, healAmount, caster);

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

    public int getCooldown(LivingEntity caster){

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

            if(!(target instanceof Player)){

                if(!pveChecker.pveLogic(target)){
                    target = caster;
                }
            }

            double distance = caster.getLocation().distance(target.getLocation());

            if(distance > 8){
                return false;
            }

            if (profileManager.getAnyProfile(target).getIfDead()) {
                target = caster;
            }

            if(target instanceof Player){
                if(pvpManager.pvpLogic(caster, (Player) target)){
                    target = caster;
                }
            }


        }

        if(target == null){
            target = caster;
        }

        if(getCooldown(caster) > 0){
            return false;
        }

        if(profileManager.getAnyProfile(caster).getCurrentMana()<cost){
            return false;
        }

        return true;
    }

}
