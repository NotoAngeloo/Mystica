package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Mystic;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.DamageType;
import me.angeloo.mystica.Utility.Enums.SubClass;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ForceOfWill extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final PveChecker pveChecker;
    private final PvpManager pvpManager;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final AbilityManager abilityManager;
    private final CooldownManager cooldownManager;

    public ForceOfWill(Mystica main, AbilityManager manager){
        super("force_of_will");
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = manager;
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = main.getCooldownManager();
    }

    private final int baseCooldown = 15;
    private final int baseDamage = 35;
    private final double baseRange = 20;

    @Override
    public boolean use(LivingEntity caster){

        targetManager.setTargetToNearestValid(caster, getRange(caster));
        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return false;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 3, (long) (baseCooldown * 1000));
        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    /*private void passThroughDamage(LivingEntity caster, LivingEntity target){

        abilityManager.setCasting(caster, true);
        double castTime = 4;
        castTime = castTime - statusEffectManager.getHasteLevel(caster);
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
                        statusEffectManager.reduceShield(caster, shield);
                        buffAndDebuffManager.getPassThrough().removePassThrough(target);
                        return;
                    }
                }

                if(!statusEffectManager.canCast(caster)){
                    this.cancel();
                    abilityManager.setCasting(caster, false);
                    if(caster instanceof Player){
                        abilityManager.setCastBar((Player) caster, 0);
                    }

                    statusEffectManager.reduceShield(caster, shield);
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

                    statusEffectManager.reduceShield(caster, shield);
                    buffAndDebuffManager.getPassThrough().removePassThrough(target);
                    return;
                }

                if (!sameWorld(start, targetWasLoc)) {
                    this.cancel();
                    abilityManager.setCasting(caster, false);

                    if(caster instanceof Player){
                        abilityManager.setCastBar((Player) caster, 0);
                    }


                    statusEffectManager.reduceShield(caster, shield);
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


                    statusEffectManager.reduceShield(caster, shield);
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

    }*/

    //i should make this tick less often
    private void execute(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        boolean arcane = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Shepard);

        double castTime = 4;
        castTime = castTime - statusEffectManager.getHastePercent(caster);
        castTime = castTime * 20;

        double skillDamage = getSkillDamage(caster)/castTime;

        abilityManager.setSkillCurrentlyCasting(caster, statusBarIcon());

        double finalCastTime = castTime;
        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone();
            int count = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();
                        abilityManager.stopCasting(caster);
                        return;
                    }
                }

                if(!statusEffectManager.canCast(caster)){
                    this.cancel();
                    abilityManager.stopCasting(caster);

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
                    abilityManager.stopCasting(caster);
                    return;
                }

                if (!sameWorld(start, targetWasLoc)) {
                    this.cancel();
                    abilityManager.stopCasting(caster);
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
                    abilityManager.stopCasting(caster);
                }

                boolean crit = damageCalculator.checkIfCrit(caster, 0);
                double damage = damageCalculator.calculateDamage(caster, target, DamageType.Magical, skillDamage, crit, 0);
                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));

                if(target instanceof Player){
                    statusEffectManager.removeEffect(target, "shield");
                }

                changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);

                if(arcane && crit){
                    double fifteenPercent = (double) profileManager.getAnyProfile(caster).getTotalAttack() * .15;
                    changeResourceHandler.subtractHealthFromEntity(target, fifteenPercent, caster, crit);
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
        double extraRange = statusEffectManager.getAdditionalRange(caster);
        return baseRange + extraRange;
    }


    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_3_Level_Bonus();
        return baseDamage + ((int)(skillLevel/3));
    }


    @Override
    public boolean usable(LivingEntity caster, LivingEntity target){

        if(target == null){
            return false;
        }

        double distance = caster.getLocation().distance(target.getLocation());

        if(distance > getRange(caster)){
            return false;
        }

        if(distance<1){
            return false;
        }

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

        return cooldownManager.isReady(caster.getUniqueId(), 3, statusEffectManager.getHastePercent(caster));
    }

    @Override
    public String skillBarIcon(LivingEntity entity) {
        return "\ue3d9";
    }
}
