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

    private final Mana mana;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public ForceOfWill(Mystica main, AbilityManager manager, MysticAbilities mysticAbilities){
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
        mana = mysticAbilities.getMana();
    }

    public void use(LivingEntity caster){
        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }


        boolean shepard = profileManager.getAnyProfile(caster).getPlayerSubclass().equalsIgnoreCase("shepard");

        if(shepard){
            LivingEntity target = targetManager.getPlayerTarget(caster);

            if(target instanceof Player){
                if(!pvpManager.pvpLogic(caster, (Player) target)){
                    //do something else

                    if(!usable(caster, target)){
                        return;
                    }

                    combatManager.startCombatTimer(caster);

                    mana.subTractManaFromEntity(caster, getCost());

                    passThroughDamage(caster, target);
                    return;
                }
            }

            if(!(target instanceof Player)){
                if(!pveChecker.pveLogic(target)){

                    if(!usable(caster, target)){
                        return;
                    }

                    mana.subTractManaFromEntity(caster, getCost());

                    combatManager.startCombatTimer(caster);

                    passThroughDamage(caster, target);
                    return;
                }
            }


        }

        targetManager.setTargetToNearestValid(caster, getRange(caster));
        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }



        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 15);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 3);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 3);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);
    }

    private void passThroughDamage(LivingEntity caster, LivingEntity target){

        abilityManager.setCasting(caster, true);
        double castTime = 4;
        castTime = castTime - buffAndDebuffManager.getHaste().getHasteLevel(caster);
        castTime = castTime * 20;

        double shield = profileManager.getAnyProfile(target).getTotalHealth() * .25;
        buffAndDebuffManager.getGenericShield().applyOrAddShield(caster, shield);
        buffAndDebuffManager.getPassThrough().applyPassThrough(caster, target);

        double finalCastTime = castTime;
        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone();
            int count = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();
                        abilityManager.setCasting(caster, false);
                        abilityManager.setCastBar((Player) caster, 0);
                        buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(caster, shield);
                        buffAndDebuffManager.getPassThrough().removePassThrough(target);
                        return;
                    }
                }

                if(buffAndDebuffManager.getIfInterrupt(caster)){
                    this.cancel();
                    abilityManager.setCasting(caster, false);
                    if(caster instanceof Player){
                        abilityManager.setCastBar((Player) caster, 0);
                    }

                    buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(caster, shield);
                    buffAndDebuffManager.getPassThrough().removePassThrough(target);
                    return;
                }

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation();
                    targetWasLoc = targetLoc.clone();
                }

                Location start = caster.getLocation().clone();

                double distanceToTarget = start.distance(targetWasLoc);

                if(distanceToTarget>getRange(caster)){
                    this.cancel();
                    abilityManager.setCasting(caster, false);
                    if(caster instanceof Player){
                        abilityManager.setCastBar((Player) caster, 0);
                    }

                    buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(caster, shield);
                    buffAndDebuffManager.getPassThrough().removePassThrough(target);
                    return;
                }

                if (!sameWorld(start, targetWasLoc)) {
                    this.cancel();
                    abilityManager.setCasting(caster, false);

                    if(caster instanceof Player){
                        abilityManager.setCastBar((Player) caster, 0);
                    }


                    buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(caster, shield);
                    buffAndDebuffManager.getPassThrough().removePassThrough(target);
                    return;
                }

                Location current = caster.getLocation().clone().add(0,.5,0);

                for(double i = 0; i<distanceToTarget;i+=.75){

                    Vector direction = targetWasLoc.toVector().subtract(start.toVector());
                    double distanceThisTick = Math.min(distanceToTarget, .75);
                    current.add(direction.normalize().multiply(distanceThisTick));

                    caster.getWorld().spawnParticle(Particle.WAX_OFF, current, 1, 0, 0, 0, 0);

                }

                double percent = ((double) count / finalCastTime) * 100;

                if(caster instanceof Player){
                    abilityManager.setCastBar((Player) caster, percent);
                }



                if(count >= finalCastTime){
                    this.cancel();
                    abilityManager.setCasting(caster, false);

                    if(caster instanceof Player){
                        abilityManager.setCastBar((Player) caster, 0);
                    }


                    buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(caster, shield);
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

    private void execute(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        boolean arcane = profileManager.getAnyProfile(caster).getPlayerSubclass().equalsIgnoreCase("arcane master");

        abilityManager.setCasting(caster, true);
        double castTime = 4;
        castTime = castTime - buffAndDebuffManager.getHaste().getHasteLevel(caster);
        castTime = castTime * 20;

        double skillDamage = getSkillDamage(caster)/castTime;

        double finalCastTime = castTime;
        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone();
            int count = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();
                        abilityManager.setCasting(caster, false);
                        abilityManager.setCastBar((Player) caster, 0);
                        return;
                    }
                }

                if(buffAndDebuffManager.getIfInterrupt(caster)){
                    this.cancel();
                    abilityManager.setCasting(caster, false);

                    if(caster instanceof Player){
                        abilityManager.setCastBar(caster, 0);
                    }

                    return;
                }

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation();
                    targetWasLoc = targetLoc.clone();
                }

                Location start = caster.getLocation().clone();

                double distanceToTarget = start.distance(targetWasLoc);

                if(distanceToTarget>getRange(caster)){
                    this.cancel();
                    abilityManager.setCasting(caster, false);
                    abilityManager.setCastBar(caster, 0);
                    return;
                }

                if (!sameWorld(start, targetWasLoc)) {
                    this.cancel();
                    abilityManager.setCasting(caster, false);
                    abilityManager.setCastBar(caster, 0);
                    return;
                }

                Location current = caster.getLocation().clone().add(0,.5,0);

                for(double i = 0; i<distanceToTarget;i+=.75){

                    Vector direction = targetWasLoc.toVector().subtract(start.toVector());
                    double distanceThisTick = Math.min(distanceToTarget, .75);
                    current.add(direction.normalize().multiply(distanceThisTick));

                    caster.getWorld().spawnParticle(Particle.SPELL_WITCH, current, 1, 0, 0, 0, 0);

                }

                double percent = ((double) count / finalCastTime) * 100;

                abilityManager.setCastBar(caster, percent);

                if(count >= finalCastTime){
                    this.cancel();
                    abilityManager.setCasting(caster, false);
                    abilityManager.setCastBar(caster, 0);
                }

                boolean crit = damageCalculator.checkIfCrit(caster, 0);
                double damage = damageCalculator.calculateDamage(caster, target, "Magical", skillDamage, crit);
                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));

                if(target instanceof Player){
                    buffAndDebuffManager.getGenericShield().removeShields(target);
                }

                changeResourceHandler.subtractHealthFromEntity(target, damage, caster);

                if(arcane && crit){
                    double fifteenPercent = (double) profileManager.getAnyProfile(caster).getTotalAttack() * .15;
                    changeResourceHandler.subtractHealthFromEntity(target, fifteenPercent, caster);
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

    private double getRange(LivingEntity caster){
        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(caster);
        return baseRange + extraRange;
    }

    public int getCost(){
        return 50;
    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_3_Level_Bonus();
        return 35 + ((int)(skillLevel/3));
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
        double distance = caster.getLocation().distance(target.getLocation());

        if(distance > getRange(caster)){
            return false;
        }

        if(distance<1){
            return false;
        }

        if(getCooldown(caster) > 0){
            return false;
        }


        //check shepardlogic
        if(profileManager.getAnyProfile(caster).getPlayerSubclass().equalsIgnoreCase("shepard")){
            return mana.getCurrentMana(caster) >= getCost();
        }


        return true;
    }
}
