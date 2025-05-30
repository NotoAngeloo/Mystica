package me.angeloo.mystica.Components.Abilities.Assassin;

import me.angeloo.mystica.Components.Abilities.AssassinAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.CustomEvents.UltimateStatusChageEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.*;
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

public class DuelistsFrenzy {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;

    private final Stealth stealth;
    private final Combo combo;


    private final Map<UUID, Boolean> frenzy = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();
    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();

    public DuelistsFrenzy(Mystica main, AbilityManager manager, AssassinAbilities assassinAbilities){
        this.main = main;
        targetManager = main.getTargetManager();
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        combo = assassinAbilities.getCombo();
        stealth = assassinAbilities.getStealth();
    }

    private final double range = 7;

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }


        targetManager.setTargetToNearestValid(caster, range);
        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), getSkillCooldown());
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getPlayerCooldown(caster) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = getPlayerCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                if(caster instanceof Player){
                    Bukkit.getServer().getPluginManager().callEvent(new UltimateStatusChageEvent((Player) caster));
                }


            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);;


        combo.removeAnAmountOfPoints(caster, combo.getComboPoints(caster));

        //abilityManager.setSkillRunning(player, true);
        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            boolean up = true;
            Vector initialDirection;
            double height = 0;
            double radius = 5;
            double angle = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                        return;
                    }
                }

                if(buffAndDebuffManager.getIfInterrupt(caster)){
                    cancelTask();
                    return;
                }

                if(!targetStillValid(target)){
                    cancelTask();
                    return;
                }

                Location center = target.getLocation().clone();

                if(up){
                    if(initialDirection == null) {
                        initialDirection = center.getDirection().setY(0).normalize();
                        initialDirection.rotateAroundY(Math.toRadians(-45));
                    }

                    Vector direction = initialDirection.clone();
                    double radians = Math.toRadians(angle);
                    direction.rotateAroundY(radians);

                    double x = center.getX() + direction.getX() * radius;
                    double z = center.getZ() + direction.getZ() * radius;

                    Location current = new Location(center.getWorld(), x, center.getY() + height, z);
                    Vector dirToTarget = center.toVector().subtract(current.toVector());
                    current.setDirection(dirToTarget);
                    caster.teleport(current);
                    caster.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, current, 1, 0, 0, 0, 0);

                    if(height<5){
                        height+=.2;
                    }
                    else{
                        up = false;
                    }

                    radius-=.2;
                    angle+=20;
                }
                else{

                    Location current = caster.getLocation().clone();

                    double distance = current.distance(center);
                    double distanceThisTick = Math.min(distance, .5);
                    Vector downDir = center.toVector().subtract(current.toVector());

                    if(distanceThisTick!=0){
                        current.add(downDir.normalize().multiply(distanceThisTick));
                    }

                    center.setDirection(downDir);

                    caster.teleport(current);

                    if(distance<=1){

                        double increment = (2 * Math.PI) / 16; // angle between particles

                        for (int i = 0; i < 16; i++) {
                            double angle = i * increment;
                            double x = current.getX() + (2 * Math.cos(angle));
                            double y = current.getY() + 1;
                            double z = current.getZ() + (2 * Math.sin(angle));
                            Location loc = new Location(current.getWorld(), x, y, z);
                            caster.getWorld().spawnParticle(Particle.CRIT_MAGIC, loc, 1,0, 0, 0, 0);
                        }

                        //also damage
                        boolean crit = damageCalculator.checkIfCrit(caster, 0);
                        double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);

                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                        changeResourceHandler.subtractHealthFromEntity(target, damage, caster);
                        stealth.stealthBonusCheck(caster, target);
                        applyFrenzy(caster);
                        cancelTask();
                    }
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

            private void cancelTask(){
                this.cancel();
                //abilityManager.setSkillRunning(player, false);
            }

        }.runTaskTimer(main, 0, 1);

    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getStats().getLevel();
        return 150 + ((int)(skillLevel/3));
    }

    public int getSkillCooldown(){
        return 30;
    }

    private void applyFrenzy(LivingEntity caster){

        frenzy.put(caster.getUniqueId(), true);

        new BukkitRunnable(){
            @Override
            public void run(){
                removeFrenzy(caster);
            }
        }.runTaskLater(main, 20*15);

    }

    public void removeFrenzy(LivingEntity caster){
        frenzy.remove(caster.getUniqueId());
    }

    public boolean getFrenzy(LivingEntity caster){
        return frenzy.getOrDefault(caster.getUniqueId(),false);
    }

    public int getPlayerCooldown(LivingEntity caster){
        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public int returnWhichItem(Player player){

        if(combo.getComboPoints(player) != 6){
            return 1;
        }

        return 0;
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

            if(distance > range){
                return false;
            }
        }

        if(target == null){
            return false;
        }


        if(getPlayerCooldown(caster) > 0){
            return false;
        }

        return combo.getComboPoints(caster) >= 5;
    }

}
