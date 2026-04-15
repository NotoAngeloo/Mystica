package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Mystic;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.MysticAbilities;
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

import java.util.List;

public class Enlightenment {

    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final CooldownManager cooldownManager;

    private final Mana mana;
    private final Consolation consolation;

    public Enlightenment(Mystica main, MysticAbilities mysticAbilities, AbilityManager manager){
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        mana = mysticAbilities.getMana();
        consolation = mysticAbilities.getConsolation();
        cooldownManager = manager.getCooldownManager();
    }

    private final int abilityNumber = -1;
    private final int baseCooldown = 5;
    private final int cost = 50;
    private final int healPercent = 10;

    public void use(LivingEntity caster){



        if(!usable(caster)){
            return;
        }

        mana.subTractManaFromEntity(caster, cost);


        execute(caster);

        cooldownManager.start(caster.getUniqueId(), abilityNumber, (long) (baseCooldown * 1000));
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
                //purifying blast
                cooldownManager.clear(caster.getUniqueId(), 2);
            }

            changeResourceHandler.addHealthToEntity(target, healAmount, caster);

            statusEffectManager.applyEffect(target, new GenericDamageReduction(), 20*10, 0.6);

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
        return healPercent + ((int)(skillLevel/3));
    }



    public boolean usable(LivingEntity caster){
        if (mana.getCurrentMana(caster)<cost) {
            return false;
        }

        return cooldownManager.isReady(caster.getUniqueId(), abilityNumber, statusEffectManager.getHastePercent(caster));
    }

    /*public int returnWhichItem(Player player){

        if(mana.getCurrentMana(player)<getCost()){
            return 6;
        }

        return 0;
    }*/

}
