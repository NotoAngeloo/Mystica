package me.angeloo.mystica.Components.CombatSystem.Abilities.Paladin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.PaladinAbilities;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.BuffAndDebuffManager;
import me.angeloo.mystica.Components.CombatSystem.CombatManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.Enums.BarType;
import me.angeloo.mystica.Components.Hud.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

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
    private final PveChecker pveChecker;
    private final PvpManager pvpManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final AbilityManager abilityManager;
    private final CooldownDisplayer cooldownDisplayer;

    private final Purity purity;
    private final JusticeMark justiceMark;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Boolean> moveCast = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public MercifulHealing(Mystica main, AbilityManager manager, PaladinAbilities paladinAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        damageCalculator = main.getDamageCalculator();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        abilityManager = manager;
        cooldownDisplayer= new CooldownDisplayer(main, manager);
        purity = paladinAbilities.getPurity();
        justiceMark = paladinAbilities.getJusticeMark();
    }

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

        execute(caster, target);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 7);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 2);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 2);

            }
        }.runTaskTimerAsynchronously(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private double getRange(LivingEntity caster){
        double baseRange = 10;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(caster);
        return baseRange + extraRange;
    }

    private void execute(LivingEntity caster, LivingEntity target){

        abilityManager.setCasting(caster, true);
        int castTime = 20;

        if(getMoveCast(caster)){
            unQueueMoveCast(caster);
        }
        else{
            buffAndDebuffManager.getImmobile().applyImmobile(caster, castTime);
        }

        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone();
            int ran = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();
                        abilityManager.setCasting(caster, false);
                        buffAndDebuffManager.getImmobile().removeImmobile(caster);
                    }
                }

                if(buffAndDebuffManager.getIfInterrupt(caster)){
                    this.cancel();
                    abilityManager.setCasting(caster, false);
                    buffAndDebuffManager.getImmobile().removeImmobile(caster);
                    return;
                }

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation();
                    targetLoc = targetLoc.subtract(0,1,0);
                    targetWasLoc = targetLoc.clone();
                }

                double distanceToTarget = caster.getLocation().distance(targetWasLoc);

                if(distanceToTarget>getRange(caster)){
                    this.cancel();
                    abilityManager.setCasting(caster, false);
                    buffAndDebuffManager.getImmobile().removeImmobile(caster);
                    return;
                }

                double percent = ((double) ran / castTime) * 100;
                abilityManager.setCastBar(caster, percent);

                if(ran >= castTime){
                    this.cancel();
                    abilityManager.setCasting(caster, false);
                    healTarget(target);
                    buffAndDebuffManager.getImmobile().removeImmobile(caster);
                }

                ran++;
            }

            private void healTarget(LivingEntity target){



                boolean crit = damageCalculator.checkIfCrit(caster, 0);

                double healAmount = damageCalculator.calculateHealing(caster, getHealPower(caster), crit);

                if(justiceMark.markProc(caster, target)){
                    markHealInstead(caster, healAmount);
                    unQueueMoveCast(caster);
                    return;
                }

                changeResourceHandler.addHealthToEntity(target, healAmount, caster);
                unQueueMoveCast(caster);

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

    private void markHealInstead(LivingEntity caster, double healAmount){

        List<LivingEntity> affected = justiceMark.getMarkedTargets(caster);

        for(LivingEntity thisPlayer : affected){
            changeResourceHandler.addHealthToEntity(thisPlayer, healAmount, caster);

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

    public void queueMoveCast(LivingEntity caster){

        if(caster instanceof Player){
            Player player = (Player) caster;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Status));
        }

        moveCast.put(caster.getUniqueId(), true);
    }
    public void unQueueMoveCast(LivingEntity caster){
        if(caster instanceof Player){
            Player player = (Player) caster;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Status));
        }
        moveCast.remove(caster.getUniqueId());
    }
    private boolean getMoveCast(LivingEntity caster){
        return moveCast.getOrDefault(caster.getUniqueId(), false);
    }

    public int getCooldown(LivingEntity caster){

        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public double getHealPower(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_2_Level_Bonus();
        double damage = 10 + ((int)(skillLevel/3));

        if(purity.active(caster)){
            damage = damage * 3;
            purity.reset(caster);
        }

        return damage;
    }

    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

    public boolean usable(LivingEntity caster, LivingEntity target){
        if(target != null){

            if(!(target instanceof Player)){

                if(pveChecker.pveLogic(target)){
                    target = caster;
                }
            }

            double distance = caster.getLocation().distance(target.getLocation());

            if(distance > getRange(caster)){
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


        return getCooldown(caster) <= 0;
    }

}
