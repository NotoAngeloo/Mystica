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

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double baseRange = 4;

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


        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 8);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    cooldownDisplayer.displayCooldown(player, 2);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 2);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        boolean alchemist = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("alchemist");

        LivingEntity target = targetManager.getPlayerTarget(player);

        Location playerLoc = player.getLocation().clone();
        Location targetLoc = target.getLocation();
        Vector targetDir = targetLoc.toVector().subtract(playerLoc.toVector());

        Location warpLoc = targetLoc.add(targetDir.clone().normalize().multiply(-1.5));
        warpLoc.setDirection(targetDir);

        player.teleport(warpLoc);
        player.getWorld().spawnParticle(Particle.REDSTONE, targetLoc, 50, .5, 1, .5, 1, new Particle.DustOptions(Color.RED, 1.0f));

        double bleedDamage = 3;

        if(alchemist){
            int comboPoints = combo.removeAnAmountOfPoints(player, combo.getComboPoints(player));

            bleedDamage = bleedDamage + comboPoints;
        }

        double skillDamage = 7;
        double level = profileManager.getAnyProfile(player).getSkillLevels().getSkill_2_Level()
                + profileManager.getAnyProfile(player).getSkillLevels().getSkill_2_Level_Bonus();
        skillDamage = skillDamage + ((int)(level/10));
        bleedDamage = bleedDamage + ((int)(level/10));

        boolean crit = damageCalculator.checkIfCrit(player, 0);
        double damage = damageCalculator.calculateDamage(player, target, "Physical", skillDamage, crit);
        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
        changeResourceHandler.subtractHealthFromEntity(target, damage, player);
        stealth.stealthBonusCheck(player, target);
        combo.addComboPoint(player);

        double finalBleedDamage = bleedDamage;
        new BukkitRunnable(){
            int ticks = 0;
            @Override
            public void run(){

                if(target.isDead()){
                    this.cancel();
                    return;
                }

                if(profileManager.getAnyProfile(target).getIfDead()){
                    this.cancel();
                    return;
                }

                if(target instanceof Player){
                    if(!((Player)target).isOnline()){
                        this.cancel();
                        return;
                    }
                }

                boolean crit = damageCalculator.checkIfCrit(player, 0);
                double tickDamage = damageCalculator.calculateDamage(player, target, "Physical", finalBleedDamage, crit);

                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                changeResourceHandler.subtractHealthFromEntity(target, tickDamage, player);

                ticks ++;

                if(ticks >= 10){
                    this.cancel();
                }

            }
        }.runTaskTimer(main, 20, 20);
    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }
}
