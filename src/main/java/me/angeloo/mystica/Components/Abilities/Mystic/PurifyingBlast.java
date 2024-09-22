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

    private final Mana mana;
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
        mana = mysticAbilities.getMana();
        consolation = mysticAbilities.getConsolation();
    }

    public void use(LivingEntity caster){
        if (!abilityReadyInMap.containsKey(caster.getUniqueId())) {
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if (getCooldown(caster) > 0) {
            return;
        }


        if(!usable(caster)){
            return;
        }

        mana.subTractManaFromEntity(caster, getCost());

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 12);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                if (getCooldown(caster) <= 0) {
                    cooldownDisplayer.displayCooldown(caster, 2);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;

                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 2);

            }
        }.runTaskTimer(main, 0, 20);
        cooldownTask.put(caster.getUniqueId(), task);
    }

    private void execute(LivingEntity caster){

        int castTime = 20;

        if(getInstantCast(caster)){
            blastTask(caster);
            return;
        }

        buffAndDebuffManager.getImmobile().applyImmobile(caster, castTime);

        abilityManager.setCasting(caster, true);

        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();
                        abilityManager.setCasting(caster, false);
                        return;
                    }
                }

                if(buffAndDebuffManager.getIfInterrupt(caster)){
                    this.cancel();
                    abilityManager.setCasting(caster, false);
                    return;
                }

                double percent = ((double) count / castTime) * 100;

                if(caster instanceof Player){
                    abilityManager.setCastBar((Player) caster, percent);
                }


                if(count >= castTime){
                    this.cancel();
                    abilityManager.setCasting(caster, false);
                    blastTask(caster);
                }

                count++;
            }


        }.runTaskTimer(main, 0, 1);

    }

    private void blastTask(LivingEntity caster){

        boolean arcane = profileManager.getAnyProfile(caster).getPlayerSubclass().equalsIgnoreCase("arcane master");
        boolean shepard = profileManager.getAnyProfile(caster).getPlayerSubclass().equalsIgnoreCase("shepard");

        if(getInstantCast(caster)){
            unQueueInstantCast(caster);
        }


        double healPower = getSkillDamage(caster);

        if(shepard){
            healPower *= 1.5;
        }

        double skillDamage = getSkillDamage(caster);

        if(arcane){
            skillDamage*=3;
        }

        Location center = caster.getLocation().clone();

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

                for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {


                    if(!(entity instanceof LivingEntity)){
                        continue;
                    }

                    if(entity instanceof ArmorStand){
                        continue;
                    }

                    LivingEntity livingEntity = (LivingEntity) entity;

                    if(profileManager.getAnyProfile(livingEntity).getIfObject()){
                        continue;
                    }

                    if(hitBySkill.contains(livingEntity)){
                        continue;
                    }

                    hitBySkill.add(livingEntity);

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = (damageCalculator.calculateDamage(caster, livingEntity, "Magical", finalSkillDamage, crit));

                    //pvp logic
                    if(entity instanceof Player){
                        if(pvpManager.pvpLogic(caster, (Player) entity)){
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster);
                        }
                        else{
                            double healAmount  = damageCalculator.calculateHealing(caster, finalHealPower, crit);
                            changeResourceHandler.addHealthToEntity(livingEntity, healAmount, caster);
                            if(shepard){
                                consolation.apply(caster, livingEntity);
                            }
                        }

                        continue;
                    }

                    if(pveChecker.pveLogic(livingEntity)){
                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, caster));
                        changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster);
                    }
                    else{
                        double healAmount  = damageCalculator.calculateHealing(caster, finalHealPower, crit);
                        changeResourceHandler.addHealthToEntity(livingEntity, healAmount, caster);
                        if(shepard){
                            consolation.apply(caster, livingEntity);
                        }
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
                    Location loc = new Location(caster.getWorld(), x, center.getY(), z);
                    caster.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1, 0, 0, 0, 0);
                }


                progress += .6;

                if(progress >= maxDistance){
                    this.cancel();
                }


            }
        }.runTaskTimer(main, 0, 1);
    }

    public void queueInstantCast(LivingEntity caster){
        instantCast.put(caster.getUniqueId(), true);

        if(caster instanceof Player){
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent((Player) caster));
        }


    }
    public void unQueueInstantCast(LivingEntity caster){
        instantCast.remove(caster.getUniqueId());

        if(caster instanceof Player){
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent((Player) caster));

        }

    }


    public boolean getInstantCast(LivingEntity caster){
        return instantCast.getOrDefault(caster.getUniqueId(), false);
    }

    public int getCooldown(LivingEntity caster){
        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public int getCost(){
        return 10;
    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_2_Level_Bonus();

        return 15 + ((int)(skillLevel/3));
    }

    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

    public boolean usable(LivingEntity caster){
        if (getCooldown(caster) > 0) {
            return false;
        }

        if(mana.getCurrentMana(caster)<getCost()){
            return false;
        }

        return true;
    }
}
