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

    private final double range = 8;

    private final double cost = 10;

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }


        if(getCooldown(caster) > 0){
            return;
        }


        targetManager.setTargetToNearestValid(caster, range + buffAndDebuffManager.getTotalRangeModifier(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }



        changeResourceHandler.subTractManaFromEntity(caster, cost);

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 20);
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

    private void execute(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);
        Location start = caster.getLocation().clone().add(0,1,0);


        double skillDamage = 5;

        if(adrenaline.getIfBuffTime(caster)>0){
            skillDamage = 15;
        }
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_3_Level_Bonus();

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
                caster.getWorld().spawnParticle(Particle.FALLING_DUST, current, 1, 0, 0, 0, 0, Material.SAND.createBlockData());

                if (distance <= 1) {
                    this.cancel();

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));

                    double increment = (2 * Math.PI) / 16; // angle between particles

                    for (int i = 0; i < 16; i++) {
                        double angle = i * increment;
                        double x = current.getX() + (1 * Math.cos(angle));
                        double y = current.clone().getY();
                        double z = current.getZ() + (1 * Math.sin(angle));
                        Location loc = new Location(start.getWorld(), x, y, z);
                        caster.getWorld().spawnParticle(Particle.FALLING_DUST, loc, 1, 0, 0, 0, 0, Material.SAND.createBlockData());
                    }


                    //removes target for the player if hit
                    if(target instanceof Player){
                        targetManager.setPlayerTarget((Player) target, null);

                        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1));
                    }

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = (damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit));


                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster);

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

            if(distance > range + buffAndDebuffManager.getTotalRangeModifier(caster)){
                return false;
            }
        }

        if(target == null){
            return false;
        }


        if(profileManager.getAnyProfile(caster).getCurrentMana()<cost){
            return false;
        }

        return true;
    }

}
