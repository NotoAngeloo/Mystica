package me.angeloo.mystica.Components.Abilities.Assassin;

import me.angeloo.mystica.Components.Abilities.AssassinAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
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
    private final ShieldAbilityManaDisplayer shieldAbilityManaDisplayer;

    private final Stealth stealth;
    private final Combo combo;

    private final Map<UUID, Boolean> frenzy = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();
    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();

    public DuelistsFrenzy(Mystica main, AbilityManager manager, AssassinAbilities assassinAbilities){
        this.main = main;
        shieldAbilityManaDisplayer = new ShieldAbilityManaDisplayer(main, manager);
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

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double baseRange = 7;

        targetManager.setTargetToNearestValid(player, baseRange);

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

            if(distance > baseRange){
                return;
            }
        }

        if(target == null){
            return;
        }


        if(getCooldown(player) > 0){
            return;
        }

        if(combo.getComboPoints(player) < 6){
            return;
        }

        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);

        execute(player);

        if(cooldownTask.containsKey(player.getUniqueId())){
            cooldownTask.get(player.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(player.getUniqueId(), 30);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(player) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(player) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                shieldAbilityManaDisplayer.displayPlayerHealthPlusInfo(player);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(player.getUniqueId(), task);

    }

    private void execute(Player player){

        LivingEntity target = targetManager.getPlayerTarget(player);

        combo.removeAnAmountOfPoints(player, combo.getComboPoints(player));

        //abilityManager.setSkillRunning(player, true);
        double finalSkillDamage = getSkillDamage(player);
        new BukkitRunnable(){
            boolean up = true;
            Vector initialDirection;
            double height = 0;
            double radius = 5;
            double angle = 0;
            @Override
            public void run(){

                if(!player.isOnline() || buffAndDebuffManager.getIfInterrupt(player)){
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
                    player.teleport(current);
                    player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, current, 1, 0, 0, 0, 0);

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

                    Location current = player.getLocation().clone();

                    double distance = current.distance(center);
                    double distanceThisTick = Math.min(distance, .5);
                    Vector downDir = center.toVector().subtract(current.toVector());

                    if(distanceThisTick!=0){
                        current.add(downDir.normalize().multiply(distanceThisTick));
                    }

                    center.setDirection(downDir);

                    player.teleport(current);

                    if(distance<=1){

                        double increment = (2 * Math.PI) / 16; // angle between particles

                        for (int i = 0; i < 16; i++) {
                            double angle = i * increment;
                            double x = current.getX() + (2 * Math.cos(angle));
                            double y = current.getY() + 1;
                            double z = current.getZ() + (2 * Math.sin(angle));
                            Location loc = new Location(current.getWorld(), x, y, z);
                            player.getWorld().spawnParticle(Particle.CRIT_MAGIC, loc, 1,0, 0, 0, 0);
                        }

                        //also damage
                        boolean crit = damageCalculator.checkIfCrit(player, 0);
                        double damage = damageCalculator.calculateDamage(player, target, "Physical", finalSkillDamage, crit);

                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                        changeResourceHandler.subtractHealthFromEntity(target, damage, player);
                        stealth.stealthBonusCheck(player, target);
                        applyFrenzy(player);
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

    public double getCost(){
        return 20;
    }

    public double getSkillDamage(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getStats().getLevel();
        return 150 + ((int)(skillLevel/3));
    }

    private void applyFrenzy(Player player){

        frenzy.put(player.getUniqueId(), true);

        new BukkitRunnable(){
            @Override
            public void run(){
                removeFrenzy(player);
            }
        }.runTaskLater(main, 20*15);

    }

    public void removeFrenzy(Player player){
        frenzy.remove(player.getUniqueId());
    }

    public boolean getFrenzy(Player player){
        return frenzy.getOrDefault(player.getUniqueId(),false);
    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

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

    public void resetCooldown(Player player){
        abilityReadyInMap.remove(player.getUniqueId());
    }

}
