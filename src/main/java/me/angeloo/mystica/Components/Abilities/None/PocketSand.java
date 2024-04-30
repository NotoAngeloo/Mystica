package me.angeloo.mystica.Components.Abilities.None;

import me.angeloo.mystica.Components.Abilities.NoneAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PocketSand {

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
    private final Adrenaline adrenaline;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public PocketSand(Mystica main, AbilityManager manager, NoneAbilities noneAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        adrenaline = noneAbilities.getAdrenaline();
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }


        if(getCooldown(player) > 0){
            return;
        }

        double baseRange = 8;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        double totalRange = baseRange + extraRange;

        targetManager.setTargetToNearestValid(player, totalRange);

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

            if(distance > totalRange){
                return;
            }
        }

        if(target == null){
            return;
        }

        double cost = 10;

        if(profileManager.getAnyProfile(player).getCurrentMana()<cost){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, cost);

        combatManager.startCombatTimer(player);

        execute(player);

        if(cooldownTask.containsKey(player.getUniqueId())){
            cooldownTask.get(player.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(player.getUniqueId(), 20);
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

    private void execute(Player player){

        LivingEntity target = targetManager.getPlayerTarget(player);
        Location start = player.getLocation().clone().add(0,1,0);


        double skillDamage = 5;

        if(adrenaline.getIfBuffTime(player)>0){
            skillDamage = 15;
        }
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_3_Level_Bonus();

        skillDamage = skillDamage + ((int)(skillLevel/10));
        double finalSkillDamage = skillDamage;
        new BukkitRunnable(){
            final Location current = start.clone();
            @Override
            public void run(){


                if(!targetStillValid(target)){
                    this.cancel();
                    return;
                }

                Location targetLoc = target.getLocation().clone().add(0,1,0);


                Vector direction = targetLoc.toVector().subtract(current.toVector());

                double distance = current.distance(targetLoc);
                double distanceThisTick = Math.min(distance, 1.5);

                if(distanceThisTick!=0){
                    current.add(direction.normalize().multiply(distanceThisTick));
                }

                //spawn particles at current
                player.getWorld().spawnParticle(Particle.FALLING_DUST, current, 1, 0, 0, 0, 0, Material.SAND.createBlockData());

                if (distance <= 1) {
                    this.cancel();

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));

                    double increment = (2 * Math.PI) / 16; // angle between particles

                    for (int i = 0; i < 16; i++) {
                        double angle = i * increment;
                        double x = current.getX() + (1 * Math.cos(angle));
                        double y = current.clone().getY();
                        double z = current.getZ() + (1 * Math.sin(angle));
                        Location loc = new Location(start.getWorld(), x, y, z);
                        player.getWorld().spawnParticle(Particle.FALLING_DUST, loc, 1, 0, 0, 0, 0, Material.SAND.createBlockData());
                    }


                    //removes target for the player if hit
                    if(target instanceof Player){
                        targetManager.setPlayerTarget((Player) target, null);

                        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1));
                    }

                    boolean crit = damageCalculator.checkIfCrit(player, 0);
                    double damage = (damageCalculator.calculateDamage(player, target, "Physical", finalSkillDamage, crit));


                    changeResourceHandler.subtractHealthFromEntity(target, damage, player);

                }
            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){

                    if(!((Player) target).isOnline()){
                        return false;
                    }

                    if(profileManager.getAnyProfile(target).getIfDead()){
                        return false;
                    }

                }

                if(profileManager.getIfResetProcessing(target)){
                    return false;
                }

                return !target.isDead();
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

    public void resetCooldown(Player player){
        abilityReadyInMap.remove(player.getUniqueId());
    }

}
