package me.angeloo.mystica.Components.Abilities.Assassin;

import me.angeloo.mystica.Components.Abilities.AssassinAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Color;
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

public class Laceration {

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

    private final Stealth stealth;
    private final Combo combo;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public Laceration(Mystica main, AbilityManager manager, AssassinAbilities assassinAbilities){
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
        combo = assassinAbilities.getCombo();
        stealth = assassinAbilities.getStealth();
    }

    private final double range = 4;

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

        abilityReadyInMap.put(caster.getUniqueId(), 8);
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
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        boolean alchemist = profileManager.getAnyProfile(caster).getPlayerSubclass().equalsIgnoreCase("alchemist");

        LivingEntity target = targetManager.getPlayerTarget(caster);

        Location playerLoc = caster.getLocation().clone();
        Location targetLoc = target.getLocation();
        Vector targetDir = targetLoc.toVector().subtract(playerLoc.toVector());

        if(playerLoc!=targetLoc){
            Location warpLoc = targetLoc.add(targetDir.clone().normalize().multiply(-1.5));
            warpLoc.setDirection(targetDir);

            while (!warpLoc.getBlock().isPassable()){
                warpLoc.add(0,.1,0);
            }

            if(caster instanceof Player){
                if(((Player)caster).isSneaking()){
                    caster.teleport(warpLoc);
                }
            }



        }


        caster.getWorld().spawnParticle(Particle.REDSTONE, targetLoc, 50, .5, 1, .5, 1, new Particle.DustOptions(Color.RED, 1.0f));

        double bleedDamage = getBleedDamage(caster);

        if(alchemist){
            int comboPoints = combo.removeAnAmountOfPoints(caster, combo.getComboPoints(caster));
            bleedDamage = bleedDamage + comboPoints;
        }


        boolean crit = damageCalculator.checkIfCrit(caster, 0);
        double damage = damageCalculator.calculateDamage(caster, target, "Physical", getSkillDamage(caster), crit);
        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
        changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);
        stealth.stealthBonusCheck(caster, target);
        combo.addComboPoint(caster);

        double finalBleedDamage = bleedDamage;
        new BukkitRunnable(){
            int ticks = 0;
            @Override
            public void run(){

                if(profileManager.getIfResetProcessing(target)){
                    this.cancel();
                    return;
                }

                if(target.isDead()){
                    this.cancel();
                    return;
                }

                if(target instanceof Player){
                    if(!((Player)target).isOnline()){
                        this.cancel();
                        return;
                    }

                    if(profileManager.getAnyProfile(target).getIfDead()){
                        this.cancel();
                        return;
                    }
                }

                boolean crit = damageCalculator.checkIfCrit(caster, 0);
                double tickDamage = damageCalculator.calculateDamage(caster, target, "Physical", finalBleedDamage, crit);

                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                changeResourceHandler.subtractHealthFromEntity(target, tickDamage, caster,crit);

                ticks ++;

                if(ticks >= 10){
                    this.cancel();
                }

            }
        }.runTaskTimer(main, 20, 20);
    }

    public double getSkillDamage(LivingEntity caster){
        double level = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel())
                + profileManager.getAnyProfile(caster).getSkillLevels().getSkill_2_Level_Bonus();
        return 17 + ((int)(level/10));
    }

    public double getBleedDamage(LivingEntity caster){
        double level = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel())
                + profileManager.getAnyProfile(caster).getSkillLevels().getSkill_2_Level_Bonus();
        return 5 + ((int)(level/3));
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

            if(distance > range){
                return false;
            }
        }

        if(target == null){
            return false;
        }


        return getCooldown(caster) <= 0;
    }
}
