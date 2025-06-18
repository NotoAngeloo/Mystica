package me.angeloo.mystica.Components.Abilities.Paladin;

import me.angeloo.mystica.Components.Abilities.PaladinAbilities;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.Hud.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpiritualGift {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final DamageCalculator damageCalculator;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final Purity purity;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public SpiritualGift(Mystica main, AbilityManager manager, PaladinAbilities paladinAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        damageCalculator = main.getDamageCalculator();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        purity = paladinAbilities.getPurity();
    }

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        if(target == null){
            target = caster;
        }

        if (profileManager.getAnyProfile(target).getIfDead()) {
            target = caster;
        }

        if(target instanceof Player){
            if(pvpManager.pvpLogic(caster, (Player) target)){
                target = caster;
            }
        }


        combatManager.startCombatTimer(caster);

        execute(caster, target);
        purity.skillListAdd(caster, 5);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 20);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 5);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 5);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }


    private double getRange(LivingEntity caster){
        double baseRange = 12;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(caster);
        return baseRange + extraRange;
    }

    private void execute(LivingEntity caster, LivingEntity target){

        //every 15 levels is a +1


        buffAndDebuffManager.getHaste().applyHaste(target, 3, getDuration(caster));

        double finalHealPower = getHealPower(caster);
        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(!targetStillValid(target)){
                    this.cancel();
                    return;
                }

                Location center = target.getLocation().clone().add(0,1,0);

                double increment = (2 * Math.PI) / 16; // angle between particles

                for (int i = 0; i < 16; i++) {
                    double angle = i * increment;
                    double x = center.getX() + (1 * Math.cos(angle));
                    double z = center.getZ() + (1 * Math.sin(angle));
                    Location loc = new Location(center.getWorld(), x, (center.getY()), z);

                    target.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
                }

                if(count>=getDuration(caster)){
                    this.cancel();
                    healTarget(target);
                }

                count++;
            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){
                    if(!((Player) target).isOnline()){
                        return false;
                    }
                }
                return !target.isDead();
            }

            private void healTarget(LivingEntity target){

                if(!targetStillValid(target)){
                    return;
                }

                boolean crit = damageCalculator.checkIfCrit(caster, 0);
                double healAmount = damageCalculator.calculateHealing(caster, finalHealPower, crit);

                changeResourceHandler.addHealthToEntity(target, healAmount, caster);
            }

        }.runTaskTimer(main, 0, 1);

    }


    public double getHealPower(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_5_Level_Bonus();
        return (purity.calculatePurityPercentDamage(caster, 5, 5)) +  ((int)(skillLevel/10));
    }

    public int getDuration(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_5_Level_Bonus();
        int bonusDuration = (int)(skillLevel/3);
        return  (5*20) + (bonusDuration*20);
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

            if(!(target instanceof Player)){

                if(pveChecker.pveLogic(target)){
                    target = caster;
                }

            }

            double distance = caster.getLocation().distance(target.getLocation());

            if(distance > getRange(caster)){
                return false;
            }
        }


        if(getCooldown(caster) > 0){
            return false;
        }

        return true;
    }


}
