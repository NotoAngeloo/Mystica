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
import org.bukkit.scheduler.BukkitTask;
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

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
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


        boolean shepard = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("shepard");

        if(shepard){
            LivingEntity target = targetManager.getPlayerTarget(player);

            if(target instanceof Player){
                if(!pvpManager.pvpLogic(player, (Player) target)){
                    //do something else

                    double distance = player.getLocation().distance(target.getLocation());

                    if(distance > totalRange){
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

                    passThroughDamage(player, target);
                    return;
                }
            }

            if(!(target instanceof Player)){
                if(!pveChecker.pveLogic(target)){

                    double distance = player.getLocation().distance(target.getLocation());

                    if(distance > totalRange){
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

                    passThroughDamage(player, target);
                    return;
                }
            }


        }


        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);

        execute(player);

        if(cooldownTask.containsKey(player.getUniqueId())){
            cooldownTask.get(player.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(player.getUniqueId(), 15);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(player) <= 0){
                    cooldownDisplayer.displayCooldown(player, 3);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(player) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 3);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(player.getUniqueId(), task);
    }

    private void passThroughDamage(Player player, LivingEntity target){

        abilityManager.setCasting(player, true);
        double castTime = 4;
        castTime = castTime - buffAndDebuffManager.getHaste().getHasteLevel(player);
        castTime = castTime * 20;

        double shield = profileManager.getAnyProfile(target).getTotalHealth() * .25;
        buffAndDebuffManager.getGenericShield().applyOrAddShield(player, shield);
        buffAndDebuffManager.getPassThrough().applyPassThrough(player, target);

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
                    buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(player, shield);
                    buffAndDebuffManager.getPassThrough().removePassThrough(target);
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
                    buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(player, shield);
                    buffAndDebuffManager.getPassThrough().removePassThrough(target);
                    return;
                }

                if (!sameWorld(start, targetWasLoc)) {
                    this.cancel();
                    abilityManager.setCasting(player, false);
                    abilityManager.setCastBar(player, 0);
                    buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(player, shield);
                    buffAndDebuffManager.getPassThrough().removePassThrough(target);
                    return;
                }

                Location current = player.getLocation().clone().add(0,.5,0);

                for(double i = 0; i<distanceToTarget;i+=.75){

                    Vector direction = targetWasLoc.toVector().subtract(start.toVector());
                    double distanceThisTick = Math.min(distanceToTarget, .75);
                    current.add(direction.normalize().multiply(distanceThisTick));

                    player.getWorld().spawnParticle(Particle.WAX_OFF, current, 1, 0, 0, 0, 0);

                }

                double percent = ((double) count / finalCastTime) * 100;

                abilityManager.setCastBar(player, percent);

                if(count >= finalCastTime){
                    this.cancel();
                    abilityManager.setCasting(player, false);
                    abilityManager.setCastBar(player, 0);
                    buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(player, shield);
                    buffAndDebuffManager.getPassThrough().removePassThrough(target);
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

        }.runTaskTimer(main, 0, 1);

    }

    private void execute(Player player){

        LivingEntity target = targetManager.getPlayerTarget(player);

        boolean arcane = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("arcane master");

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

                if(target instanceof Player){
                    buffAndDebuffManager.getGenericShield().removeShields(target);
                }

                changeResourceHandler.subtractHealthFromEntity(target, damage, player);

                if(arcane && crit){
                    double fifteenPercent = (double) profileManager.getAnyProfile(player).getTotalAttack() * .15;
                    changeResourceHandler.subtractHealthFromEntity(target, fifteenPercent, player);
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
        return 35 + ((int)(skillLevel/3));
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
