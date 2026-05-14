package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Mystic;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityMarkManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers.GenericDamageReduction;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;

import java.util.Set;

public class Enlightenment extends BaseAbility {

    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final CooldownManager cooldownManager;
    private final AbilityMarkManager abilityMarkManager;

    private final Mana mana;

    public Enlightenment(Mystica main,AbilityManager manager){
        super("enlightenment");
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        mana = manager.getMana();
        cooldownManager = main.getCooldownManager();
        abilityMarkManager = manager.getAbilityMarkManager();
    }

    private final int baseCooldown = 5;
    private final int cost = 50;
    private final int healPercent = 10;

    @Override
    public boolean use(LivingEntity caster){



        if(!usable(caster)){
            return false;
        }

        mana.subTractManaFromEntity(caster, cost);


        execute(caster);

        cooldownManager.start(caster.getUniqueId(), -1, (long) (baseCooldown * 1000));
        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        Set<LivingEntity> targets = abilityMarkManager.getTargets(caster);
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
                //purifying blast
                cooldownManager.clear(caster.getUniqueId(), 2);
            }

            changeResourceHandler.addHealthToEntity(target, healAmount, caster, crit);

            statusEffectManager.applyEffect(target, new GenericDamageReduction(), 20*10, 0.6, caster);

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

        abilityMarkManager.removeTargets(caster);
    }

    public double getHealPercent(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getStats().getLevel();
        return healPercent + ((int)(skillLevel/3));
    }



    @Override
    public boolean usable(LivingEntity caster){
        if (mana.getCurrentMana(caster)<cost) {
            return false;
        }

        return cooldownManager.isReady(caster.getUniqueId(), -1, statusEffectManager.getHastePercent(caster));
    }

    @Override
    public String skillBarIcon(LivingEntity entity) {

        if (mana.getCurrentMana(entity)<cost) {
            return "\ue3d8";
        }

        return "\ue3d7";
    }
}
