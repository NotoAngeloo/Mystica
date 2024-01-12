package me.angeloo.mystica.Components.Abilities.Warrior;

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
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnvilDrop {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final TargetManager targetManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public AnvilDrop(Mystica main, AbilityManager manager){
        this.main = main;
        targetManager = main.getTargetManager();
        profileManager = main.getProfileManager();
        abilityManager = manager;
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }


        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        combatManager.startCombatTimer(player);

        execute(player);

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
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 5);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        double baseRange = 15;

        targetManager.setTargetToNearestValid(player, baseRange);

        LivingEntity target = targetManager.getPlayerTarget(player);

        boolean targeted = false;

        Vector direction = player.getLocation().getDirection().setY(0).normalize();

        if(target != null){

            if(target instanceof Player){
                if(pvpManager.pvpLogic(player, (Player) target)){

                    double distance = player.getLocation().distance(target.getLocation());

                    if(distance < baseRange){
                        targeted = true;
                    }

                }
            }

            if(!(target instanceof Player)){
                if(pveChecker.pveLogic(target)){

                    double distance = player.getLocation().distance(target.getLocation());

                    if(distance < baseRange){
                        targeted = true;
                    }

                }
            }


        }

        if(targeted){
            direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).setY(0).normalize();
        }

        Location start = player.getLocation().clone();
        //Location end = start.clone().add(direction.multiply(baseRange));
        Location end = start.clone();

        while (baseRange > 0) {
            end.add(direction);
            if (!end.getBlock().isPassable()) {
                end.subtract(direction.multiply(2));
                break;
            }
            baseRange -= 1;
        }

        if(targeted){
            end = target.getLocation().clone();
        }

        //if distance too short no leap
        double distance = start.distance(end);

        if(distance<5){
            knockUp(player);
            return;
        }

        abilityManager.setSkillRunning(player, true);
        Location finalEnd = end;
        new BukkitRunnable(){
            final double length = start.distance(finalEnd);
            final double half = length/2;
            double traveled = 0;
            @Override
            public void run(){

                if(!player.isOnline() || profileManager.getAnyProfile(player).getIfDead()){
                    this.cancel();
                    abilityManager.setSkillRunning(player, false);
                    return;
                }

                Location current = player.getLocation();
                double distance = current.distance(finalEnd);
                double distanceThisTick = Math.min(distance, 1);

                Vector direction = finalEnd.toVector().subtract(current.toVector());

                current.add(direction.normalize().multiply(distanceThisTick));

                traveled = traveled + distanceThisTick;

                if(traveled<half){
                    current.add(0,distanceThisTick,0);
                }

                player.teleport(current);

                if(distance<=1){
                    this.cancel();
                    knockUp(player);
                    abilityManager.setSkillRunning(player, false);
                }
            }
        }.runTaskTimer(main, 0, 1);
    }

    private void knockUp(Player player){

        boolean executioner = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("executioner");

        double skillDamage = 5;
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_5_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_5_Level_Bonus();
        skillDamage = skillDamage + ((int)(skillLevel/10));

        if(executioner){
            skillDamage = skillLevel * 2;
        }


        BoundingBox hitBox = new BoundingBox(
                player.getLocation().getX() - 4,
                player.getLocation().getY() - 2,
                player.getLocation().getZ() - 4,
                player.getLocation().getX() + 4,
                player.getLocation().getY() + 4,
                player.getLocation().getZ() + 4
        );

        double increment = (2 * Math.PI) / 16; // angle between particles

        for (int i = 0; i < 16; i++) {
            double angle = i * increment;
            double x = player.getLocation().getX() + (4 * Math.cos(angle));
            double y = player.getLocation().getY() + 1;
            double z = player.getLocation().getZ() + (4 * Math.sin(angle));
            Location loc = new Location(player.getWorld(), x, y, z);
            player.getWorld().spawnParticle(Particle.CRIT, loc, 1,0, 0, 0, 0);
        }

        LivingEntity targetToHit = null;
        LivingEntity target = targetManager.getPlayerTarget(player);
        LivingEntity firstHit = null;
        boolean targetHit = false;

        for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {

            if(entity == player){
                continue;
            }

            if(entity.isDead()){
                continue;
            }

            if(!(entity instanceof LivingEntity)){
                continue;
            }

            if(entity instanceof Player){
                if(!pvpManager.pvpLogic(player, (Player) entity)){
                    continue;
                }
            }

            if(entity instanceof ArmorStand){
                continue;
            }

            LivingEntity livingEntity = (LivingEntity) entity;

            if(!(entity instanceof Player)){
                if(!pveChecker.pveLogic(livingEntity)){
                    continue;
                }
            }

            if(firstHit == null){
                firstHit = livingEntity;
            }

            if(target != null){
                if(livingEntity == target){
                    targetHit = true;
                    targetToHit = livingEntity;
                    break;
                }
            }
        }

        if(!targetHit && firstHit!= null){
            targetToHit = firstHit;
        }

        if(targetToHit != null){
            targetManager.setPlayerTarget(player, targetToHit);
            Location playerLoc = player.getLocation().clone();
            Vector targetDir = targetToHit.getLocation().toVector().subtract(playerLoc.toVector());
            playerLoc.setDirection(targetDir);
            player.teleport(playerLoc);

            int bonus = 0;
            if(executioner){
                bonus = 15;
            }

            boolean crit = damageCalculator.checkIfCrit(player, bonus);
            double damage = damageCalculator.calculateDamage(player, targetToHit, "Physical", skillDamage, crit);

            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(targetToHit, player));
            changeResourceHandler.subtractHealthFromEntity(targetToHit, damage, player);

            //also knockup
            if(profileManager.getAnyProfile(targetToHit).getIsMovable()){
                Vector velocity = (new Vector(0, .75, 0));
                targetToHit.setVelocity(velocity);
                buffAndDebuffManager.getKnockUp().applyKnockUp(targetToHit);
            }
        }
    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
