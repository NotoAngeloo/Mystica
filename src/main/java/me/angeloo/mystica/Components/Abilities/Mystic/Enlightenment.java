package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.Components.Abilities.MysticAbilities;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.ShieldAbilityManaDisplayer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Enlightenment {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final ShieldAbilityManaDisplayer shieldAbilityManaDisplayer;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    private final Consolation consolation;
    private final PurifyingBlast purifyingBlast;

    public Enlightenment(Mystica main, AbilityManager manager, MysticAbilities mysticAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        shieldAbilityManaDisplayer = new ShieldAbilityManaDisplayer(main, manager);
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        consolation = mysticAbilities.getConsolation();
        purifyingBlast = mysticAbilities.getPurifyingBlast();
    }

    public void use(LivingEntity caster){
        if (!abilityReadyInMap.containsKey(caster.getUniqueId())) {
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }


        if(!usable(caster)){
            return;
        }


        changeResourceHandler.subTractManaFromEntity(caster, getCost());

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 20);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                if (getCooldown(caster) <= 0) {
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;

                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);

                if(caster instanceof Player){
                    shieldAbilityManaDisplayer.displayPlayerHealthPlusInfo((Player) caster);
                }


            }
        }.runTaskTimer(main, 0, 20);
        cooldownTask.put(caster.getUniqueId(), task);
    }

    private void execute(LivingEntity caster){

        List<LivingEntity> targets = consolation.getTargets(caster);
        targets.add(caster);

        double increment = (2 * Math.PI) / 16; // angle between particles

        for (int i = 0; i < 16; i++) {
            double angle = i * increment;
            double x = caster.getLocation().getX() + (1 * Math.cos(angle));
            double z = caster.getLocation().getZ() + (1 * Math.sin(angle));
            Location loc = new Location(caster.getLocation().getWorld(), x, (caster.getLocation().getY()), z);

            caster.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
        }

        for (LivingEntity target : targets) {

            boolean crit = damageCalculator.checkIfCrit(caster, 0);

            double healAmount  = damageCalculator.calculateHealing(caster, getHealPercent(caster), crit);

            if(crit){
                purifyingBlast.resetCooldown(caster);
            }

            changeResourceHandler.addHealthToEntity(target, healAmount, caster);
            buffAndDebuffManager.getDamageReduction().applyDamageReduction(target, .6, 20*10);

            if(caster.getWorld() == target.getWorld()){
                Location start = caster.getLocation();
                Location end = target.getLocation();

                // Calculate the direction vector between the two locations
                double distance = start.distance(end);
                double incrementX = (end.getX() - start.getX()) / distance * 0.5;
                double incrementY = (end.getY() - start.getY()) / distance * 0.5;
                double incrementZ = (end.getZ() - start.getZ()) / distance * 0.5;


                // Iterate over the points between the start and end locations
                while (distance > 0) {
                    // Spawn particle at current location

                    for (int i = 0; i < 16; i++) {
                        double angle = i * increment;
                        double x = start.getX() + (1 * Math.cos(angle));
                        double z = start.getZ() + (1 * Math.sin(angle));
                        Location loc = new Location(start.getWorld(), x, (start.getY()), z);

                        caster.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
                    }


                    // Move to the next point
                    start.add(incrementX, incrementY, incrementZ);
                    distance -= 0.5;
                }
            }

        }

        consolation.removeTargets(caster);

    }

    public double getHealPercent(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getStats().getLevel();
        return 10 + ((int)(skillLevel/3));
    }

    public double getCost(){
        return 20;
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
        if (getCooldown(caster) > 0) {
            return false;
        }

        if(profileManager.getAnyProfile(caster).getCurrentMana()<getCost()){
            return false;
        }

        return true;
    }

}
