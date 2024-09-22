package me.angeloo.mystica.Components.Abilities.Paladin;

import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.BuffAndDebuffManager;
import me.angeloo.mystica.Managers.CombatManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageCalculator;
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

public class GloryOfPaladins {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final DamageCalculator damageCalculator;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();

    private final Map<UUID, Integer> buffActiveMap = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public GloryOfPaladins(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        damageCalculator = main.getDamageCalculator();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)){
            return;
        }

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 12);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 6);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;

                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 6);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        //increase max hp as well

        buffActiveMap.put(caster.getUniqueId(), 8);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(buffActiveMap.get(caster.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = buffActiveMap.get(caster.getUniqueId()) - 1;

                buffActiveMap.put(caster.getUniqueId(), cooldown);

            }
        }.runTaskTimer(main, 0,20);

        new BukkitRunnable(){
            double height = 0;
            boolean up = true;
            final double radius = 1;
            double angle = 0;
            Vector initialDirection;
            @Override
            public void run(){

                if(getIfBuffTime(caster) <= 0){
                    this.cancel();
                    return;
                }

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();
                        return;
                    }
                }



                Location loc = caster.getLocation();

                if(initialDirection == null) {
                    initialDirection = loc.getDirection().setY(0).normalize();
                    initialDirection.rotateAroundY(Math.toRadians(-45));
                }

                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);

                direction.rotateAroundY(radians);

                double x = loc.getX() + direction.getX() * radius;
                double z = loc.getZ() + direction.getZ() * radius;

                double x2 = loc.getX() - direction.getX() * radius;
                double z2 = loc.getZ() - direction.getZ() * radius;

                Location particleLoc = new Location(loc.getWorld(), x, loc.getY() + height, z);
                Location particleLoc2 = new Location(loc.getWorld(), x2, loc.getY() + height, z2);

                caster.getWorld().spawnParticle(Particle.WAX_OFF, particleLoc, 1, 0, 0, 0, 0);
                caster.getWorld().spawnParticle(Particle.WAX_OFF, particleLoc2, 1, 0, 0, 0, 0);

                if(up){
                    height += .1;
                }
                else{
                    height -= .1;
                }

                angle += 5;

                if(height >= 2){
                    up = false;
                }

                if(height < 0){
                    up = true;
                }

            }
        }.runTaskTimer(main, 0, 2);

    }

    public void procGlory(LivingEntity caster, LivingEntity livingEntity){

        if(getIfBuffTime(caster) <= 0){
            return;
        }


        boolean crit = damageCalculator.checkIfCrit(caster, 0);
        double damage = damageCalculator.calculateDamage(caster, livingEntity, "Physical", getSkillDamage(caster), crit);

        changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster);

        double healAmount = (profileManager.getAnyProfile(caster).getTotalHealth()+ buffAndDebuffManager.getHealthBuffAmount(caster)) * .05;
        //chance to restore
        int random = (int) (Math.random() * 100) + 1;
        if(random >= 25){
            changeResourceHandler.addHealthToEntity(caster, healAmount, caster);
        }
    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_6_Level_Bonus();
        return 20 + ((int)(skillLevel/3));
    }

    public int getIfBuffTime(LivingEntity caster){
        return buffActiveMap.getOrDefault(caster.getUniqueId(), 0);
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

    public boolean usable(LivingEntity caster){
        if(getCooldown(caster) > 0){
            return false;
        }

        return getIfBuffTime(caster) <= 0;
    }

}
