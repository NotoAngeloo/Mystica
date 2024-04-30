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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class LavaQuake {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public LavaQuake(Mystica main, AbilityManager manager){
        this.main = main;
        targetManager = main.getTargetManager();
        profileManager = main.getProfileManager();
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


        if(getCooldown(player) > 0){
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

        abilityReadyInMap.put(player.getUniqueId(), 10);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(player) <= 0){
                    cooldownDisplayer.displayCooldown(player, 1);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(player) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 1);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(player.getUniqueId(), task);

    }

    private void execute(Player player){

        double baseRange = 8;

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


        Location current = player.getLocation().clone().add(direction.multiply(1));
        current.setDirection(direction);

        Location end = current.clone().add(direction.multiply(8));



        double maxHealth = profileManager.getAnyProfile(player).getTotalHealth();
        double shield = maxHealth * .1;

        buffAndDebuffManager.getGenericShield().applyOrAddShield(player, shield);

        Set<LivingEntity> hitBySkill = new HashSet<>();

        Vector finalDirection = direction;
        Vector crossSection = finalDirection.clone().crossProduct(new Vector(0,1,0)).normalize();
        double finalSkillDamage = getSkillDamage(player);
        new BukkitRunnable(){
            double offset = .1;
            @Override
            public void run(){

                BoundingBox hitBox = new BoundingBox(
                        current.getX() - 3,
                        current.getY() - 2,
                        current.getZ() - 3,
                        current.getX() + 3,
                        current.getY() + 6,
                        current.getZ() + 3
                );

                for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {

                    if(entity == player){
                        continue;
                    }

                    if(!(entity instanceof LivingEntity)){
                        continue;
                    }

                    if(entity instanceof ArmorStand){
                        continue;
                    }

                    LivingEntity livingEntity = (LivingEntity) entity;

                    if(hitBySkill.contains(livingEntity)){
                        continue;
                    }

                    hitBySkill.add(livingEntity);

                    boolean crit = damageCalculator.checkIfCrit(player, 0);
                    double damage = (damageCalculator.calculateDamage(player, livingEntity, "Physical", finalSkillDamage, crit));

                    //pvp logic
                    if(entity instanceof Player){
                        if(pvpManager.pvpLogic(player, (Player) entity)){
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                            buffAndDebuffManager.getGenericShield().removeShields(livingEntity);
                        }
                        continue;
                    }

                    if(pveChecker.pveLogic(livingEntity)){
                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, player));
                        changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                    }

                }

                player.getWorld().spawnParticle(Particle.LAVA, current, 3);
                player.getWorld().spawnParticle(Particle.LAVA, current.clone().add(crossSection.clone().multiply(offset)), 1);
                player.getWorld().spawnParticle(Particle.LAVA, current.clone().subtract(crossSection.clone().multiply(offset)), 1);

                double distance = current.distance(end);
                double distanceThisTick = Math.min(distance, .75);

                if(distanceThisTick!=0){
                    current.add(finalDirection.normalize().multiply(distanceThisTick));
                }

                if(distance <=1){
                    this.cancel();

                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(player, shield);
                        }
                    }.runTaskLater(main, 20*5);

                    return;
                }

                offset+=.3;
            }
        }.runTaskTimer(main, 0, 2);
    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public double getCost(){
        return 5;
    }

    public double getSkillDamage(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_1_Level_Bonus();
        return 25 + ((int)(skillLevel/3));
    }

    public void resetCooldown(Player player){
        abilityReadyInMap.remove(player.getUniqueId());
    }

}
