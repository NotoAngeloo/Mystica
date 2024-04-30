package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.Components.Abilities.MysticAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.CustomEvents.StatusUpdateEvent;
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

import java.util.*;

public class PurifyingBlast {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final AbilityManager abilityManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    private final Consolation consolation;
    private final Map<UUID, Boolean> instantCast = new HashMap<>();

    public PurifyingBlast(Mystica main, AbilityManager manager, MysticAbilities mysticAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        abilityManager = manager;
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        consolation = mysticAbilities.getConsolation();
    }

    public void use(Player player){
        if (!abilityReadyInMap.containsKey(player.getUniqueId())) {
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if (getCooldown(player) > 0) {
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

        abilityReadyInMap.put(player.getUniqueId(), 12);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                if (getCooldown(player) <= 0) {
                    cooldownDisplayer.displayCooldown(player, 2);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(player) - 1;

                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 2);

            }
        }.runTaskTimer(main, 0, 20);
        cooldownTask.put(player.getUniqueId(), task);
    }

    private void execute(Player player){

        int castTime = 20;

        if(getInstantCast(player)){
            blastTask(player);
            return;
        }

        buffAndDebuffManager.getImmobile().applyImmobile(player, castTime);

        abilityManager.setCasting(player, true);

        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(!player.isOnline() || buffAndDebuffManager.getIfInterrupt(player)){
                    this.cancel();
                    abilityManager.setCasting(player, false);
                    return;
                }

                double percent = ((double) count / castTime) * 100;

                abilityManager.setCastBar(player, percent);

                if(count >= castTime){
                    this.cancel();
                    abilityManager.setCasting(player, false);
                    blastTask(player);
                }

                count++;
            }


        }.runTaskTimer(main, 0, 1);

    }

    private void blastTask(Player player){

        boolean arcane = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("arcane master");
        boolean shepard = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("shepard");

        if(getInstantCast(player)){
            unQueueInstantCast(player);
        }


        double healPower = getSkillDamage(player);

        if(shepard){
            healPower *= 1.5;
        }

        double skillDamage = getSkillDamage(player);

        if(arcane){
            skillDamage*=3;
        }

        Location center = player.getLocation().clone();

        Set<LivingEntity> hitBySkill = new HashSet<>();

        double finalSkillDamage = skillDamage;
        double finalHealPower = healPower;
        new BukkitRunnable(){
            double progress = 0;
            final int maxDistance = 10;
            @Override
            public void run(){

                BoundingBox hitBox = new BoundingBox(
                        center.getX() - progress,
                        center.getY() - 2,
                        center.getZ() - progress,
                        center.getX() + progress,
                        center.getY() + 4,
                        center.getZ() + progress
                );

                for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {


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
                    double damage = (damageCalculator.calculateDamage(player, livingEntity, "Magical", finalSkillDamage, crit));

                    //pvp logic
                    if(entity instanceof Player){
                        if(pvpManager.pvpLogic(player, (Player) entity)){
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                        }
                        else{
                            double healAmount  = damageCalculator.calculateHealing(player, finalHealPower, crit);
                            changeResourceHandler.addHealthToEntity(livingEntity, healAmount, player);
                            if(shepard){
                                consolation.apply(player, (Player) livingEntity);
                            }
                        }

                        continue;
                    }

                    if(pveChecker.pveLogic(livingEntity)){
                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, player));
                        changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                    }

                }

                //particles
                double radius = progress;
                double thisNumber = (Math.pow(2, progress));
                double increment = (2 * Math.PI) / thisNumber;

                for (double i = 0; i < thisNumber; i++) {
                    double angle = i * increment;
                    double x = center.getX() + (radius * Math.cos(angle));
                    double z = center.getZ() + (radius * Math.sin(angle));
                    Location loc = new Location(player.getWorld(), x, center.getY(), z);
                    player.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1, 0, 0, 0, 0);
                }


                progress += .6;

                if(progress >= maxDistance){
                    this.cancel();
                }


            }
        }.runTaskTimer(main, 0, 1);
    }

    public void queueInstantCast(Player player){
        instantCast.put(player.getUniqueId(), true);
        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
    }
    public void unQueueInstantCast(Player player){
        instantCast.remove(player.getUniqueId());
        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));}


    public boolean getInstantCast(Player player){
        return instantCast.getOrDefault(player.getUniqueId(), false);
    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public double getCost(){
        return 10;
    }

    public double getSkillDamage(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_2_Level_Bonus();

        return 15 + ((int)(skillLevel/3));
    }

    public void resetCooldown(Player player){
        abilityReadyInMap.remove(player.getUniqueId());
    }
}
